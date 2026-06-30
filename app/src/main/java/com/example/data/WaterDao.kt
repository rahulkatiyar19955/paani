package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_records WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay ORDER BY timestamp DESC")
    fun getRecordsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WaterRecord>>

    @Query("SELECT * FROM water_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<WaterRecord>>

    @Insert
    suspend fun insertRecord(record: WaterRecord)

    @Query("SELECT * FROM water_settings WHERE id = 1")
    fun getSettings(): Flow<WaterSettings?>

    @Query("SELECT * FROM water_settings WHERE id = 1")
    suspend fun getSettingsSync(): WaterSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: WaterSettings)
}
