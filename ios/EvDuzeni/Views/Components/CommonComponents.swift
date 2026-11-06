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
