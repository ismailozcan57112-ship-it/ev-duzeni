package com.evduzeni.planner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.evduzeni.planner.data.entity.TaskEntity
import com.evduzeni.planner.ui.components.AddEditTaskDialog
import com.evduzeni.planner.ui.components.ConfirmDeleteDialog
import com.evduzeni.planner.ui.components.TaskCard
import com.evduzeni.planner.ui.theme.ForestInk
import com.evduzeni.planner.ui.theme.SageGreen
import com.evduzeni.planner.ui.theme.SlateMuted
import com.evduzeni.planner.ui.theme.WarmCream
import com.evduzeni.planner.ui.viewmodel.PlannerViewModel

@Composable
fun PlanScreen(
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val roomsWithTasks by viewModel.roomsWithTasks.collectAsState()
    val allRooms by viewModel.allRooms.collectAsState()
    val todayEpoch = viewModel.todayEpoch

    var isAddingTask by remember { mutableStateOf(false) }
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
                        text = stringResource(R.string.plan_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.plan_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateMuted
                    )
                }
            }

            roomsWithTasks.forEach { roomWithTasks ->
                val room = roomWithTasks.room
                val tasks = roomWithTasks.tasks

                if (tasks.isNotEmpty()) {
                    item {
                        Text(
                            text = room.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ForestInk,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    items(tasks, key = { it.id }) { task ->
                        val isCompletedToday = task.completedDayEpoch == todayEpoch
                        TaskCard(
                            task = task,
                            roomName = room.name,
                            isCompletedToday = isCompletedToday,
                            todayEpoch = todayEpoch,
                            onToggleComplete = { viewModel.toggleTaskComplete(task) },
                            onEdit = { editingTask = task },
                            onDelete = { deletingTask = task }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        FloatingActionButton(
            onClick = { isAddingTask = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = SageGreen,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.btn_add_task))
        }

        if (isAddingTask) {
            AddEditTaskDialog(
                rooms = allRooms,
                onDismiss = { isAddingTask = false },
                onSave = { title, roomId, intervalDays ->
                    viewModel.saveTask(title, roomId, intervalDays)
                    isAddingTask = false
                }
            )
        }

        editingTask?.let { task ->
            AddEditTaskDialog(
                initialTask = task,
                rooms = allRooms,
                onDismiss = { editingTask = null },
                onSave = { title, roomId, intervalDays ->
                    viewModel.saveTask(title, roomId, intervalDays, task)
                    editingTask = null
                }
            )
        }

        deletingTask?.let { task ->
            ConfirmDeleteDialog(
                message = stringResource(R.string.confirm_delete_task_desc),
                onDismiss = { deletingTask = null },
                onConfirm = {
                    viewModel.deleteTask(task)
                    deletingTask = null
                }
            )
        }
    }
}
