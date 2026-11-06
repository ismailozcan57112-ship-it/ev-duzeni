import os

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EV_DUZENI_DIR = os.path.join(BASE_DIR, "ios", "EvDuzeni")

def write_file(rel_path, content):
    full_path = os.path.join(EV_DUZENI_DIR, rel_path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, "w", encoding="utf-8") as f:
        f.write(content.strip() + "\n")
    print(f"Wrote {rel_path}")

# ==============================================================================
# 1. Colors.swift
# ==============================================================================
write_file("Theme/Colors.swift", """
import SwiftUI

extension Color {
    init(hex: UInt, alpha: Double = 1.0) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xff) / 255,
            green: Double((hex >> 08) & 0xff) / 255,
            blue: Double((hex >> 00) & 0xff) / 255,
            opacity: alpha
        )
    }
}

enum AppColors {
    static let forestInk = Color(hex: 0x213C35)
    static let sageGreen = Color(hex: 0x215C4E)
    static let lightMint = Color(hex: 0xEAF0E6)
    static let warmCream = Color(hex: 0xF6F5F0)
    static let borderLine = Color(hex: 0xE1E7DD)
    static let terracottaOrange = Color(hex: 0xA95830)
    static let slateMuted = Color(hex: 0x697B71)
    static let pureWhite = Color(hex: 0xFFFFFF)
    static let successGreen = Color(hex: 0x2E7D32)
    static let softGold = Color(hex: 0xD4AF37)
    
    static let surface = Color(hex: 0xFFFFFF)
    static let background = Color(hex: 0xF6F5F0)
}
""")

# ==============================================================================
# 2. Models.swift
# ==============================================================================
write_file("Models/Models.swift", """
import Foundation

struct Room: Identifiable, Codable, Equatable, Hashable {
    var id: UUID = UUID()
    var name: String
    var sortOrder: Int = 0
    var createdAt: Date = Date()
}

struct ChoreTask: Identifiable, Codable, Equatable, Hashable {
    var id: UUID = UUID()
    var roomId: UUID
    var title: String
    var intervalDays: Int = 1
    var dueDayEpoch: Int64
    var completedDayEpoch: Int64? = nil
    var previousDueDayEpoch: Int64? = nil
    var createdAt: Date = Date()
}

struct AppPreferences: Codable, Equatable {
    var onboardingDone: Bool = false
    var notificationsEnabled: Bool = false
    var reminderHour: Int = 9
    var reminderMinute: Int = 0
    var promoDialogShown: Bool = false
    var hasProLifetime: Bool = true
    var appLanguage: String = ""
}

struct RoomWithTasks: Identifiable {
    var id: UUID { room.id }
    let room: Room
    let tasks: [ChoreTask]
}
""")

# ==============================================================================
# 3. LanguageManager.swift
# ==============================================================================
write_file("Services/LanguageManager.swift", """
import Foundation
import SwiftUI

final class LanguageManager: ObservableObject {
    static let shared = LanguageManager()
    
    @Published var currentLanguage: String = "" {
        didSet {
            loadBundle()
        }
    }
    
    private(set) var bundle: Bundle = .main
    
    init() {
        let saved = UserDefaults.standard.string(forKey: "app_language") ?? ""
        self.currentLanguage = saved
        loadBundle()
    }
    
    func setLanguage(_ code: String) {
        self.currentLanguage = code
        UserDefaults.standard.set(code, forKey: "app_language")
        loadBundle()
    }
    
    private func loadBundle() {
        if currentLanguage.isEmpty {
            bundle = .main
            return
        }
        
        let path = Bundle.main.path(forResource: currentLanguage, ofType: "lproj")
        if let path = path, let customBundle = Bundle(path: path) {
            bundle = customBundle
        } else {
            bundle = .main
        }
    }
    
    func localizedString(_ key: String) -> String {
        return bundle.localizedString(forKey: key, value: key, table: nil)
    }
    
    func localizedString(_ key: String, _ args: CVarArg...) -> String {
        let format = bundle.localizedString(forKey: key, value: key, table: nil)
        return String(format: format, arguments: args)
    }
}

func loc(_ key: String) -> String {
    LanguageManager.shared.localizedString(key)
}

func locF(_ key: String, _ args: CVarArg...) -> String {
    let format = LanguageManager.shared.localizedString(key)
    return String(format: format, arguments: args)
}
""")

# ==============================================================================
# 4. NotificationManager.swift
# ==============================================================================
write_file("Services/NotificationManager.swift", """
import Foundation
import UserNotifications

final class NotificationManager {
    static let shared = NotificationManager()
    private let notificationId = "evduzeni_daily_reminder"
    
    func requestPermission(completion: @escaping (Bool) -> Void) {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, _ in
            DispatchQueue.main.async {
                completion(granted)
            }
        }
    }
    
    func scheduleDailyReminder(hour: Int, minute: Int, title: String, body: String) {
        cancelReminder()
        
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = .default
        
        var dateComponents = DateComponents()
        dateComponents.hour = hour
        dateComponents.minute = minute
        
        let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
        let request = UNNotificationRequest(identifier: notificationId, content: content, trigger: trigger)
        
        UNUserNotificationCenter.current().add(request)
    }
    
    func cancelReminder() {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [notificationId])
    }
}
""")

