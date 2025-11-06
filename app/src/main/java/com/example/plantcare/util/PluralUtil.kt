// PluralUtil.kt
package com.example.plantcare.util

import kotlin.math.absoluteValue

object PluralUtil {
    fun daysUntil(days: Int): String {
        val d = days.absoluteValue
        return when {
            d % 10 == 1 && d % 100 != 11 -> "день"
            d % 10 in 2..4 && d !in 12..14 -> "дня"
            else -> "дней"
        }
    }

    // Опционально: можно использовать и для других слов (например, "фото")
}