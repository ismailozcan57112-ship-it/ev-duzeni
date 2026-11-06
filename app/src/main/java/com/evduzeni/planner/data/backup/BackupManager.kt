package com.evduzeni.planner.data.backup

import com.evduzeni.planner.data.database.AppDatabase
import com.evduzeni.planner.data.entity.RoomEntity
import com.evduzeni.planner.data.entity.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class BackupManager(private val database: AppDatabase) {

    suspend fun exportJson(): String = withContext(Dispatchers.IO) {
        val rooms = database.roomDao().getAllRooms().first()
        val tasks = database.taskDao().getAllTasks().first()

        val root = JSONObject()
        root.put("version", 2)
        root.put("app", "EvDuzeni")

        val roomsArray = JSONArray()
        for (room in rooms) {
            val roomObj = JSONObject()
            roomObj.put("id", room.id.toString())
            roomObj.put("name", room.name)
            roomObj.put("sort_order", room.sortOrder)
            roomsArray.put(roomObj)
        }
        root.put("rooms", roomsArray)

        val tasksArray = JSONArray()
        for (task in tasks) {
            val taskObj = JSONObject()
            taskObj.put("id", task.id.toString())
            taskObj.put("room_id", task.roomId.toString())
            taskObj.put("title", task.title)
            taskObj.put("interval_days", task.intervalDays)
            taskObj.put("due_day", task.dueDayEpoch)
            if (task.completedDayEpoch != null) {
                taskObj.put("completed_day", task.completedDayEpoch)
            }
            tasksArray.put(taskObj)
        }
        root.put("tasks", tasksArray)

        return@withContext root.toString(2)
    }

    suspend fun importJson(jsonContent: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonContent)
            val roomsArray = root.optJSONArray("rooms") ?: return@withContext false
            val tasksArray = root.optJSONArray("tasks") ?: return@withContext false

            val roomOldToNewId = mutableMapOf<String, Long>()
            val newRooms = mutableListOf<RoomEntity>()

            for (i in 0 until roomsArray.length()) {
                val obj = roomsArray.getJSONObject(i)
                val oldId = obj.optString("id", "")
                val name = obj.optString("name", "").trim()
                if (name.isNotEmpty()) {
                    newRooms.add(RoomEntity(name = name, sortOrder = i))
                }
            }

            if (newRooms.isEmpty()) return@withContext false

            // Clear old data and insert new
            database.taskDao().deleteAllTasks()
            database.roomDao().deleteAllRooms()

            for (i in 0 until newRooms.size) {
                val room = newRooms[i]
                val newId = database.roomDao().insertRoom(room)
                val oldId = roomsArray.getJSONObject(i).optString("id", "")
                if (oldId.isNotEmpty()) {
                    roomOldToNewId[oldId] = newId
                }
            }

            val newTasks = mutableListOf<TaskEntity>()
            for (i in 0 until tasksArray.length()) {
                val obj = tasksArray.getJSONObject(i)
                val oldRoomId = obj.optString("room_id", "")
                val newRoomId = roomOldToNewId[oldRoomId] ?: continue
                val title = obj.optString("title", "").trim()
                val intervalDays = obj.optInt("interval_days", 1).coerceIn(1, 365)
                val dueDay = obj.optLong("due_day", System.currentTimeMillis() / 86400000L)
                val completedDay = if (obj.has("completed_day")) obj.optLong("completed_day") else null

                if (title.isNotEmpty()) {
                    newTasks.add(
                        TaskEntity(
                            roomId = newRoomId,
                            title = title,
                            intervalDays = intervalDays,
                            dueDayEpoch = dueDay,
                            completedDayEpoch = completedDay
                        )
                    )
                }
            }

            database.taskDao().insertTasks(newTasks)
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
}
