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
