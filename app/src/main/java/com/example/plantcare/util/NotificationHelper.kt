package com.example.plantcare.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.plantcare.MainActivity
import com.example.plantcare.R

object NotificationHelper {

    private const val CHANNEL_ID = "PlantCareNotifications"
    private const val NOTIFICATION_ID = 1001 // Основное уведомление (в день ухода)
    private const val TOMORROW_NOTIFICATION_ID = 1002 // Уведомление за 1 день
    private const val THREE_DAYS_NOTIFICATION_ID = 1003 // Уведомление за 3 дня

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Напоминания о растениях",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления о поливе и удобрении растений"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun sendCareReminderNotification(context: Context, plantName: String, eventType: String /* "watering" или "fertilizing" */) {
        val (title, content) = when (eventType) {
            "watering" -> Pair(
                "Пора полить!",
                "Полей меня 😞\nПора полить $plantName"
            )
            "fertilizing" -> Pair(
                "Пора удобрить!",
                "Покорми меня 😞\nПора удобрить $plantName"
            )
            else -> Pair("Напоминание", "Пора ухаживать за $plantName")
        }

        if (hasNotificationPermission(context)) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Убедись, что у тебя есть такая иконка
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build()) // Используем постоянный ID для основного уведомления
            }
        } else {
            // Логируем, что уведомление не отправлено
            println("DEBUG: Notifications permission not granted, cannot send notification.")
        }
    }

    @SuppressLint("MissingPermission")
    fun sendTomorrowReminderNotification(context: Context, plantName: String, eventType: String /* "watering" или "fertilizing" */) {
        val action = if (eventType == "watering") "полив" else "удобрение"
        val title = "$plantName: завтра $action"
        val content = "Завтра пора $action для $plantName"

        if (hasNotificationPermission(context)) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Убедись, что у тебя есть такая иконка
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_LOW) // Низкий приоритет для напоминаний
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(TOMORROW_NOTIFICATION_ID, builder.build()) // Отдельный ID
            }
        } else {
            // Логируем, что уведомление не отправлено
            println("DEBUG: Notifications permission not granted, cannot send notification.")
        }
    }

    @SuppressLint("MissingPermission")
    fun sendInThreeDaysReminderNotification(context: Context, plantName: String, eventType: String /* "watering" или "fertilizing" */) {
        val action = if (eventType == "watering") "полив" else "удобрение"
        val title = "$plantName: через 3 дня $action"
        val content = "Через 3 дня пора $action для $plantName"

        if (hasNotificationPermission(context)) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Убедись, что у тебя есть такая иконка
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_LOW) // Низкий приоритет для напоминаний
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(THREE_DAYS_NOTIFICATION_ID, builder.build()) // Отдельный ID
            }
        } else {
            // Логируем, что уведомление не отправлено
            println("DEBUG: Notifications permission not granted, cannot send notification.")
        }
    }

    // Проверяем, есть ли разрешение на уведомления
    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            // На старых версиях разрешение есть по умолчанию
            true
        }
    }
}