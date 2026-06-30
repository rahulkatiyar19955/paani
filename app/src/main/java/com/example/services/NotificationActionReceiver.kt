package com.example.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_LOG_WATER = "com.example.ACTION_LOG_WATER"
        const val ACTION_SNOOZE = "com.example.ACTION_SNOOZE"
        const val ACTION_DISMISSED = "com.example.ACTION_DISMISSED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as App
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent.action) {
            ACTION_LOG_WATER -> {
                CoroutineScope(Dispatchers.IO).launch {
                    app.repository.addWater(250)
                }
                notificationManager.cancel(AlarmReceiver.NOTIFICATION_ID)
            }
            ACTION_SNOOZE -> {
                AlarmScheduler.scheduleNextAlarm(context, 10)
                notificationManager.cancel(AlarmReceiver.NOTIFICATION_ID)
            }
            ACTION_DISMISSED -> {
                // Nag after 15 minutes
                AlarmScheduler.scheduleNextAlarm(context, 15)
            }
        }
    }
}
