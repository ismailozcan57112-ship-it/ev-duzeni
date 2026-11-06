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
            .environment(\.locale, languageManager.currentLanguage.isEmpty ? .autoupdatingCurrent : Locale(identifier: languageManager.currentLanguage))
        }
    }
}
