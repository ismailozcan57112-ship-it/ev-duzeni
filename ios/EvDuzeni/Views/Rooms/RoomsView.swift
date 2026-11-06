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