# ==============================================================================
# 5. DataManager.swift
# ==============================================================================
write_file("Services/DataManager.swift", """
import Foundation

final class DataManager {
    static let shared = DataManager()
    
    private let fileManager = FileManager.default
    
    private var baseDirectory: URL {
        fileManager.urls(for: .applicationSupportDirectory, in: .userDomainMask).first!
    }
    
    private var roomsURL: URL { baseDirectory.appendingPathComponent("rooms.json") }
    private var tasksURL: URL { baseDirectory.appendingPathComponent("tasks.json") }
    private var prefsURL: URL { baseDirectory.appendingPathComponent("prefs.json") }
    
    init() {
        try? fileManager.createDirectory(at: baseDirectory, withIntermediateDirectories: true)
    }
    
    func loadRooms() -> [Room] {
        guard let data = try? Data(contentsOf: roomsURL),
              let rooms = try? JSONDecoder().decode([Room].self, from: data) else {
            return []
        }
        return rooms
    }
    
    func saveRooms(_ rooms: [Room]) {
        if let data = try? JSONEncoder().encode(rooms) {
            try? data.write(to: roomsURL, options: .atomic)
        }
    }
    
    func loadTasks() -> [ChoreTask] {
        guard let data = try? Data(contentsOf: tasksURL),
              let tasks = try? JSONDecoder().decode([ChoreTask].self, from: data) else {
            return []
        }
        return tasks
    }
    
    func saveTasks(_ tasks: [ChoreTask]) {
        if let data = try? JSONEncoder().encode(tasks) {
            try? data.write(to: tasksURL, options: .atomic)
        }
    }
    
    func loadPreferences() -> AppPreferences {
        guard let data = try? Data(contentsOf: prefsURL),
              let prefs = try? JSONDecoder().decode(AppPreferences.self, from: data) else {
            return AppPreferences()
        }
        return prefs
    }
    
    func savePreferences(_ prefs: AppPreferences) {
        if let data = try? JSONEncoder().encode(prefs) {
            try? data.write(to: prefsURL, options: .atomic)
        }
    }
    
    struct ExportData: Codable {
        let version: Int
        let exportDate: String
        let rooms: [Room]
        let tasks: [ChoreTask]
    }
    
    func exportBackupJSON() -> String {
        let export = ExportData(
            version: 1,
            exportDate: ISO8601DateFormatter().string(from: Date()),
            rooms: loadRooms(),
            tasks: loadTasks()
        )
        let encoder = JSONEncoder()
        encoder.outputFormatting = .prettyPrinted
        if let data = try? encoder.encode(export), let str = String(data: data, encoding: .utf8) {
            return str
        }
        return "{}"
    }
    
    func importBackupJSON(_ json: String) -> Bool {
        guard let data = json.data(using: .utf8),
              let backup = try? JSONDecoder().decode(ExportData.self, from: data) else {
            return false
        }
        saveRooms(backup.rooms)
        saveTasks(backup.tasks)
        return true
    }
}
""")

# ==============================================================================
# 6. PlannerViewModel.swift
# ==============================================================================
write_file("ViewModels/PlannerViewModel.swift", """
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
""")

# ==============================================================================
# 7. CommonComponents.swift
# ==============================================================================
write_file("Views/Components/CommonComponents.swift", """
import SwiftUI

struct TaskCardView: View {
    let task: ChoreTask
    let roomName: String
    let isCompleted: Bool
    let onToggle: () -> Void
    var onTap: (() -> Void)? = nil
    
    var body: some View {
        HStack(spacing: 14) {
            Button(action: onToggle) {
                Image(systemName: isCompleted ? "checkmark.circle.fill" : "circle")
                    .font(.system(size: 24, weight: .semibold))
                    .foregroundColor(isCompleted ? AppColors.successGreen : AppColors.slateMuted)
            }
            .buttonStyle(.plain)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(task.title)
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(isCompleted ? AppColors.slateMuted : AppColors.forestInk)
                    .strikethrough(isCompleted, color: AppColors.slateMuted)
                
                HStack(spacing: 6) {
                    if !roomName.isEmpty {
                        Text(roomName)
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(AppColors.sageGreen)
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(AppColors.lightMint)
                            .cornerRadius(4)
                    }
                    
                    Text(intervalText(task.intervalDays))
                        .font(.system(size: 12))
                        .foregroundColor(AppColors.slateMuted)
                }
            }
            
            Spacer()
        }
        .padding(14)
        .background(AppColors.surface)
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(AppColors.borderLine, lineWidth: 1)
        )
        .contentShape(Rectangle())
        .onTapGesture {
            onTap?()
        }
    }
    
    private func intervalText(_ days: Int) -> String {
        switch days {
        case 1: return loc("interval_daily")
        case 2: return loc("interval_2days")
        case 3: return loc("interval_3days")
        case 7: return loc("interval_weekly")
        case 14: return loc("interval_2weeks")
        case 30: return loc("interval_monthly")
        default: return locF("interval_custom", days)
        }
    }
}

struct ToastBanner: View {
    let message: String
    
    var body: some View {
        Text(message)
            .font(.system(size: 14, weight: .medium))
            .foregroundColor(.white)
            .padding(.horizontal, 18)
            .padding(.vertical, 10)
            .background(AppColors.forestInk.opacity(0.92))
            .cornerRadius(20)
            .shadow(radius: 6)
            .padding(.bottom, 20)
            .transition(.move(edge: .bottom).combined(with: .opacity))
    }
}
""")

