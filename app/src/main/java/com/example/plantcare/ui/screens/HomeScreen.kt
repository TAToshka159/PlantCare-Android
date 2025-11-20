// HomeScreen.kt
package com.example.plantcare.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.plantcare.PlantCareApplication
import com.example.plantcare.data.getCurrentUserId
import com.example.plantcare.data.getUserName
import com.example.plantcare.data.database.entity.Plant
import com.example.plantcare.util.PlantMoodUtil
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    onAddPlantClick: () -> Unit = {},
    onPlantClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userName = context.getUserName()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Привет, $userName! 👋",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Ваши растения:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        val userId = context.getCurrentUserId()
        val plantsWithMoods = remember { mutableStateListOf<Pair<Plant, String>>() } // <-- Новое состояние

        LaunchedEffect(userId) {
            val app = context.applicationContext as PlantCareApplication
            val dao = app.database.plantCareDao()

            dao.getPlantsByUser(userId).collectLatest { plantList ->
                val updatedList = plantList.map { plant ->
                    val events = dao.getUpcomingCareEvents(plant.id) // Получаем события ухода
                    val mood = PlantMoodUtil.getMood(events) // <-- Вычисляем смайлик
                    Pair(plant, mood) // <-- Сохраняем пару (растение, смайлик)
                }
                plantsWithMoods.clear()
                plantsWithMoods.addAll(updatedList)
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(plantsWithMoods.size) { index ->
                val (plant, mood) = plantsWithMoods[index] // <-- Извлекаем растение и смайлик
                PlantCard(
                    plant = plant,
                    mood = mood, // <-- Передаём актуальный смайлик
                    onClick = { onPlantClick(plant.id) }
                )
            }
        }

        Button(
            onClick = onAddPlantClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Добавить растение", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun PlantCard(
    plant: Plant,
    mood: String, // <-- Теперь принимаем смайлик
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Фото растения
            if (!plant.photoUri.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(plant.photoUri),
                    contentDescription = "Фото растения",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFlorist,
                        contentDescription = "Нет фото",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Название и комната
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Комната: ${plant.room}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Смайлик настроения
            Text(
                text = mood,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}