package com.example.plantcare.util

import com.example.plantcare.data.database.entity.CareEvent

object PlantMoodUtil {

    fun getMood(events: List<CareEvent>): String {
        if (events.isEmpty()) return "😐" // Нейтрально, если нет событий

        val now = System.currentTimeMillis()

        var overdueCount = 0
        var doneRecentlyCount = 0
        var upcomingSoonCount = 0

        events.forEach { event ->
            val plannedTime = event.datePlanned

            if (event.dateDone != null) {
                // Если событие выполнено недавно (например, в течение 24 часов)
                if (now - event.dateDone!! < 24 * 60 * 60 * 1000) {
                    doneRecentlyCount++
                }
            } else if (plannedTime < now) {
                // Просрочено
                overdueCount++
            } else {
                // Планируется в ближайшие 2 дня
                val diffDays = (plannedTime - now) / (24 * 60 * 60 * 1000)
                if (diffDays <= 2) {
                    upcomingSoonCount++
                }
            }
        }

        return when {
            overdueCount > 0 -> "😢"
            doneRecentlyCount > 0 -> "🥰"
            upcomingSoonCount > 0 -> "😐"
            else -> "😊"
        }
    }
}