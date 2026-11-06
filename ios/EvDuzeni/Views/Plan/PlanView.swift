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
