package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.App
import com.example.data.WaterRecord
import com.example.data.WaterSettings
import com.example.services.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class WaterTrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as App
    private val repository = app.repository

    val settings: StateFlow<WaterSettings> = repository.currentSettings
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            WaterSettings()
        )

    val todayRecords: StateFlow<List<WaterRecord>> = repository.getTodayRecords()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val allRecords: StateFlow<List<WaterRecord>> = repository.getAllRecords()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val currentIntakeMl: StateFlow<Int> = todayRecords.map { records ->
        records.sumOf { it.amountMl }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.addWater(amountMl)
            
            // Check gamification
            val currentIntake = currentIntakeMl.value + amountMl
            val currentSettings = settings.value
            
            if (currentIntake >= currentSettings.dailyGoalMl) {
                // Determine start of day for last goal met
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val todayStart = calendar.timeInMillis
                
                if (currentSettings.lastGoalMetDate < todayStart) {
                    // Goal hit for the first time today
                    // Add streak and bonus points
                    val streak = currentSettings.currentStreak + 1
                    val newPoints = currentSettings.totalPoints + 50
                    val updated = currentSettings.copy(
                        currentStreak = streak,
                        totalPoints = newPoints,
                        lastGoalMetDate = todayStart
                    )
                    repository.updateSettings(updated)
                }
            }
        }
    }

    fun updateSettings(newSettings: WaterSettings) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
            // Schedule the alarm with the new interval
            AlarmScheduler.scheduleNextAlarm(getApplication(), newSettings.intervalMinutes)
        }
    }
}
