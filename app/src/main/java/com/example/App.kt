package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.AppDatabase
import com.example.data.WaterRepository
import com.example.services.AlarmReceiver

class App : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { WaterRepository(database.waterDao()) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Water Reminders"
            val descriptionText = "Reminders to drink water"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(AlarmReceiver.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
