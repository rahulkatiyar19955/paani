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
        
        // Check if daily goal is hit
        // Need to calculate current today's intake
        // We'll calculate it in the view model, but for instant reward let's do a simple calculation here
        // Actually, we can fetch today's records sync or just let the viewmodel handle it?
        // Wait, a quick way to know is by querying records synchronously, but we don't have a sync query.
        // Let's add gamification update here or in the VM.
        // It's cleaner to handle this gamification via the ViewModel or a UseCase, but we'll do it simple:
        
        waterDao.updateSettings(settings.copy(totalPoints = newPoints))
    }
    
    suspend fun updateSettings(settings: WaterSettings) {
        waterDao.updateSettings(settings)
    }
    
    suspend fun getSettingsSync(): WaterSettings {
        return waterDao.getSettingsSync() ?: WaterSettings()
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
