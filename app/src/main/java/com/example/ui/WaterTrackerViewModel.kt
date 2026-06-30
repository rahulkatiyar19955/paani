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
        }
    }

    fun deleteRecord(recordId: Int, amountMl: Int, timestamp: Long) {
        viewModelScope.launch {
            repository.deleteRecord(recordId, amountMl, timestamp)
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
