package com.example.plantcare.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.plantcare.PlantCareApplication
import com.example.plantcare.data.database.entity.CareEvent
import com.example.plantcare.data.database.entity.Plant
import com.example.plantcare.util.NotificationHelper
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevToolsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PlantCareApplication
    val dao = app.database.plantCareDao()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Для разработчиков") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Тестирование уведомлений",
                style = MaterialTheme.typography.headlineMedium
            )

            // --- ТЕСТИРОВАНИЕ УВЕДОМЛЕНИЙ ---
            Text(
                text = "Ручная отправка уведомлений:",
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = {
                    NotificationHelper.sendCareReminderNotification(
                        context,
                        "Тестовое растение",
                        "watering"
                    )
                }
            ) {
                Text("Отправить уведомление: Пора полить!")
            }

            Button(
                onClick = {
                    NotificationHelper.sendTomorrowReminderNotification(
                        context,
                        "Тестовое растение",
                        "watering"
                    )
                }
            ) {
                Text("Отправить уведомление: Завтра полив")
            }

            Button(
                onClick = {
                    NotificationHelper.sendInThreeDaysReminderNotification(
                        context,
                        "Тестовое растение",
                        "fertilizing"
                    )
                }
            ) {
                Text("Отправить уведомление: Через 3 дня удобрение")
            }

            // --- ТЕСТИРОВАНИЕ УВЕДОМЛЕНИЙ ПО ТАЙМЕРУ ---
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Тестирование по таймеру (установка даты):",
                style = MaterialTheme.typography.titleMedium
            )

            // Кнопка: Установить полив на сегодня (0 дней)
            Button(
                onClick = {
                    runBlocking {
                        val testPlant = createOrGetTestPlant(dao)
                        val today = System.currentTimeMillis()
                        updateCareEventDate(dao, testPlant.id, "watering", today)
                    }
                }
            ) {
                Text("Полив сегодня (0 дней)")
            }

            // Кнопка: Установить полив на завтра (1 день)
            Button(
                onClick = {
                    runBlocking {
                        val testPlant = createOrGetTestPlant(dao)
                        val tomorrow = System.currentTimeMillis() + 24L * 60 * 60 * 1000
                        updateCareEventDate(dao, testPlant.id, "watering", tomorrow)
                    }
                }
            ) {
                Text("Полив завтра (1 день)")
            }

            // Кнопка: Установить полив через 3 дня
            Button(
                onClick = {
                    runBlocking {
                        val testPlant = createOrGetTestPlant(dao)
                        val inThreeDays = System.currentTimeMillis() + 3L * 24 * 60 * 60 * 1000
                        updateCareEventDate(dao, testPlant.id, "watering", inThreeDays)
                    }
                }
            ) {
                Text("Полив через 3 дня")
            }

            // Кнопка: Установить удобрение через 3 дня
            Button(
                onClick = {
                    runBlocking {
                        val testPlant = createOrGetTestPlant(dao)
                        val inThreeDays = System.currentTimeMillis() + 3L * 24 * 60 * 60 * 1000
                        updateCareEventDate(dao, testPlant.id, "fertilizing", inThreeDays)
                    }
                }
            ) {
                Text("Удобрение через 3 дня")
            }
        }
    }
}

// Вспомогательная функция для создания/получения тестового растения
private suspend fun createOrGetTestPlant(dao: com.example.plantcare.data.database.dao.PlantCareDao): Plant {
    var testPlant = dao.getPlantByNameAndUserId("Тестовое растение", 1) // Предполагаем, что админ = userId 1

    if (testPlant == null) {
        val newPlant = Plant(
            id = System.currentTimeMillis(),
            userId = 1,
            name = "Тестовое растение",
            type = "Фикус Бенджамина", // Используем тип, который есть в энциклопедии
            photoUri = null,
            room = "Тест",
            createdAt = System.currentTimeMillis(),
            wateringInterval = 7,
            fertilizingInterval = 30
        )
        val plantId = dao.insertPlant(newPlant)
        testPlant = newPlant.copy(id = plantId)
    }

    return testPlant
}

// Вспомогательная функция для обновления даты ухода
private suspend fun updateCareEventDate(
    dao: com.example.plantcare.data.database.dao.PlantCareDao,
    plantId: Long,
    eventType: String, // "watering" или "fertilizing"
    newDate: Long
) {
    // Находим событие по типу
    val event = dao.getCareEventsByPlantAndType(plantId, eventType).firstOrNull()

    if (event != null) {
        val updatedEvent = event.copy(datePlanned = newDate)
        dao.updateCareEvent(updatedEvent)
        println("DEBUG: Updated $eventType event for plant $plantId to date $newDate")
    } else {
        // Если события нет — создаём новое
        val newEvent = CareEvent(
            id = System.currentTimeMillis(),
            plantId = plantId,
            type = eventType,
            datePlanned = newDate,
            dateDone = null
        )
        dao.insertCareEvent(newEvent)
        println("DEBUG: Created new $eventType event for plant $plantId with date $newDate")
    }
}