# ==============================================================================
# 8. Dialogs.swift
# ==============================================================================
write_file("Views/Components/Dialogs.swift", """
import SwiftUI

struct AddEditRoomSheet: View {
    @Environment(\\.dismiss) private var dismiss
    var roomToEdit: Room? = nil
    var onSave: (String) -> Void
    
    @State private var name: String = ""
    
    var body: some View {
        NavigationStack {
            Form {
                Section {
                    TextField(loc("room_name_hint"), text: $name)
                } header: {
                    Text(roomToEdit == nil ? loc("dialog_add_room") : loc("dialog_edit_room"))
                }
            }
            .navigationTitle(roomToEdit == nil ? loc("dialog_add_room") : loc("dialog_edit_room"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(loc("btn_cancel")) { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(loc("btn_save")) {
                        onSave(name)
                        dismiss()
                    }
                    .disabled(name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                }
            }
            .onAppear {
                if let r = roomToEdit {
                    name = r.name
                }
            }
        }
    }
}

struct AddEditTaskSheet: View {
    @Environment(\\.dismiss) private var dismiss
    let rooms: [Room]
    var taskToEdit: ChoreTask? = nil
    var initialRoomId: UUID? = nil
    var onSave: (UUID, String, Int) -> Void
    
    @State private var title: String = ""
    @State private var selectedRoomId: UUID = UUID()
    @State private var intervalDays: Int = 1
    @State private var customDaysText: String = "1"
    
    private let intervals = [1, 2, 3, 7, 14, 30]
    
    var body: some View {
        NavigationStack {
            Form {
                Section(header: Text(loc("dialog_add_task"))) {
                    TextField(loc("task_title_hint"), text: $title)
                    
                    if !rooms.isEmpty {
                        Picker(loc("select_room"), selection: $selectedRoomId) {
                            ForEach(rooms) { r in
                                Text(r.name).tag(r.id)
                            }
                        }
                    }
                }
                
                Section(header: Text(loc("recurrence_label"))) {
                    ForEach(intervals, id: \\.self) { days in
                        HStack {
                            Text(intervalLabel(days))
                            Spacer()
                            if intervalDays == days {
                                Image(systemName: "checkmark")
                                    .foregroundColor(AppColors.sageGreen)
                            }
                        }
                        .contentShape(Rectangle())
                        .onTapGesture {
                            intervalDays = days
                        }
                    }
                    
                    HStack {
                        Text(loc("custom_days_prompt"))
                        Spacer()
                        TextField("1-365", text: $customDaysText)
                            .keyboardType(.numberPad)
                            .multilineTextAlignment(.trailing)
                            .frame(width: 80)
                            .onChange(of: customDaysText) { newVal in
                                if let d = Int(newVal), (1...365).contains(d) {
                                    intervalDays = d
                                }
                            }
                    }
                }
            }
            .navigationTitle(taskToEdit == nil ? loc("dialog_add_task") : loc("dialog_edit_task"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(loc("btn_cancel")) { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(loc("btn_save")) {
                        onSave(selectedRoomId, title, intervalDays)
                        dismiss()
                    }
                    .disabled(title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || rooms.isEmpty)
                }
            }
            .onAppear {
                if let t = taskToEdit {
                    title = t.title
                    selectedRoomId = t.roomId
                    intervalDays = t.intervalDays
                    customDaysText = "\\(t.intervalDays)"
                } else if let initId = initialRoomId, rooms.contains(where: { $0.id == initId }) {
                    selectedRoomId = initId
                } else if let first = rooms.first {
                    selectedRoomId = first.id
                }
            }
        }
    }
    
    private func intervalLabel(_ days: Int) -> String {
        switch days {
        case 1: return loc("interval_daily")
        case 2: return loc("interval_2days")
        case 3: return loc("interval_3days")
        case 7: return loc("interval_weekly")
        case 14: return loc("interval_2weeks")
        case 30: return loc("interval_monthly")
        default: return locF("interval_custom", days)
        }
    }
}

struct PromotionalNoticeSheet: View {
    @Environment(\\.dismiss) private var dismiss
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    HStack {
                        Text(loc("promo_badge"))
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 3)
                            .background(AppColors.terracottaOrange)
                            .cornerRadius(4)
                        Spacer()
                    }
                    
                    Text(loc("promo_dialog_title"))
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(AppColors.forestInk)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    
                    Text(loc("promo_dialog_subtitle"))
                        .font(.system(size: 15))
                        .foregroundColor(AppColors.slateMuted)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    
                    VStack(spacing: 16) {
                        featureRow(icon: "house.fill", title: loc("promo_feature_1"), desc: loc("promo_feature_1_desc"))
                        featureRow(icon: "arrow.triangle.2.circlepath", title: loc("promo_feature_2"), desc: loc("promo_feature_2_desc"))
                        featureRow(icon: "bell.fill", title: loc("promo_feature_3"), desc: loc("promo_feature_3_desc"))
                    }
                    .padding(16)
                    .background(AppColors.lightMint.opacity(0.5))
                    .cornerRadius(12)
                    
                    VStack(alignment: .leading, spacing: 6) {
                        Text(loc("promo_future_notice_title"))
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(AppColors.forestInk)
                        Text(loc("promo_future_notice_desc"))
                            .font(.system(size: 12))
                            .foregroundColor(AppColors.slateMuted)
                    }
                    .padding(12)
                    .background(AppColors.borderLine.opacity(0.3))
                    .cornerRadius(8)
                    
                    Button(action: { dismiss() }) {
                        Text(loc("promo_btn_dismiss"))
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(AppColors.sageGreen)
                            .cornerRadius(12)
                    }
                    .padding(.top, 10)
                }
                .padding(20)
            }
            .navigationBarTitleDisplayMode(.inline)
        }
    }
    
    private func featureRow(icon: String, title: String, desc: String) -> some View {
        HStack(alignment: .top, spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 20))
                .foregroundColor(AppColors.sageGreen)
                .frame(width: 28)
            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundColor(AppColors.forestInk)
                Text(desc)
                    .font(.system(size: 13))
                    .foregroundColor(AppColors.slateMuted)
            }
            Spacer()
        }
    }
}
""")

