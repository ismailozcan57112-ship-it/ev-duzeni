package com.evduzeni.planner.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = RoomEntity::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("roomId"),
        Index("dueDayEpoch")
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val roomId: Long,
    val title: String,
    val intervalDays: Int = 1,
    val dueDayEpoch: Long, // Epoch day (timestamp / 86400)
    val completedDayEpoch: Long? = null,
    val previousDueDayEpoch: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
