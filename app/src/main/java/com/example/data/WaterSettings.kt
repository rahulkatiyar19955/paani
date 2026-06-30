package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_settings")
data class WaterSettings(
    @PrimaryKey val id: Int = 1,
    val intervalMinutes: Int = 30, // 15, 30, or custom
    val nightModeEnabled: Boolean = false,
    val nightModeStartHour: Int = 22, // 10 PM
    val nightModeStartMin: Int = 0,
    val nightModeEndHour: Int = 7,    // 7 AM
    val nightModeEndMin: Int = 0,
    val currentStreak: Int = 0,
    val lastGoalMetDate: Long = 0L,   // timestamp of start of day when goal was last met
    val totalPoints: Int = 0,
    val dailyGoalMl: Int = 2500
)
