// EncyclopediaPlantDetailScreen.kt
package com.example.plantcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Описание
        Text(
            text = "Описание:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = entry.description,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Правила ухода
        Text(
            text = "Правила ухода:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = entry.careRules,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Советы по климату
        Text(
            text = "Советы по климату:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = entry.climateTips,
            fontSize = 16.sp,
            lineHeight = 24.sp,
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