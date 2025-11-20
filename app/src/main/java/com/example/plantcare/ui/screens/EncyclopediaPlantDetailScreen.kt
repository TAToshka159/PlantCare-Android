package com.example.plantcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.plantcare.data.database.entity.EncyclopediaEntry

@Composable
fun EncyclopediaPlantDetailScreen(
    entry: EncyclopediaEntry, // Принимаем полный объект растения
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Название
        Text(
            text = entry.name,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Описание
        Text(
            text = "Описание:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = entry.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Правила ухода
        Text(
            text = "Правила ухода:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = entry.careRules,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Советы по климату
        Text(
            text = "Советы по климату:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = entry.climateTips,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Кнопка "Назад"
        Button(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(200.dp)
        ) {
            Text("Назад")
        }
    }
}