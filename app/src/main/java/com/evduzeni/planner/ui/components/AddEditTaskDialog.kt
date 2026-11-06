package com.evduzeni.planner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.evduzeni.planner.R
import com.evduzeni.planner.data.entity.RoomEntity
import com.evduzeni.planner.data.entity.TaskEntity
import com.evduzeni.planner.ui.theme.BorderLine
import com.evduzeni.planner.ui.theme.ForestInk
import com.evduzeni.planner.ui.theme.LightMint
import com.evduzeni.planner.ui.theme.SageGreen
import com.evduzeni.planner.ui.theme.SlateMuted

@Composable
fun AddEditTaskDialog(
    initialTask: TaskEntity? = null,
    rooms: List<RoomEntity>,
    selectedRoomId: Long? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, roomId: Long, intervalDays: Int) -> Unit
) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var currentRoomId by remember {
        mutableLongStateOf(
            initialTask?.roomId ?: selectedRoomId ?: rooms.firstOrNull()?.id ?: 0L
        )
    }
    var intervalDays by remember { mutableIntStateOf(initialTask?.intervalDays ?: 1) }
    var customIntervalText by remember {
        mutableStateOf(if (initialTask != null && initialTask.intervalDays !in listOf(1, 2, 3, 7, 14, 30)) initialTask.intervalDays.toString() else "")
    }
    var isCustomSelected by remember {
        mutableStateOf(initialTask != null && initialTask.intervalDays !in listOf(1, 2, 3, 7, 14, 30))
    }
    var roomDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (initialTask == null) stringResource(R.string.dialog_add_task) else stringResource(R.string.dialog_edit_task),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ForestInk
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.task_title_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen,
                        unfocusedBorderColor = BorderLine,
                        focusedLabelColor = SageGreen
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Room selector
                Text(
                    text = stringResource(R.string.select_room),
                    style = MaterialTheme.typography.labelMedium,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                val selectedRoomName = rooms.find { it.id == currentRoomId }?.name ?: ""
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LightMint)
                            .clickable { roomDropdownExpanded = true }
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = selectedRoomName.ifEmpty { stringResource(R.string.select_room) },
                            color = ForestInk,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    DropdownMenu(
                        expanded = roomDropdownExpanded,
                        onDismissRequest = { roomDropdownExpanded = false }
                    ) {
                        rooms.forEach { room ->
                            DropdownMenuItem(
                                text = { Text(room.name) },
                                onClick = {
                                    currentRoomId = room.id
                                    roomDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recurrence
                Text(
                    text = stringResource(R.string.recurrence_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                val intervalOptions = listOf(
                    1 to stringResource(R.string.interval_daily),
                    2 to stringResource(R.string.interval_2days),
                    3 to stringResource(R.string.interval_3days),
                    7 to stringResource(R.string.interval_weekly),
                    14 to stringResource(R.string.interval_2weeks),
                    30 to stringResource(R.string.interval_monthly)
                )

                intervalOptions.forEach { (days, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                isCustomSelected = false
                                intervalDays = days
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !isCustomSelected && intervalDays == days,
                            onClick = {
                                isCustomSelected = false
                                intervalDays = days
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = SageGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, color = ForestInk)
                    }
                }

                // Custom Interval option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isCustomSelected = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isCustomSelected,
                        onClick = { isCustomSelected = true },
                        colors = RadioButtonDefaults.colors(selectedColor = SageGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.interval_custom, customIntervalText.toIntOrNull() ?: 1),
                        color = ForestInk
                    )
                }

                if (isCustomSelected) {
                    OutlinedTextField(
                        value = customIntervalText,
                        onValueChange = { customIntervalText = it.filter { ch -> ch.isDigit() } },
                        label = { Text(stringResource(R.string.custom_days_prompt)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, top = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageGreen,
                            unfocusedBorderColor = BorderLine,
                            focusedLabelColor = SageGreen
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.btn_cancel), color = ForestInk)
                    }

                    Button(
                        onClick = {
                            val finalInterval = if (isCustomSelected) {
                                (customIntervalText.toIntOrNull() ?: 1).coerceIn(1, 365)
                            } else {
                                intervalDays
                            }
                            if (title.isNotBlank() && currentRoomId > 0) {
                                onSave(title.trim(), currentRoomId, finalInterval)
                            }
                        },
                        enabled = title.isNotBlank() && currentRoomId > 0,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SageGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(stringResource(R.string.btn_save))
                    }
                }
            }
        }
    }
}
