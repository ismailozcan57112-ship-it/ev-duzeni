package com.evduzeni.planner.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.evduzeni.planner.data.entity.RoomEntity
import com.evduzeni.planner.data.model.RoomWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Query("SELECT * FROM rooms ORDER BY sortOrder ASC, id ASC")
    fun getAllRooms(): Flow<List<RoomEntity>>

    @Transaction
    @Query("SELECT * FROM rooms ORDER BY sortOrder ASC, id ASC")
    fun getRoomsWithTasks(): Flow<List<RoomWithTasks>>

    @Query("SELECT * FROM rooms WHERE id = :roomId LIMIT 1")
    suspend fun getRoomById(roomId: Long): RoomEntity?

    @Query("SELECT COUNT(*) FROM rooms")
    fun getRoomCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM rooms")
    suspend fun getRoomCountSync(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<RoomEntity>): List<Long>

    @Update
    suspend fun updateRoom(room: RoomEntity)

    @Delete
    suspend fun deleteRoom(room: RoomEntity)

    @Query("DELETE FROM rooms")
    suspend fun deleteAllRooms()
}
