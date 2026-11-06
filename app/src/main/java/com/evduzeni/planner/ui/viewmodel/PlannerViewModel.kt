package com.evduzeni.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.evduzeni.planner.R
import com.evduzeni.planner.data.backup.BackupManager
import com.evduzeni.planner.data.database.AppDatabase
import com.evduzeni.planner.data.entity.RoomEntity
import com.evduzeni.planner.data.entity.TaskEntity
import com.evduzeni.planner.data.model.RoomWithTasks
import com.evduzeni.planner.data.preferences.AppPreferences
import com.evduzeni.planner.data.preferences.PreferencesManager
import com.evduzeni.planner.reminder.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val preferencesManager = PreferencesManager(application)
    private val backupManager = BackupManager(database)

    val todayEpoch: Long
        get() = System.currentTimeMillis() / 86400000L

    val preferences: StateFlow<AppPreferences> = preferencesManager.preferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppPreferences(
            onboardingDone = false,
            notificationsEnabled = false,
            reminderHour = 9,
            reminderMinute = 0,
            promoDialogShown = false,
            hasProLifetime = true,
            appLanguage = ""
        )
    )

    val allRooms: StateFlow<List<RoomEntity>> = database.roomDao().getAllRooms().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val roomsWithTasks: StateFlow<List<RoomWithTasks>> = database.roomDao().getRoomsWithTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pendingTodayTasks: StateFlow<List<TaskEntity>> = database.taskDao().getPendingTasksUpTo(todayEpoch).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val completedTodayTasks: StateFlow<List<TaskEntity>> = database.taskDao().getCompletedToday(todayEpoch).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleTaskComplete(task: TaskEntity) {
        viewModelScope.launch {
            val isCompleted = task.completedDayEpoch == todayEpoch
            val updatedTask = if (isCompleted) {
                // Undo completion
                task.copy(
                    completedDayEpoch = null,
                    dueDayEpoch = task.previousDueDayEpoch ?: todayEpoch,
                    previousDueDayEpoch = null
                )
            } else {
                // Mark completed and schedule next cycle
                task.copy(
                    completedDayEpoch = todayEpoch,
                    previousDueDayEpoch = task.dueDayEpoch,
                    dueDayEpoch = todayEpoch + task.intervalDays
                )
            }
            database.taskDao().updateTask(updatedTask)
        }
    }

    fun saveTask(title: String, roomId: Long, intervalDays: Int, existingTask: TaskEntity? = null) {
        viewModelScope.launch {
            if (existingTask != null) {
                val updated = existingTask.copy(
                    title = title,
                    roomId = roomId,
                    intervalDays = intervalDays
                )
                database.taskDao().updateTask(updated)
            } else {
                val newTask = TaskEntity(
                    roomId = roomId,
                    title = title,
                    intervalDays = intervalDays,
                    dueDayEpoch = todayEpoch
                )
                database.taskDao().insertTask(newTask)
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            database.taskDao().deleteTask(task)
        }
    }

    fun saveRoom(name: String, existingRoom: RoomEntity? = null) {
        viewModelScope.launch {
            if (existingRoom != null) {
                database.roomDao().updateRoom(existingRoom.copy(name = name))
            } else {
                val currentCount = database.roomDao().getRoomCountSync()
                database.roomDao().insertRoom(RoomEntity(name = name, sortOrder = currentCount))
            }
        }
    }

    fun deleteRoom(room: RoomEntity) {
        viewModelScope.launch {
            database.roomDao().deleteRoom(room)
        }
    }

    fun setOnboardingDone(done: Boolean) {
        viewModelScope.launch {
            preferencesManager.setOnboardingDone(done)
        }
    }

    fun setPromoDialogShown(shown: Boolean) {
        viewModelScope.launch {
            preferencesManager.setPromoDialogShown(shown)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
            val prefs = preferences.value
            if (enabled) {
                ReminderScheduler.scheduleDailyReminder(
                    getApplication(),
                    prefs.reminderHour,
                    prefs.reminderMinute
                )
            } else {
                ReminderScheduler.cancelReminder(getApplication())
            }
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesManager.setReminderTime(hour, minute)
            if (preferences.value.notificationsEnabled) {
                ReminderScheduler.scheduleDailyReminder(getApplication(), hour, minute)
            }
        }
    }

    fun setAppLanguage(languageCode: String) {
        viewModelScope.launch {
            preferencesManager.setAppLanguage(languageCode)
        }
    }

    fun setupSamplePlan() {
        viewModelScope.launch {
            val app = getApplication<Application>()
            com.evduzeni.planner.util.AppLanguageHelper.applyLocale(app, preferences.value.appLanguage)
            val rKitchen = database.roomDao().insertRoom(RoomEntity(name = app.getString(R.string.room_kitchen), sortOrder = 0))
            val rLiving = database.roomDao().insertRoom(RoomEntity(name = app.getString(R.string.room_living), sortOrder = 1))
            val rBathroom = database.roomDao().insertRoom(RoomEntity(name = app.getString(R.string.room_bathroom), sortOrder = 2))
            val rBedroom = database.roomDao().insertRoom(RoomEntity(name = app.getString(R.string.room_bedroom), sortOrder = 3))

            val starterTasks = listOf(
                TaskEntity(roomId = rKitchen, title = app.getString(R.string.task_wipe_counters), intervalDays = 1, dueDayEpoch = todayEpoch),
                TaskEntity(roomId = rKitchen, title = app.getString(R.string.task_take_trash), intervalDays = 2, dueDayEpoch = todayEpoch),
                TaskEntity(roomId = rLiving, title = app.getString(R.string.task_vacuum_living), intervalDays = 3, dueDayEpoch = todayEpoch),
                TaskEntity(roomId = rLiving, title = app.getString(R.string.task_dust_surfaces), intervalDays = 7, dueDayEpoch = todayEpoch),
                TaskEntity(roomId = rBathroom, title = app.getString(R.string.task_clean_sink), intervalDays = 3, dueDayEpoch = todayEpoch),
                TaskEntity(roomId = rBathroom, title = app.getString(R.string.task_mop_bathroom), intervalDays = 7, dueDayEpoch = todayEpoch),
                TaskEntity(roomId = rBedroom, title = app.getString(R.string.task_change_sheets), intervalDays = 7, dueDayEpoch = todayEpoch),
                TaskEntity(roomId = rBedroom, title = app.getString(R.string.task_ventilate), intervalDays = 1, dueDayEpoch = todayEpoch)
            )
            database.taskDao().insertTasks(starterTasks)
            setOnboardingDone(true)
        }
    }

    suspend fun exportBackupJson(): String {
        return backupManager.exportJson()
    }

    suspend fun importBackupJson(json: String): Boolean {
        return backupManager.importJson(json)
    }
}
