// PlantDetailScreen.kt
package com.example.plantcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plantcare.data.database.entity.CareEvent
import com.example.plantcare.PlantCareApplication
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PlantDetailScreen(
    plantId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as PlantCareApplication
    val dao = app.database.plantCareDao()

    var plant by remember { mutableStateOf<com.example.plantcare.data.database.entity.Plant?>(null) }
    var careEvents by remember { mutableStateOf<List<CareEvent>>(emptyList()) }

    LaunchedEffect(plantId) {
        plant = dao.getPlantById(plantId)
        careEvents = dao.getUpcomingCareEvents(plantId)
    }

    if (plant == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding() // ← ОТСТУП ПОД СТАТУС-БАР И НАВИГАЦИЮ
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Растение не найдено")
            Button(onClick = onBack) { Text("Назад") }
        }
        return
    }

    val p = plant!!
    val wateringEvent = careEvents.find { it.type == "watering" }
    val fertilizingEvent = careEvents.find { it.type == "fertilizing" }

    // Скролл, если контент не помещается
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding() // ← ГЛАВНОЕ: отступ под системные элементы
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Детали растения", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))
        Text("Название: ${p.name}")
        Text("Тип: ${p.type}", modifier = Modifier.padding(top = 4.dp))
        Text("Комната: ${p.room}", modifier = Modifier.padding(top = 4.dp))
        Text("Добавлено: ${formatDate(p.createdAt)}", modifier = Modifier.padding(top = 4.dp))

        Text("Ближайший уход:", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))

        if (wateringEvent != null) {
            val days = daysUntil(wateringEvent.datePlanned)
            val dateStr = formatDate(wateringEvent.datePlanned)
            val wateringText = when {
                days > 0 -> "Полив через $days дней ($dateStr)"
                days == 0 -> "Полив сегодня! ($dateStr)"
                else -> "Полив просрочен на ${-days} дней ($dateStr)"
            }
            Text(wateringText, color = if (days < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        }

        if (fertilizingEvent != null) {
            val days = daysUntil(fertilizingEvent.datePlanned)
            val dateStr = formatDate(fertilizingEvent.datePlanned)
            val fertilizingText = when {
                days > 0 -> "Удобрение через $days дней ($dateStr)"
                days == 0 -> "Удобрение сегодня! ($dateStr)"
                else -> "Удобрение просрочено на ${-days} дней ($dateStr)"
            }
            Text(fertilizingText, color = if (days < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBack) {
                Text("Назад")
            }
            OutlinedButton(onClick = onEdit) {
                Text("Изменить")
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(timestamp))
}

private fun daysUntil(futureTimestamp: Long): Int {
    val now = System.currentTimeMillis()
    val diffMillis = futureTimestamp - now
    return (diffMillis / (1000 * 60 * 60 * 24)).toInt()
}