# ==============================================================================
# 9. TodayView.swift
# ==============================================================================
write_file("Views/Today/TodayView.swift", """
import SwiftUI

struct TodayView: View {
    @ObservedObject var viewModel: PlannerViewModel
    @State private var showingAddTask = false
    @State private var editingTask: ChoreTask? = nil
    
    var body: some View {
        NavigationStack {
            ZStack(alignment: .bottom) {
                ScrollView {
                    VStack(spacing: 20) {
                        // Header Greeting & Progress
                        progressHeader
                        
                        // Overdue Section
                        if !viewModel.overdueTasks.isEmpty {
                            VStack(alignment: .leading, spacing: 10) {
                                HStack {
                                    Text(loc("section_overdue"))
                                        .font(.system(size: 17, weight: .bold))
                                        .foregroundColor(AppColors.terracottaOrange)
                                    Spacer()
                                    Text("\\(viewModel.overdueTasks.count)")
                                        .font(.system(size: 13, weight: .semibold))
                                        .foregroundColor(.white)
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 2)
                                        .background(AppColors.terracottaOrange)
                                        .clipShape(Capsule())
                                }
                                
                                ForEach(viewModel.overdueTasks) { task in
                                    TaskCardView(
                                        task: task,
                                        roomName: viewModel.roomName(for: task.roomId),
                                        isCompleted: false,
                                        onToggle: { viewModel.toggleTaskComplete(task) },
                                        onTap: { editingTask = task }
                                    )
                                }
                            }
                        }
                        
                        // Due Today Section
                        VStack(alignment: .leading, spacing: 10) {
                            Text(loc("section_today"))
                                .font(.system(size: 17, weight: .bold))
                                .foregroundColor(AppColors.forestInk)
                            
                            let todayOnly = viewModel.pendingTodayTasks.filter { $0.dueDayEpoch == viewModel.todayEpoch }
                            if todayOnly.isEmpty && viewModel.overdueTasks.isEmpty {
                                emptyStateCard
                            } else {
                                ForEach(todayOnly) { task in
                                    TaskCardView(
                                        task: task,
                                        roomName: viewModel.roomName(for: task.roomId),
                                        isCompleted: false,
                                        onToggle: { viewModel.toggleTaskComplete(task) },
                                        onTap: { editingTask = task }
                                    )
                                }
                            }
                        }
                        
                        // Completed Today Section
                        if !viewModel.completedTodayTasks.isEmpty {
                            VStack(alignment: .leading, spacing: 10) {
                                Text(loc("section_completed_today"))
                                    .font(.system(size: 17, weight: .bold))
                                    .foregroundColor(AppColors.slateMuted)
                                
                                ForEach(viewModel.completedTodayTasks) { task in
                                    TaskCardView(
                                        task: task,
                                        roomName: viewModel.roomName(for: task.roomId),
                                        isCompleted: true,
                                        onToggle: { viewModel.toggleTaskComplete(task) },
                                        onTap: { editingTask = task }
                                    )
                                }
                            }
                        }
                    }
                    .padding(16)
                }
                .background(AppColors.background.ignoresSafeArea())
                
                if let msg = viewModel.toastMessage {
                    ToastBanner(message: msg)
                }
            }
            .navigationTitle(loc("nav_today"))
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button(action: { showingAddTask = true }) {
                        Image(systemName: "plus.circle.fill")
                            .font(.system(size: 22))
                            .foregroundColor(AppColors.sageGreen)
                    }
                }
            }
            .sheet(isPresented: $showingAddTask) {
                AddEditTaskSheet(rooms: viewModel.rooms) { rId, title, interval in
                    viewModel.addTask(roomId: rId, title: title, intervalDays: interval)
                }
            }
            .sheet(item: $editingTask) { task in
                AddEditTaskSheet(rooms: viewModel.rooms, taskToEdit: task) { rId, title, interval in
                    viewModel.updateTask(id: task.id, roomId: rId, title: title, intervalDays: interval)
                }
            }
        }
    }
    
    private var progressHeader: some View {
        let total = viewModel.pendingTodayTasks.count + viewModel.completedTodayTasks.count
        let completed = viewModel.completedTodayTasks.count
        let ratio = total > 0 ? Double(completed) / Double(total) : 1.0
        
        return VStack(alignment: .leading, spacing: 12) {
            Text(loc("today_greeting"))
                .font(.system(size: 15, weight: .medium))
                .foregroundColor(AppColors.slateMuted)
            
            HStack {
                Text(total > 0 && completed == total ? loc("today_all_done") : "\\(completed) / \\(total) done")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(AppColors.forestInk)
                Spacer()
                Text("\(Int(ratio * 100))%")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(AppColors.sageGreen)
            }
            
            ProgressView(value: ratio)
                .tint(AppColors.sageGreen)
                .background(AppColors.borderLine)
                .scaleEffect(x: 1, y: 2, anchor: .center)
                .clipShape(Capsule())
        }
        .padding(16)
        .background(AppColors.surface)
        .cornerRadius(14)
        .overlay(RoundedRectangle(cornerRadius: 14).stroke(AppColors.borderLine, lineWidth: 1))
    }
    
    private var emptyStateCard: some View {
        HStack {
            Spacer()
            VStack(spacing: 8) {
                Image(systemName: "sparkles")
                    .font(.system(size: 32))
                    .foregroundColor(AppColors.sageGreen)
                Text(loc("today_all_done"))
                    .font(.system(size: 15, weight: .medium))
                    .foregroundColor(AppColors.forestInk)
                    .multilineTextAlignment(.center)
            }
            .padding(24)
            Spacer()
        }
        .background(AppColors.surface)
        .cornerRadius(12)
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColors.borderLine, lineWidth: 1))
    }
}
""")

