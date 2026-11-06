import Foundation
import SwiftUI
import Combine

final class PlannerViewModel: ObservableObject {
    @Published var rooms: [Room] = []
    @Published var tasks: [ChoreTask] = []
    @Published var preferences: AppPreferences = AppPreferences()
    
    @Published var toastMessage: String? = nil
    
    private let dataManager = DataManager.shared
    private let notificationManager = NotificationManager.shared
    private let languageManager = LanguageManager.shared
    
    var todayEpoch: Int64 {
        Int64(Date().timeIntervalSince1970 / 86400)
    }
    
    init() {
        loadData()
    }
    
    func loadData() {
        self.rooms = dataManager.loadRooms()
        self.tasks = dataManager.loadTasks()
        self.preferences = dataManager.loadPreferences()
        languageManager.setLanguage(self.preferences.appLanguage)
    }
    
    private func persist() {
        dataManager.saveRooms(rooms)
        dataManager.saveTasks(tasks)
        dataManager.savePreferences(preferences)
    }
    
    // MARK: - Queries
    var pendingTodayTasks: [ChoreTask] {
        tasks.filter { $0.dueDayEpoch <= todayEpoch && $0.completedDayEpoch != todayEpoch }
            .sorted { $0.dueDayEpoch < $1.dueDayEpoch }
    }
    
    var completedTodayTasks: [ChoreTask] {
        tasks.filter { $0.completedDayEpoch == todayEpoch }
    }
    
    var overdueTasks: [ChoreTask] {
        tasks.filter { $0.dueDayEpoch < todayEpoch && $0.completedDayEpoch != todayEpoch }
    }
    
    func tasksForRoom(_ roomId: UUID) -> [ChoreTask] {
        tasks.filter { $0.roomId == roomId }
    }
    
    func overdueCountForRoom(_ roomId: UUID) -> Int {
        tasks.filter { $0.roomId == roomId && $0.dueDayEpoch < todayEpoch && $0.completedDayEpoch != todayEpoch }.count
    }
    
    func roomName(for id: UUID) -> String {
        rooms.first(where: { $0.id == id })?.name ?? ""
    }
    
    // MARK: - Actions
    func toggleTaskComplete(_ task: ChoreTask) {
        guard let index = tasks.firstIndex(where: { $0.id == task.id }) else { return }
        var t = tasks[index]
        let isDone = (t.completedDayEpoch == todayEpoch)
        
        UIImpactFeedbackGenerator(style: .medium).impactOccurred()
        
        if isDone {
            // Undo completion
            t.completedDayEpoch = nil
            t.dueDayEpoch = t.previousDueDayEpoch ?? todayEpoch
            t.previousDueDayEpoch = nil
            showToast(loc("task_undo_msg"))
        } else {
            // Mark complete and schedule next
            t.completedDayEpoch = todayEpoch
            t.previousDueDayEpoch = t.dueDayEpoch
            t.dueDayEpoch = todayEpoch + Int64(t.intervalDays)
            showToast(loc("task_completed_msg"))
        }
        
        tasks[index] = t
        persist()
        updateReminderSchedule()
    }
    
    func addRoom(name: String) {
        let trimmed = name.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        let newRoom = Room(name: trimmed, sortOrder: rooms.count)
        rooms.append(newRoom)
        persist()
    }
    
    func updateRoom(id: UUID, name: String) {
        guard let index = rooms.firstIndex(where: { $0.id == id }) else { return }
        rooms[index].name = name.trimmingCharacters(in: .whitespacesAndNewlines)
        persist()
    }
    
    func deleteRoom(id: UUID) {
        rooms.removeAll(where: { $0.id == id })
        tasks.removeAll(where: { $0.roomId == id })
        persist()
        updateReminderSchedule()
    }
    
    func addTask(roomId: UUID, title: String, intervalDays: Int) {
        let trimmed = title.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        let newTask = ChoreTask(
            roomId: roomId,
            title: trimmed,
            intervalDays: max(1, intervalDays),
            dueDayEpoch: todayEpoch
        )
        tasks.append(newTask)
        persist()
        updateReminderSchedule()
    }
    
