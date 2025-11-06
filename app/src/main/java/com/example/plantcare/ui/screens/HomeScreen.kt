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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.plantcare.PlantCareApplication
import com.example.plantcare.data.getCurrentUserId
import com.example.plantcare.data.getUserName
import com.example.plantcare.data.database.entity.Plant
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    onAddPlantClick: () -> Unit = {},
    onPlantClick: (Long) -> Unit = {},
    onReturnToOnboarding: () -> Unit = {},
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
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Ваши растения:",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        val userId = context.getCurrentUserId()
        val plants = remember { mutableStateListOf<Plant>() }

        LaunchedEffect(userId) {
            val app = context.applicationContext as PlantCareApplication
            app.database.plantCareDao()
                .getPlantsByUser(userId)
                .collectLatest { list ->
                    plants.clear()
                    plants.addAll(list)
                }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(plants.size) { index ->
                val plant = plants[index]
                PlantCard(
                    plant = plant,
                    mood = "🙂", // ← временно фиксированный смайлик
                    onClick = { onPlantClick(plant.id) }
                )
            }
        }

        // 🔴 ТЕСТОВАЯ КНОПКА — ВРЕМЕННО!
        OutlinedButton(
            onClick = onReturnToOnboarding,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Вернуться к входу (тест)", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Button(
            onClick = onAddPlantClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Добавить растение", fontSize = 16.sp)
        }
    }
}

@Composable
private fun PlantCard(
    plant: Plant,
    mood: String,
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Комната: ${plant.room}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Смайлик настроения
            Text(
                text = mood,
                fontSize = 20.sp
            )
        }
    }
}