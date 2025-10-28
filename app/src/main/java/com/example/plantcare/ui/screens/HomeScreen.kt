package com.example.plantcare.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantcare.data.getUserName
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.statusBars

@Composable
fun HomeScreen(
    onAddPlantClick: () -> Unit = {},
    onReturnToOnboarding: () -> Unit = {}, // ← ДОБАВЛЕНО
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userName = context.getUserName()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start, // или CenterHorizontally, если хочешь
        verticalArrangement = Arrangement.Top // ← КЛЮЧЕВОЕ ИЗМЕНЕНИЕ
    ) {
        // Приветствие
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

        // Список растений
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Пример статичных растений (пока без Room)
            items(2) { index ->
                PlantCard(
                    name = if (index == 0) "Фикус" else "Кактус",
                    room = if (index == 0) "Гостиная" else "Спальня",
                    mood = if (index == 0) "🙂" else "😢"
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
        // Кнопка добавления
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
    name: String,
    room: String,
    mood: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Комната: $room",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = mood,
                fontSize = 20.sp
            )
        }
    }
}