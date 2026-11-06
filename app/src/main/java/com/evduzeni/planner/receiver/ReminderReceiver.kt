package com.evduzeni.planner.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.evduzeni.planner.MainActivity
import com.evduzeni.planner.R
import com.evduzeni.planner.data.database.AppDatabase
import com.evduzeni.planner.data.preferences.PreferencesManager
import com.evduzeni.planner.reminder.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val prefs = PreferencesManager(context).preferencesFlow.first()

                if (prefs.notificationsEnabled) {
                    val todayEpoch = System.currentTimeMillis() / 86400000L
                    val pendingTasks = db.taskDao().getPendingTasksForDaySync(todayEpoch)

                    if (pendingTasks.isNotEmpty()) {
                        com.evduzeni.planner.util.AppLanguageHelper.applyLocale(context, prefs.appLanguage)
                        showNotification(context, pendingTasks.map { it.title })
                    }

                    // Reschedule for next day
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

    private fun showNotification(context: Context, taskTitles: List<String>) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        val channelId = "evduzeni_reminder_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notif_channel_desc)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = context.getString(R.string.notif_title)
        val body = if (taskTitles.size == 1) {
            context.getString(R.string.notif_body_single, taskTitles.first())
        } else {
            val summary = taskTitles.take(2).joinToString(", ") + if (taskTitles.size > 2) "..." else ""
            context.getString(R.string.notif_body_multi, taskTitles.size, summary)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(101, notification)
    }
}
