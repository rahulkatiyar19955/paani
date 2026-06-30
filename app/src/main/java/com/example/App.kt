package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.WaterRepository

class App : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { WaterRepository(database.waterDao()) }
}
