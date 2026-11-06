package com.evduzeni.planner.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evduzeni.planner.R
import com.evduzeni.planner.data.entity.TaskEntity
import com.evduzeni.planner.ui.theme.BorderLine
import com.evduzeni.planner.ui.theme.ForestInk
import com.evduzeni.planner.ui.theme.LightMint
import com.evduzeni.planner.ui.theme.SageGreen
import com.evduzeni.planner.ui.theme.SlateMuted
import com.evduzeni.planner.ui.theme.SuccessGreen
import com.evduzeni.planner.ui.theme.TerracottaOrange

@Composable
fun TaskCard(
    task: TaskEntity,
    roomName: String,
    isCompletedToday: Boolean,
    todayEpoch: Long,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val isOverdue = !isCompletedToday && task.dueDayEpoch < todayEpoch
    val isDueToday = !isCompletedToday && task.dueDayEpoch == todayEpoch

    val checkBgColor by animateColorAsState(
        targetValue = if (isCompletedToday) SuccessGreen else Color.Transparent,
        label = "checkBgColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggleComplete() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompletedToday) LightMint.copy(alpha = 0.35f) else Color.White
        ),
        border = BorderStroke(1.dp, BorderLine),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox button
            Surface(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { onToggleComplete() },
                shape = CircleShape,
                color = checkBgColor,
                border = BorderStroke(
                    width = 2.dp,
                    color = if (isCompletedToday) SuccessGreen else SageGreen
                )
            ) {
                if (isCompletedToday) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.undo),
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title and metadata
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCompletedToday) SlateMuted else ForestInk,
                    textDecoration = if (isCompletedToday) TextDecoration.LineThrough else TextDecoration.None
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Room badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = LightMint
                    ) {
                        Text(
                            text = roomName,
                            style = MaterialTheme.typography.labelSmall,
                            color = ForestInk,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Due badge
                    if (isOverdue) {
                        val days = (todayEpoch - task.dueDayEpoch).toInt()
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = TerracottaOrange.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = stringResource(R.string.due_overdue_badge, days),
                                style = MaterialTheme.typography.labelSmall,
                                color = TerracottaOrange,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else if (isDueToday) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SageGreen.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = stringResource(R.string.due_today_badge),
                                style = MaterialTheme.typography.labelSmall,
                                color = SageGreen,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else if (!isCompletedToday && task.dueDayEpoch > todayEpoch) {
                        val days = (task.dueDayEpoch - todayEpoch).toInt()
                        Text(
                            text = stringResource(R.string.due_in_days, days),
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateMuted
                        )
                    }
                }
            }

            // More menu
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = SlateMuted
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.dialog_edit_task)) },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.btn_delete), color = TerracottaOrange) },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
