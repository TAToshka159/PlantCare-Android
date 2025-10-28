// PlantCareApplication.kt
package com.example.plantcare

import android.app.Application
import com.example.plantcare.data.database.AppDatabase
import com.example.plantcare.data.database.DatabaseInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlantCareApplication : Application() {
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)

        // Инициализируем админа в фоне
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseInitializer.ensureDefaultAdmin(database)
        }
    }
}