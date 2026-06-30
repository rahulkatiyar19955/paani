package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class WaterRepository(private val waterDao: WaterDao) {
    
    val currentSettings: Flow<WaterSettings> = waterDao.getSettings().map { it ?: WaterSettings() }

    fun getTodayRecords(): Flow<List<WaterRecord>> {
        val (start, end) = getStartAndEndOfDay()
        return waterDao.getRecordsForDay(start, end)
    }

    fun getAllRecords(): Flow<List<WaterRecord>> {
        return waterDao.getAllRecords()
    }

    suspend fun addWater(amountMl: Int) {
        val now = System.currentTimeMillis()
        waterDao.insertRecord(WaterRecord(amountMl = amountMl, timestamp = now))
        
        // Update gamification points and streak
        val settings = waterDao.getSettingsSync() ?: WaterSettings()
        var newPoints = settings.totalPoints + 10 // 10 points per log
        var currentStreak = settings.currentStreak
        var lastGoalMetDate = settings.lastGoalMetDate
        
        // Calculate today's intake
        val (start, end) = getStartAndEndOfDay()
        val todayIntake = waterDao.getTodayIntakeSync(start, end) ?: 0
        
        if (todayIntake >= settings.dailyGoalMl) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val todayStart = calendar.timeInMillis
            
            if (settings.lastGoalMetDate < todayStart) {
                // Goal hit for the first time today
                currentStreak += 1
                newPoints += 50 // 50 points bonus
                lastGoalMetDate = todayStart
            }
        }
        
        waterDao.updateSettings(settings.copy(
            totalPoints = newPoints,
            currentStreak = currentStreak,
            lastGoalMetDate = lastGoalMetDate
        ))
    }
    
    suspend fun updateSettings(settings: WaterSettings) {
        waterDao.updateSettings(settings)
    }
    
    suspend fun getSettingsSync(): WaterSettings {
        return waterDao.getSettingsSync() ?: WaterSettings()
    }

    suspend fun deleteRecord(recordId: Int, amountMl: Int, recordTimestamp: Long) {
        waterDao.deleteRecordById(recordId)
        
        val settings = waterDao.getSettingsSync() ?: WaterSettings()
        var newPoints = maxOf(0, settings.totalPoints - 10) // Deduct base points
        var currentStreak = settings.currentStreak
        var lastGoalMetDate = settings.lastGoalMetDate
        
        val (start, end) = getStartAndEndOfDay()
        if (recordTimestamp in start..end) {
            val todayIntake = waterDao.getTodayIntakeSync(start, end) ?: 0
            if (todayIntake < settings.dailyGoalMl) {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val todayStart = calendar.timeInMillis
                
                if (settings.lastGoalMetDate == todayStart) {
                    currentStreak = maxOf(0, currentStreak - 1)
                    newPoints = maxOf(0, newPoints - 50) // Deduct bonus points
                    lastGoalMetDate = 0L // Reset last goal met date
                }
            }
        }
        
        waterDao.updateSettings(settings.copy(
            totalPoints = newPoints,
            currentStreak = currentStreak,
            lastGoalMetDate = lastGoalMetDate
        ))
    }

    private fun getStartAndEndOfDay(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis
        return Pair(start, end)
    }
}
