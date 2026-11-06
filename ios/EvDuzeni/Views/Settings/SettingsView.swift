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
                        ForEach(availableLanguages, id: \.code) { lang in
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