    func updateTask(id: UUID, roomId: UUID, title: String, intervalDays: Int) {
        guard let index = tasks.firstIndex(where: { $0.id == id }) else { return }
        tasks[index].roomId = roomId
        tasks[index].title = title.trimmingCharacters(in: .whitespacesAndNewlines)
        tasks[index].intervalDays = max(1, intervalDays)
        persist()
    }
    
    func deleteTask(id: UUID) {
        tasks.removeAll(where: { $0.id == id })
        persist()
        updateReminderSchedule()
    }
    
    func setOnboardingDone(_ done: Bool) {
        preferences.onboardingDone = done
        persist()
    }
    
    func setPromoDialogShown(_ shown: Bool) {
        preferences.promoDialogShown = shown
        persist()
    }
    
    func setNotificationsEnabled(_ enabled: Bool) {
        preferences.notificationsEnabled = enabled
        persist()
        if enabled {
            notificationManager.requestPermission { [weak self] granted in
                guard let self = self else { return }
                self.preferences.notificationsEnabled = granted
                self.persist()
                if granted {
                    self.updateReminderSchedule()
                }
            }
        } else {
            notificationManager.cancelReminder()
        }
    }
    
    func setReminderTime(hour: Int, minute: Int) {
        preferences.reminderHour = hour
        preferences.reminderMinute = minute
        persist()
        if preferences.notificationsEnabled {
            updateReminderSchedule()
        }
    }
    
    func setAppLanguage(_ code: String) {
        preferences.appLanguage = code
        persist()
        languageManager.setLanguage(code)
        objectWillChange.send()
    }
    
    func updateReminderSchedule() {
        guard preferences.notificationsEnabled else { return }
        let title = loc("app_name")
        let body = loc("reminder_section_desc")
        notificationManager.scheduleDailyReminder(
            hour: preferences.reminderHour,
            minute: preferences.reminderMinute,
            title: title,
            body: body
        )
    }
    
    func setupSamplePlan() {
        let rKitchen = Room(name: loc("room_kitchen"), sortOrder: 0)
        let rLiving = Room(name: loc("room_living"), sortOrder: 1)
        let rBathroom = Room(name: loc("room_bathroom"), sortOrder: 2)
        let rBedroom = Room(name: loc("room_bedroom"), sortOrder: 3)
        
        rooms = [rKitchen, rLiving, rBathroom, rBedroom]
        
        tasks = [
            ChoreTask(roomId: rKitchen.id, title: loc("task_wipe_counters"), intervalDays: 1, dueDayEpoch: todayEpoch),
            ChoreTask(roomId: rKitchen.id, title: loc("task_take_trash"), intervalDays: 2, dueDayEpoch: todayEpoch),
            ChoreTask(roomId: rLiving.id, title: loc("task_vacuum_living"), intervalDays: 3, dueDayEpoch: todayEpoch),
            ChoreTask(roomId: rLiving.id, title: loc("task_dust_surfaces"), intervalDays: 7, dueDayEpoch: todayEpoch),
            ChoreTask(roomId: rBathroom.id, title: loc("task_clean_sink"), intervalDays: 3, dueDayEpoch: todayEpoch),
            ChoreTask(roomId: rBathroom.id, title: loc("task_mop_bathroom"), intervalDays: 7, dueDayEpoch: todayEpoch),
            ChoreTask(roomId: rBedroom.id, title: loc("task_change_sheets"), intervalDays: 7, dueDayEpoch: todayEpoch),
            ChoreTask(roomId: rBedroom.id, title: loc("task_ventilate"), intervalDays: 1, dueDayEpoch: todayEpoch)
        ]
        
        preferences.onboardingDone = true
        persist()
        updateReminderSchedule()
    }
    
    func exportBackup() -> String {
        dataManager.exportBackupJSON()
    }
    
    func importBackup(_ json: String) -> Bool {
        let success = dataManager.importBackupJSON(json)
        if success {
            loadData()
            showToast(loc("backup_success"))
        } else {
            showToast(loc("backup_error"))
        }
        return success
    }
    
    private func showToast(_ message: String) {
        toastMessage = message
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.5) { [weak self] in
            if self?.toastMessage == message {
                self?.toastMessage = nil
            }
        }
    }
}
