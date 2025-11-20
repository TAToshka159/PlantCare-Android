// utils/EmailHelper.kt
package com.example.plantcare.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object EmailHelper {

    private const val SUPPORT_EMAIL = "support@plantcare.com" // <-- Укажи нужный адрес
    private const val EMAIL_SUBJECT = "Обращение в поддержку PlantCare" // <-- Необязательно
    private const val EMAIL_BODY = "Здравствуйте, у меня возник вопрос..." // <-- Необязательно

    /**
     * Открывает диалог выбора приложения для отправки email
     *
     * @param context Контекст приложения
     */
    fun sendSupportEmail(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$SUPPORT_EMAIL") // Только для email
            putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT)
            putExtra(Intent.EXTRA_TEXT, EMAIL_BODY)
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Если не найдено ни одного приложения для email
            android.widget.Toast.makeText(context, "Не найдено приложение для отправки email", android.widget.Toast.LENGTH_LONG).show()
        }
    }
}