// ReminderWorker.kt
package com.example.plantcare.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.plantcare.PlantCareApplication
import com.example.plantcare.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class ReminderWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val app = context.applicationContext as PlantCareApplication
        val dao = app.database.plantCareDao()

        return withContext(Dispatchers.IO) {
            try {
                val now = System.currentTimeMillis()
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
                calendar.timeInMillis = now
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDay = calendar.timeInMillis

                calendar.add(Calendar.DAY_OF_MONTH, 1)
                val endOfDay = calendar.timeInMillis

                // Получаем события, запланированные на сегодня
                val events = dao.getUpcomingCareEventsForPeriod(startOfDay, endOfDay)

                events.forEach { event ->
                    val plant = dao.getPlantById(event.plantId)

                    if (plant != null) {
                        // Отправляем уведомление
                        NotificationHelper.sendCareReminderNotification(context, plant.name, event.type)
                    }
                }

                // Также проверяем, есть ли события на завтра или через 3 дня
                val tomorrow = startOfDay + 24 * 60 * 60 * 1000
                val dayAfterTomorrow = tomorrow + 24 * 60 * 60 * 1000
                val inThreeDays = dayAfterTomorrow + 24 * 60 * 60 * 1000

                // Уведомления за 1 день
                val tomorrowEvents = dao.getUpcomingCareEventsForPeriod(tomorrow, tomorrow + 24 * 60 * 60 * 1000)
                tomorrowEvents.forEach { event ->
                    val plant = dao.getPlantById(event.plantId)
                    if (plant != null) {
                        NotificationHelper.sendTomorrowReminderNotification(context, plant.name, event.type)
                    }
                }

                // Уведомления за 3 дня
                val inThreeDaysEvents = dao.getUpcomingCareEventsForPeriod(inThreeDays, inThreeDays + 24 * 60 * 60 * 1000)
                inThreeDaysEvents.forEach { event ->
                    val plant = dao.getPlantById(event.plantId)
                    if (plant != null) {
                        NotificationHelper.sendInThreeDaysReminderNotification(context, plant.name, event.type)
                    }
                }

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }
}