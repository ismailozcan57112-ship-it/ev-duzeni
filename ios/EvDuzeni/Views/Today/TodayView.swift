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
                                    Text("\(viewModel.overdueTasks.count)")
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
                Text(total > 0 && completed == total ? loc("today_all_done") : "\(completed) / \(total) done")
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
