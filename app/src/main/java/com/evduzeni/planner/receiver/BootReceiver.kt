package com.evduzeni.planner.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.evduzeni.planner.data.preferences.PreferencesManager
import com.evduzeni.planner.reminder.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val prefs = PreferencesManager(context).preferencesFlow.first()
                    if (prefs.notificationsEnabled) {
                        ReminderScheduler.scheduleDailyReminder(
                            context,
                            prefs.reminderHour,
                            prefs.reminderMinute
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
