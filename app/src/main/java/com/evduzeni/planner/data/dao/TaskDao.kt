package com.evduzeni.planner.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.evduzeni.planner.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDayEpoch ASC, id ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE roomId = :roomId ORDER BY dueDayEpoch ASC, id ASC")
    fun getTasksByRoom(roomId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE completedDayEpoch = :todayEpoch")
    fun getCompletedToday(todayEpoch: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE (completedDayEpoch IS NULL OR completedDayEpoch != :todayEpoch) AND dueDayEpoch <= :todayEpoch ORDER BY dueDayEpoch ASC")
    fun getPendingTasksUpTo(todayEpoch: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE (completedDayEpoch IS NULL OR completedDayEpoch != :todayEpoch) AND dueDayEpoch <= :todayEpoch")
    suspend fun getPendingTasksForDaySync(todayEpoch: Long): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTaskCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>): List<Long>

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
