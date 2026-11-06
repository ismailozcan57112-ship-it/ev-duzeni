package com.evduzeni.planner.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.evduzeni.planner.data.entity.RoomEntity
import com.evduzeni.planner.data.entity.TaskEntity

data class RoomWithTasks(
    @Embedded val room: RoomEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "roomId"
    )
    val tasks: List<TaskEntity>
)