# ==============================================================================
# 10. PlanView.swift
# ==============================================================================
write_file("Views/Plan/PlanView.swift", """
import SwiftUI

struct PlanView: View {
    @ObservedObject var viewModel: PlannerViewModel
    @State private var showingAddTask = false
    @State private var editingTask: ChoreTask? = nil
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 16) {
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(loc("plan_subtitle"))
                                .font(.system(size: 14))
                                .foregroundColor(AppColors.slateMuted)
                        }
                        Spacer()
                    }
                    .padding(.horizontal, 16)
                    
                    if viewModel.tasks.isEmpty {
                        VStack(spacing: 12) {
                            Image(systemName: "calendar.badge.plus")
                                .font(.system(size: 40))
                                .foregroundColor(AppColors.slateMuted)
                            Text(loc("today_no_tasks"))
                                .font(.system(size: 15))
                                .foregroundColor(AppColors.slateMuted)
                            Button(action: { showingAddTask = true }) {
                                Text(loc("btn_add_task"))
                                    .font(.system(size: 15, weight: .semibold))
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 20)
                                    .padding(.vertical, 10)
                                    .background(AppColors.sageGreen)
                                    .cornerRadius(10)
                            }
                        }
                        .padding(40)
                    } else {
                        ForEach(viewModel.rooms) { room in
                            let roomTasks = viewModel.tasksForRoom(room.id)
                            if !roomTasks.isEmpty {
                                VStack(alignment: .leading, spacing: 8) {
                                    Text(room.name)
                                        .font(.system(size: 17, weight: .bold))
                                        .foregroundColor(AppColors.forestInk)
                                        .padding(.horizontal, 16)
                                        .padding(.top, 8)
                                    
                                    ForEach(roomTasks) { task in
                                        TaskCardView(
                                            task: task,
                                            roomName: "",
                                            isCompleted: task.completedDayEpoch == viewModel.todayEpoch,
                                            onToggle: { viewModel.toggleTaskComplete(task) },
                                            onTap: { editingTask = task }
                                        )
                                        .padding(.horizontal, 16)
                                    }
                                }
                            }
                        }
                    }
                }
                .padding(.vertical, 12)
            }
            .background(AppColors.background.ignoresSafeArea())
            .navigationTitle(loc("plan_title"))
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button(action: { showingAddTask = true }) {
                        Image(systemName: "plus")
                            .font(.system(size: 18, weight: .semibold))
                            .foregroundColor(AppColors.sageGreen)
                    }
                }
            }
            .sheet(isPresented: $showingAddTask) {
                AddEditTaskSheet(rooms: viewModel.rooms) { rId, title, interval in
                    viewModel.addTask(roomId: rId, title: title, intervalDays: interval)
                }
            }
            .sheet(item: $editingTask) { task in
                AddEditTaskSheet(rooms: viewModel.rooms, taskToEdit: task) { rId, title, interval in
                    viewModel.updateTask(id: task.id, roomId: rId, title: title, intervalDays: interval)
                }
            }
        }
    }
}
""")

