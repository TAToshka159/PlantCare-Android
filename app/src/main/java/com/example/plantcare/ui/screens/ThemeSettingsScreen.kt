package com.example.plantcare.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// Список доступных системных шрифтов
private val availableFonts = listOf(
    FontFamilyName("System Default", FontFamily.Default),
    FontFamilyName("Sans Serif", FontFamily.SansSerif),
    FontFamilyName("Serif", FontFamily.Serif),
    FontFamilyName("Monospace", FontFamily.Monospace)
)

// Список доступных цветовых тем
private val availableColorThemes = listOf(
    ColorThemeName("Лес", "Forest"),
    ColorThemeName("Океан", "Ocean"),
    ColorThemeName("Солнце", "Sunset"),
    ColorThemeName("Сирень", "Lavender"),
    ColorThemeName("Розовый рассвет", "Rose")
)

// Модель для хранения имени и FontFamily
data class FontFamilyName(val name: String, val fontFamily: FontFamily)

// Модель для хранения имени и названия темы
data class ColorThemeName(val name: String, val themeKey: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    isDarkTheme: Boolean,
    onDarkThemeToggle: (Boolean) -> Unit,
    currentFontName: String,
    onFontChange: (FontFamily) -> Unit,
    currentColorTheme: String,
    onColorThemeChange: (String) -> Unit,
    currentFontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: (String) -> Unit = {}
) {
    var showFontSelector by remember { mutableStateOf(false) }
    var showColorThemeSelector by remember { mutableStateOf(false) }

    // Оборачиваем в Scaffold, чтобы добавить AppBar с кнопкой "Назад"
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Оформление") },
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // <-- Учитываем padding от TopAppBar
                .padding(16.dp), // <-- Внутренние отступы
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Тема и шрифты",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Переключатель тёмной темы
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Тёмная тема")
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onDarkThemeToggle
                )
            }

            // Кнопка выбора шрифта
            Button(
                onClick = { showFontSelector = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Выбрать шрифт: $currentFontName")
            }

            // Кнопка выбора цветовой темы
            Button(
                onClick = { showColorThemeSelector = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                val themeName = availableColorThemes.find { it.themeKey == currentColorTheme }?.name ?: "Неизвестная тема"
                Text(text = "Цветовая тема: $themeName")
            }

            // Слайдер размера шрифта
            Column {
                Text(
                    text = "Размер шрифта: ${(currentFontSize * 16f).toInt()}sp",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = currentFontSize,
                    onValueChange = onFontSizeChange,
                    valueRange = 0.75f..1.5f, // <-- От 12sp до 24sp
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Пример текста с текущим шрифтом
            Text(
                text = "Пример текста с выбранным шрифтом\nНе забывайте поливать цветы",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize * currentFontSize // <-- Исправлено
                ),
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Диалог выбора шрифта
        if (showFontSelector) {
            // Оборачиваем Dialog в @OptIn, чтобы убрать предупреждение
            @OptIn(ExperimentalMaterial3Api::class)
            Dialog(
                onDismissRequest = { showFontSelector = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Выберите шрифт",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn {
                            items(availableFonts) { font ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onFontChange(font.fontFamily)
                                            showFontSelector = false
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = font.name,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .alpha(if (currentFontName == font.name) 1f else 0f)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { showFontSelector = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text("Закрыть")
                        }
                    }
                }
            }
        }

        // Диалог выбора цветовой темы
        if (showColorThemeSelector) {
            // Оборачиваем Dialog в @OptIn, чтобы убрать предупреждение
            @OptIn(ExperimentalMaterial3Api::class)
            Dialog(
                onDismissRequest = { showColorThemeSelector = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Выберите цветовую тему",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn {
                            items(availableColorThemes) { theme ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onColorThemeChange(theme.themeKey)
                                            showColorThemeSelector = false
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = theme.name,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .alpha(if (currentColorTheme == theme.themeKey) 1f else 0f)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { showColorThemeSelector = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text("Закрыть")
                        }
                    }
                }
            }
        }
    }
}