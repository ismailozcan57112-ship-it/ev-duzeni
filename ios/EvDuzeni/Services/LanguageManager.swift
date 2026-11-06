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