# ==============================================================================
# 11. RoomsView.swift
# ==============================================================================
write_file("Views/Rooms/RoomsView.swift", """
import SwiftUI

struct RoomsView: View {
    @ObservedObject var viewModel: PlannerViewModel
    @State private var showingAddRoom = false
    @State private var editingRoom: Room? = nil
    @State private var selectedRoomForTasks: Room? = nil
    @State private var showingAddTask = false
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 12) {
                    HStack {
                        Text(loc("rooms_subtitle"))
                            .font(.system(size: 14))
                            .foregroundColor(AppColors.slateMuted)
                        Spacer()
                    }
                    .padding(.horizontal, 16)
                    
                    ForEach(viewModel.rooms) { room in
                        let taskCount = viewModel.tasksForRoom(room.id).count
                        let overdueCount = viewModel.overdueCountForRoom(room.id)
                        
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(room.name)
                                    .font(.system(size: 17, weight: .semibold))
                                    .foregroundColor(AppColors.forestInk)
                                
                                HStack(spacing: 8) {
                                    Text(locF("room_tasks_count", taskCount))
                                        .font(.system(size: 13))
                                        .foregroundColor(AppColors.slateMuted)
                                    
                                    if overdueCount > 0 {
                                        Text(locF("room_overdue_badge", overdueCount))
                                            .font(.system(size: 11, weight: .bold))
                                            .foregroundColor(.white)
                                            .padding(.horizontal, 6)
                                            .padding(.vertical, 2)
                                            .background(AppColors.terracottaOrange)
                                            .cornerRadius(4)
                                    }
                                }
                            }
                            
                            Spacer()
                            
                            Menu {
                                Button(loc("dialog_edit_room")) {
                                    editingRoom = room
                                }
                                Button(role: .destructive) {
                                    viewModel.deleteRoom(id: room.id)
                                } label: {
                                    Text(loc("btn_delete"))
                                }
                            } label: {
                                Image(systemName: "ellipsis.circle")
                                    .font(.system(size: 20))
                                    .foregroundColor(AppColors.slateMuted)
                            }
                        }
                        .padding(16)
                        .background(AppColors.surface)
                        .cornerRadius(12)
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColors.borderLine, lineWidth: 1))
                        .padding(.horizontal, 16)
                    }
                    
                    Button(action: { showingAddRoom = true }) {
                        HStack {
                            Image(systemName: "plus")
                            Text(loc("btn_add_room"))
                        }
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundColor(AppColors.sageGreen)
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(AppColors.lightMint)
                        .cornerRadius(12)
                        .padding(.horizontal, 16)
                    }
                    .padding(.top, 8)
                }
                .padding(.vertical, 12)
            }
            .background(AppColors.background.ignoresSafeArea())
            .navigationTitle(loc("rooms_title"))
            .sheet(isPresented: $showingAddRoom) {
                AddEditRoomSheet { name in
                    viewModel.addRoom(name: name)
                }
            }
            .sheet(item: $editingRoom) { room in
                AddEditRoomSheet(roomToEdit: room) { newName in
                    viewModel.updateRoom(id: room.id, name: newName)
                }
            }
        }
    }
}
""")

