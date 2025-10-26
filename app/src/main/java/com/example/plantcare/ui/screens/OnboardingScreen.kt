package com.example.plantcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantcare.data.saveOnboardingCompleted
import com.example.plantcare.data.saveUserName
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.statusBars

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var userName by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start, // или CenterHorizontally, если хочешь
        verticalArrangement = Arrangement.Center // ← КЛЮЧЕВОЕ ИЗМЕНЕНИЕ
    ) {
        Text(
            text = "Добро пожаловать в PlantCare+!",
            fontSize = 24.sp,
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Приложение для ухода за комнатными растениями",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Введите ваше имя") },
            isError = nameError != null,
            supportingText = { if (nameError != null) Text(nameError!!) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                val trimmed = userName.trim()
                if (trimmed.isEmpty()) {
                    // Если не ввёл — гость
                    context.saveUserName("Гость")
                    context.saveOnboardingCompleted(true)
                    onComplete()
                } else if (trimmed.any { it.isLetter() }) {
                    // Всё ок — сохраняем имя
                    context.saveUserName(trimmed)
                    context.saveOnboardingCompleted(true)
                    onComplete()
                } else {
                    // Только цифры/символы — ошибка
                    nameError = "Имя должно содержать буквы"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Начать", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}