package com.evduzeni.planner.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evduzeni.planner.R
import com.evduzeni.planner.data.entity.TaskEntity
import com.evduzeni.planner.ui.components.AddEditTaskDialog
import com.evduzeni.planner.ui.components.ConfirmDeleteDialog
import com.evduzeni.planner.ui.components.TaskCard
import com.evduzeni.planner.ui.theme.ForestInk
import com.evduzeni.planner.ui.theme.LightMint
import com.evduzeni.planner.ui.theme.SageGreen
import com.evduzeni.planner.ui.theme.SlateMuted
import com.evduzeni.planner.ui.theme.TerracottaOrange
import com.evduzeni.planner.ui.theme.WarmCream
import com.evduzeni.planner.ui.viewmodel.PlannerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val pendingTasks by viewModel.pendingTodayTasks.collectAsState()
    val completedTasks by viewModel.completedTodayTasks.collectAsState()
    val allRooms by viewModel.allRooms.collectAsState()
    val todayEpoch = viewModel.todayEpoch

    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var deletingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var isAddingTask by remember { mutableStateOf(false) }

    val overdueTasks = pendingTasks.filter { it.dueDayEpoch < todayEpoch }
    val todayTasks = pendingTasks.filter { it.dueDayEpoch == todayEpoch }

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val currentLocale = try {
        val list = configuration.locales
        if (!list.isEmpty) list[0] else Locale.getDefault()
    } catch (_: Exception) {
        Locale.getDefault()
    }
    val formattedDate = remember(currentLocale) {
        val sdf = SimpleDateFormat("d MMMM, EEEE", currentLocale)
        sdf.format(Date())
    }

    val roomMap = remember(allRooms) {
        allRooms.associate { it.id to it.name }
    }

    Box(modifier = modifier.fillMaxSize().background(WarmCream)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelLarge,
                        color = SlateMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.today_greeting),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestInk,
                        lineHeight = 32.sp
                    )
                }
            }

            // Overdue section
            if (overdueTasks.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.section_overdue) + " (${overdueTasks.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TerracottaOrange,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(overdueTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        roomName = roomMap[task.roomId] ?: "",
                        isCompletedToday = false,
                        todayEpoch = todayEpoch,
                        onToggleComplete = { viewModel.toggleTaskComplete(task) },
                        onEdit = { editingTask = task },
                        onDelete = { deletingTask = task }
                    )
                }
            }

            // Today section
            if (todayTasks.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.section_today) + " (${todayTasks.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SageGreen,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(todayTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        roomName = roomMap[task.roomId] ?: "",
                        isCompletedToday = false,
                        todayEpoch = todayEpoch,
                        onToggleComplete = { viewModel.toggleTaskComplete(task) },
                        onEdit = { editingTask = task },
                        onDelete = { deletingTask = task }
                    )
                }
            }

            // Empty state (all done or no tasks)
            if (pendingTasks.isEmpty() && completedTasks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(LightMint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = null,
                                    tint = SageGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.today_no_tasks),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ForestInk,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else if (pendingTasks.isEmpty() && completedTasks.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = LightMint.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SageGreen,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.today_all_done),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ForestInk,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Completed today section
            if (completedTasks.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.section_completed_today) + " (${completedTasks.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateMuted,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                items(completedTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        roomName = roomMap[task.roomId] ?: "",
                        isCompletedToday = true,
                        todayEpoch = todayEpoch,
                        onToggleComplete = { viewModel.toggleTaskComplete(task) },
                        onEdit = { editingTask = task },
                        onDelete = { deletingTask = task }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button to add task
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

        // Add task dialog
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

        // Edit task dialog
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

        // Delete confirmation dialog
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
