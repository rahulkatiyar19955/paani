package com.example.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.App
import com.example.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_WATER_REMINDER = "com.example.ACTION_WATER_REMINDER"
        const val REQUEST_CODE = 1001
        const val NOTIFICATION_ID = 2001
        const val CHANNEL_ID = "water_reminder_channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleFromBoot(context)
            return
        }

        if (intent.action == ACTION_WATER_REMINDER) {
            handleReminder(context)
        }
    }

    private fun rescheduleFromBoot(context: Context) {
        val app = context.applicationContext as App
        CoroutineScope(Dispatchers.IO).launch {
            val settings = app.repository.getSettingsSync()
            AlarmScheduler.scheduleNextAlarm(context, settings.intervalMinutes)
        }
    }

    private fun handleReminder(context: Context) {
        val app = context.applicationContext as App
        CoroutineScope(Dispatchers.IO).launch {
            val settings = app.repository.getSettingsSync()
            
            // Check DND / Silence Window
            if (isSilenceWindow(settings)) {
                // Silently reschedule
                AlarmScheduler.scheduleNextAlarm(context, settings.intervalMinutes)
                return@launch
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val filter = notificationManager.currentInterruptionFilter
                if (filter == NotificationManager.INTERRUPTION_FILTER_NONE || filter == NotificationManager.INTERRUPTION_FILTER_ALARMS) {
                    // Respect system DND
                    AlarmScheduler.scheduleNextAlarm(context, settings.intervalMinutes)
                    return@launch
                }
            }

            showNotification(context)
            // Schedule the next normal interval
            AlarmScheduler.scheduleNextAlarm(context, settings.intervalMinutes)
        }
    }

    private fun isSilenceWindow(settings: com.example.data.WaterSettings): Boolean {
        if (!settings.nightModeEnabled) return false

        val cal = Calendar.getInstance()
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMin = cal.get(Calendar.MINUTE)
        val currentMinutes = currentHour * 60 + currentMin

        val startMinutes = settings.nightModeStartHour * 60 + settings.nightModeStartMin
        val endMinutes = settings.nightModeEndHour * 60 + settings.nightModeEndMin

        return if (startMinutes < endMinutes) {
            currentMinutes in startMinutes..endMinutes
        } else {
            // crosses midnight
            currentMinutes >= startMinutes || currentMinutes <= endMinutes
        }
    }

    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Water Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to drink water"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val contentIntent = Intent(context, MainActivity::class.java)
        val pendingContent = PendingIntent.getActivity(
            context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val logIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_LOG_WATER
        }
        val pendingLog = PendingIntent.getBroadcast(
            context, 1, logIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_SNOOZE
        }
        val pendingSnooze = PendingIntent.getBroadcast(
            context, 2, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Fallback icon
            .setContentTitle("Time to hydrate!")
            .setContentText("Grab a glass of water to keep your streak going.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingContent)
            .addAction(android.R.drawable.ic_input_add, "Log 250ml", pendingLog)
            .addAction(android.R.drawable.ic_popup_reminder, "Snooze 10m", pendingSnooze)

        // For the "nag" feature, we could set a delete intent
        val deleteIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_DISMISSED
        }
        val pendingDelete = PendingIntent.getBroadcast(
            context, 3, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.setDeleteIntent(pendingDelete)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}