# ==============================================================================
# 12. SettingsView.swift
# ==============================================================================
write_file("Views/Settings/SettingsView.swift", """
import SwiftUI

struct SettingsView: View {
    @ObservedObject var viewModel: PlannerViewModel
    @State private var showingPromo = false
    @State private var showingLanguagePicker = false
    @State private var shareSheetText: String? = nil
    @State private var importAlert = false
    @State private var importInputText = ""
    
    private let availableLanguages: [(code: String, name: String)] = [
        ("", "System Default (Sistem)"),
        ("tr", "Türkçe (TR)"),
        ("en", "English (EN)"),
        ("de", "Deutsch (DE)"),
        ("es", "Español (ES)"),
        ("fr", "Français (FR)"),
        ("it", "Italiano (IT)"),
        ("pt-PT", "Português (PT)"),
        ("ru", "Русский (RU)"),
        ("ja", "日本語 (JA)"),
        ("ko", "한국어 (KO)"),
        ("zh-Hans", "简体中文 (ZH)"),
        ("zh-Hant", "繁體中文 (ZH-TW)"),
        ("ar", "العربية (AR)"),
        ("hi", "हिन्दी (HI)"),
        ("id", "Bahasa Indonesia (ID)"),
        ("ur", "اردو (UR)"),
        ("uk", "Українська (UK)"),
        ("nl", "Nederlands (NL)"),
        ("vi", "Tiếng Việt (VI)"),
        ("th", "ไทย (TH)"),
        ("pl", "Polski (PL)"),
        ("fil", "Filipino (FIL)")
    ]
    
    var body: some View {
        NavigationStack {
            List {
                // Promotional Banner
                Section {
                    Button(action: { showingPromo = true }) {
                        HStack(spacing: 12) {
                            Image(systemName: "sparkles")
                                .font(.system(size: 22))
                                .foregroundColor(AppColors.terracottaOrange)
                            VStack(alignment: .leading, spacing: 2) {
                                Text(loc("promo_settings_card_title"))
                                    .font(.system(size: 15, weight: .semibold))
                                    .foregroundColor(AppColors.forestInk)
                                Text(loc("promo_settings_card_desc"))
                                    .font(.system(size: 13))
                                    .foregroundColor(AppColors.slateMuted)
                            }
                            Spacer()
                            Image(systemName: "chevron.right")
                                .font(.system(size: 14))
                                .foregroundColor(AppColors.slateMuted)
                        }
                    }
                }
                
                // Notifications Section
                Section(header: Text(loc("reminder_section_title")), footer: Text(loc("reminder_section_desc"))) {
                    Toggle(isOn: Binding(
                        get: { viewModel.preferences.notificationsEnabled },
                        set: { viewModel.setNotificationsEnabled($0) }
                    )) {
                        Text(loc("btn_toggle_reminder_on"))
                    }
                    
                    if viewModel.preferences.notificationsEnabled {
                        DatePicker(
                            loc("reminder_time"),
                            selection: reminderBinding,
                            displayedComponents: .hourAndMinute
                        )
                    }
                }
                
                // Language Picker Section
                Section(header: Text("Language / Dil")) {
                    Picker("Language", selection: Binding(
                        get: { viewModel.preferences.appLanguage },
                        set: { viewModel.setAppLanguage($0) }
                    )) {
                        ForEach(availableLanguages, id: \\.code) { lang in
                            Text(lang.name).tag(lang.code)
                        }
                    }
                }
                
                // Backup Section
                Section(header: Text(loc("backup_section_title")), footer: Text(loc("backup_section_desc"))) {
                    Button(action: {
                        shareSheetText = viewModel.exportBackup()
                    }) {
                        HStack {
                            Image(systemName: "square.and.arrow.up")
                            Text(loc("btn_export_backup"))
                        }
                    }
                    
                    Button(action: {
                        importAlert = true
                    }) {
                        HStack {
                            Image(systemName: "square.and.arrow.down")
                            Text(loc("btn_import_backup"))
                        }
                    }
                }
                
                // About Section
                Section(header: Text(loc("about_app"))) {
                    HStack {
                        Text("Version")
                        Spacer()
                        Text("1.0.0 (iOS Release)")
                            .foregroundColor(AppColors.slateMuted)
                    }
                    
                    VStack(alignment: .leading, spacing: 4) {
                        Text(loc("privacy_title"))
                            .font(.system(size: 14, weight: .semibold))
                        Text(loc("privacy_summary"))
                            .font(.system(size: 12))
                            .foregroundColor(AppColors.slateMuted)
                    }
                }
            }
            .navigationTitle(loc("settings_title"))
            .sheet(isPresented: $showingPromo) {
                PromotionalNoticeSheet()
            }
            .sheet(isPresented: Binding(
                get: { shareSheetText != nil },
                set: { if !$0 { shareSheetText = nil } }
            )) {
                if let text = shareSheetText {
                    ActivityView(activityItems: [text])
                }
            }
            .alert("Import Backup JSON", isPresented: $importAlert) {
                TextField("Paste JSON here", text: $importInputText)
                Button("Import") {
                    _ = viewModel.importBackup(importInputText)
                    importInputText = ""
                }
                Button("Cancel", role: .cancel) { importInputText = "" }
            }
        }
    }
    
    private var reminderBinding: Binding<Date> {
        Binding(
            get: {
                var c = DateComponents()
                c.hour = viewModel.preferences.reminderHour
                c.minute = viewModel.preferences.reminderMinute
                return Calendar.current.date(from: c) ?? Date()
            },
            set: { newDate in
                let c = Calendar.current.dateComponents([.hour, .minute], from: newDate)
                viewModel.setReminderTime(hour: c.hour ?? 9, minute: c.minute ?? 0)
            }
        )
    }
}

struct ActivityView: UIViewControllerRepresentable {
    let activityItems: [Any]
    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: activityItems, applicationActivities: nil)
    }
    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}
""")

