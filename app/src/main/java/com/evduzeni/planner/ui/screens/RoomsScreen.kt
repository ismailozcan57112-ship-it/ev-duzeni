package com.evduzeni.planner.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.evduzeni.planner.R
import com.evduzeni.planner.data.entity.RoomEntity
import com.evduzeni.planner.data.entity.TaskEntity
import com.evduzeni.planner.ui.components.AddEditRoomDialog
import com.evduzeni.planner.ui.components.AddEditTaskDialog
import com.evduzeni.planner.ui.components.ConfirmDeleteDialog
import com.evduzeni.planner.ui.theme.BorderLine
import com.evduzeni.planner.ui.theme.ForestInk
import com.evduzeni.planner.ui.theme.LightMint
import com.evduzeni.planner.ui.theme.SageGreen
import com.evduzeni.planner.ui.theme.SlateMuted
import com.evduzeni.planner.ui.theme.TerracottaOrange
import com.evduzeni.planner.ui.theme.WarmCream
import com.evduzeni.planner.ui.viewmodel.PlannerViewModel

@Composable
fun RoomsScreen(
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val roomsWithTasks by viewModel.roomsWithTasks.collectAsState()
    val allRooms by viewModel.allRooms.collectAsState()
    val todayEpoch = viewModel.todayEpoch

    var isAddingRoom by remember { mutableStateOf(false) }
    var editingRoom by remember { mutableStateOf<RoomEntity?>(null) }
    var deletingRoom by remember { mutableStateOf<RoomEntity?>(null) }

    var addingTaskForRoomId by remember { mutableStateOf<Long?>(null) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var deletingTask by remember { mutableStateOf<TaskEntity?>(null) }

    Box(modifier = modifier.fillMaxSize().background(WarmCream)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = stringResource(R.string.rooms_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.rooms_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateMuted
                    )
                }
            }

            items(roomsWithTasks, key = { it.room.id }) { roomWithTasks ->
                val room = roomWithTasks.room
                val tasks = roomWithTasks.tasks
                val overdueCount = tasks.count { it.completedDayEpoch != todayEpoch && it.dueDayEpoch < todayEpoch }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderLine)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Room title & actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = room.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestInk
                                )
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.room_tasks_count, tasks.size),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = SlateMuted
                                    )
                                    if (overdueCount > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = TerracottaOrange.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.room_overdue_badge, overdueCount),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TerracottaOrange,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            IconButton(onClick = { editingRoom = room }) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = SlateMuted)
                            }
                            IconButton(onClick = { deletingRoom = room }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = TerracottaOrange)
                            }
                        }

                        // Chores inside this room
                        if (tasks.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                tasks.forEach { task ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(LightMint.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = ForestInk,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = stringResource(R.string.interval_custom, task.intervalDays),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SlateMuted
                                        )
                                        IconButton(onClick = { editingTask = task }) {
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = SlateMuted)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Add chore button
                        OutlinedButton(
                            onClick = { addingTaskForRoomId = room.id },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SageGreen.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = SageGreen)
                            Text(
                                text = stringResource(R.string.btn_add_task),
                                color = SageGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // FAB to add Room
        FloatingActionButton(
            onClick = { isAddingRoom = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = SageGreen,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.btn_add_room))
        }

        // Add Room dialog
        if (isAddingRoom) {
            AddEditRoomDialog(
                onDismiss = { isAddingRoom = false },
                onSave = { name ->
                    viewModel.saveRoom(name)
                    isAddingRoom = false
                }
            )
        }

        // Edit Room dialog
        editingRoom?.let { room ->
            AddEditRoomDialog(
                initialRoom = room,
                onDismiss = { editingRoom = null },
                onSave = { name ->
                    viewModel.saveRoom(name, room)
                    editingRoom = null
                }
            )
        }

        // Delete Room dialog
        deletingRoom?.let { room ->
            ConfirmDeleteDialog(
                message = stringResource(R.string.confirm_delete_room_desc),
                onDismiss = { deletingRoom = null },
                onConfirm = {
                    viewModel.deleteRoom(room)
                    deletingRoom = null
                }
            )
        }

        // Add task into room dialog
        addingTaskForRoomId?.let { roomId ->
            AddEditTaskDialog(
                rooms = allRooms,
                selectedRoomId = roomId,
                onDismiss = { addingTaskForRoomId = null },
                onSave = { title, targetRoomId, intervalDays ->
                    viewModel.saveTask(title, targetRoomId, intervalDays)
                    addingTaskForRoomId = null
                }
            )
        }

        // Edit task dialog
        editingTask?.let { task ->
            AddEditTaskDialog(
                initialTask = task,
                rooms = allRooms,
                onDismiss = { editingTask = null },
                onSave = { title, targetRoomId, intervalDays ->
                    viewModel.saveTask(title, targetRoomId, intervalDays, task)
                    editingTask = null
                }
            )
        }
    }
}
