// PlantMood.kt
package com.example.plantcare.util

import com.example.plantcare.data.database.entity.CareEvent

object PlantMood {
    fun getMood(careEvents: List<CareEvent>): String {
        if (careEvents.isEmpty()) return "🙂"

        val now = System.currentTimeMillis()
        var isOverdue = false
        var isToday = false

        for (event in careEvents) {
            val daysDiff = ((event.datePlanned - now) / (1000 * 60 * 60 * 24)).toInt()
            when {
                daysDiff < 0 -> isOverdue = true
                daysDiff == 0 -> isToday = true
            }
        }

        return when {
            isOverdue -> "😢"
            isToday -> "😐"
            else -> "🙂"
        }
    }
}