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