# ==============================================================================
# 13. OnboardingView.swift
# ==============================================================================
write_file("Views/Onboarding/OnboardingView.swift", """
import SwiftUI

struct OnboardingView: View {
    @ObservedObject var viewModel: PlannerViewModel
    @State private var currentStep = 0
    
    private let slides = [
        (icon: "house.fill", titleKey: "onboarding_1_title", descKey: "onboarding_1_desc"),
        (icon: "calendar", titleKey: "onboarding_2_title", descKey: "onboarding_2_desc"),
        (icon: "bell.badge.fill", titleKey: "onboarding_3_title", descKey: "onboarding_3_desc")
    ]
    
    var body: some View {
        VStack {
            HStack {
                Spacer()
                Button(loc("onboarding_btn_skip")) {
                    viewModel.setOnboardingDone(true)
                }
                .font(.system(size: 15, weight: .medium))
                .foregroundColor(AppColors.slateMuted)
                .padding(20)
            }
            
            Spacer()
            
            TabView(selection: $currentStep) {
                ForEach(0..<slides.count, id: \\.self) { i in
                    VStack(spacing: 20) {
                        Image(systemName: slides[i].icon)
                            .font(.system(size: 64))
                            .foregroundColor(AppColors.sageGreen)
                            .frame(height: 100)
                        
                        Text(loc(slides[i].titleKey))
                            .font(.system(size: 26, weight: .bold))
                            .foregroundColor(AppColors.forestInk)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 24)
                        
                        Text(loc(slides[i].descKey))
                            .font(.system(size: 15))
                            .foregroundColor(AppColors.slateMuted)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 32)
                    }
                    .tag(i)
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .always))
            .frame(height: 380)
            
            Spacer()
            
            VStack(spacing: 12) {
                if currentStep < slides.count - 1 {
                    Button(action: {
                        withAnimation { currentStep += 1 }
                    }) {
                        Text(loc("onboarding_btn_next"))
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .background(AppColors.sageGreen)
                            .cornerRadius(14)
                    }
                } else {
                    Button(action: {
                        viewModel.setupSamplePlan()
                    }) {
                        Text(loc("onboarding_btn_sample_plan"))
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .background(AppColors.sageGreen)
                            .cornerRadius(14)
                    }
                    
                    Button(action: {
                        viewModel.setOnboardingDone(true)
                    }) {
                        Text(loc("onboarding_btn_start_empty"))
                            .font(.system(size: 15, weight: .medium))
                            .foregroundColor(AppColors.forestInk)
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                    }
                }
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
        .background(AppColors.background.ignoresSafeArea())
    }
}
""")

# ==============================================================================
# 14. MainTabView.swift
# ==============================================================================
write_file("Views/MainTabView.swift", """
import SwiftUI

struct MainTabView: View {
    @ObservedObject var viewModel: PlannerViewModel
    @State private var selectedTab = 0
    @State private var showPromoDialog = false
    
    var body: some View {
        TabView(selection: $selectedTab) {
            TodayView(viewModel: viewModel)
                .tabItem {
                    Label(loc("nav_today"), systemImage: "checkmark.circle.fill")
                }
                .tag(0)
            
            PlanView(viewModel: viewModel)
                .tabItem {
                    Label(loc("nav_plan"), systemImage: "calendar")
                }
                .tag(1)
            
            RoomsView(viewModel: viewModel)
                .tabItem {
                    Label(loc("nav_rooms"), systemImage: "door.left.hand.open")
                }
                .tag(2)
            
            SettingsView(viewModel: viewModel)
                .tabItem {
                    Label(loc("nav_settings"), systemImage: "gearshape.fill")
                }
                .tag(3)
        }
        .tint(AppColors.sageGreen)
        .onAppear {
            if viewModel.preferences.onboardingDone && !viewModel.preferences.promoDialogShown {
                showPromoDialog = true
                viewModel.setPromoDialogShown(true)
            }
        }
        .sheet(isPresented: $showPromoDialog) {
            PromotionalNoticeSheet()
        }
    }
}
""")

# ==============================================================================
# 15. EvDuzeniApp.swift
# ==============================================================================
write_file("App/EvDuzeniApp.swift", """
import SwiftUI

@main
struct EvDuzeniApp: App {
    @StateObject private var viewModel = PlannerViewModel()
    @ObservedObject private var languageManager = LanguageManager.shared
    
    var body: some Scene {
        WindowGroup {
            Group {
                if !viewModel.preferences.onboardingDone {
                    OnboardingView(viewModel: viewModel)
                } else {
                    MainTabView(viewModel: viewModel)
                }
            }
            .environment(\\.locale, languageManager.currentLanguage.isEmpty ? .autoupdatingCurrent : Locale(identifier: languageManager.currentLanguage))
        }
    }
}
""")

# ==============================================================================
# 16. Info.plist
# ==============================================================================
write_file("Info.plist", """
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleDevelopmentRegion</key>
    <string>en</string>
    <key>CFBundleExecutable</key>
    <string>$(EXECUTABLE_NAME)</string>
    <key>CFBundleIdentifier</key>
    <string>$(PRODUCT_BUNDLE_IDENTIFIER)</string>
    <key>CFBundleInfoDictionaryVersion</key>
    <string>6.0</string>
    <key>CFBundleName</key>
    <string>$(PRODUCT_NAME)</string>
    <key>CFBundlePackageType</key>
    <string>$(PRODUCT_BUNDLE_PACKAGE_TYPE)</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0.0</string>
    <key>CFBundleVersion</key>
    <string>1</string>
    <key>CFBundleDisplayName</key>
    <string>Ev Düzeni</string>
    <key>LSRequiresIPhoneOS</key>
    <true/>
    <key>ITSAppUsesNonExemptEncryption</key>
    <false/>
    <key>UILaunchScreen</key>
    <dict/>
    <key>UISupportedInterfaceOrientations</key>
    <array>
        <string>UIInterfaceOrientationPortrait</string>
    </array>
</dict>
</plist>
""")

print("Successfully generated all Swift source files and Info.plist.")
