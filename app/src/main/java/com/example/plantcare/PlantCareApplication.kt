// PlantCareApplication.kt
package com.example.plantcare

import android.app.Application
import android.util.Log
import androidx.work.*
import com.example.plantcare.data.database.AppDatabase
import com.example.plantcare.data.database.DatabaseInitializer
import com.example.plantcare.worker.ReminderWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import java.util.*

class PlantCareApplication : Application() {
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)

        // Инициализируем админа и энциклопедию в фоне
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseInitializer.ensureDefaultAdmin(database)
            DatabaseInitializer.ensureEncyclopediaEntries(database)
        }

        // --- НАСТРАИВАЕМ РАСПИСАНИЕ ДЛЯ ReminderWorker ---
        scheduleDailyReminder()
        // --- /НАСТРАИВАЕМ РАСПИСАНИЕ ДЛЯ ReminderWorker ---
    }

    private fun scheduleDailyReminder() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        // Рассчитываем время до следующего 00:00 по Московскому времени
        val now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
        val nextRun = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))

        nextRun.set(Calendar.HOUR_OF_DAY, 0)
        nextRun.set(Calendar.MINUTE, 0)
        nextRun.set(Calendar.SECOND, 0)
        nextRun.set(Calendar.MILLISECOND, 0)

        // Если текущее время уже прошло 00:00, ставим на следующий день
        if (now.timeInMillis >= nextRun.timeInMillis) {
            nextRun.add(Calendar.DAY_OF_MONTH, 1)
        }

        val initialDelay = nextRun.timeInMillis - now.timeInMillis

        Log.d("PlantCareApp", "DEBUG: Scheduling ReminderWorker to run in $initialDelay ms (at ${Date(nextRun.timeInMillis)})")

        val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyReminder",
            ExistingPeriodicWorkPolicy.KEEP, // Не перезаписывать, если уже запланирован
            reminderRequest
        )
    }
}