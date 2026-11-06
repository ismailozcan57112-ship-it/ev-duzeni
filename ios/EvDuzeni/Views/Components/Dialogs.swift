import SwiftUI

struct AddEditRoomSheet: View {
    @Environment(\.dismiss) private var dismiss
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
    @Environment(\.dismiss) private var dismiss
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
                    ForEach(intervals, id: \.self) { days in
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
                    customDaysText = "\(t.intervalDays)"
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
    @Environment(\.dismiss) private var dismiss
    
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
