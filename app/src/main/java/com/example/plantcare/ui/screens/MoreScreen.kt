// MoreScreen.kt
package com.example.plantcare.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.plantcare.util.EmailHelper

@Composable
fun MoreScreen(
    isGuestUser: Boolean = false,
    isAdminUser: Boolean = false,
    userName: String = "",
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}, // Оставлен для совместимости, но не используется
    onAboutClick: () -> Unit = {},
    onThemeSettingsClick: () -> Unit = {}, // <-- Новый параметр
    onSupportClick: () -> Unit = {}, // <-- Новый параметр
    onDevToolsClick: () -> Unit = {}, // <-- Новый параметр
    onLogoutClick: () -> Unit = {}, // <-- Новый параметр для выхода
    onShowSnackbar: (String) -> Unit = {}
) {
    val context = LocalContext.current

    val profileClickAction = if (isGuestUser) {
        { onShowSnackbar("Зарегистрируйтесь, чтобы получить возможность настройки профиля") }
    } else {
        onProfileClick
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Мини-профиль вверху
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { profileClickAction() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Аватар пользователя (заглушка)
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Аватар пользователя",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(end = 16.dp)
                )

                // Информация о пользователе
                Column {
                    if (isGuestUser) {
                        Text(
                            text = "Гость",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Гостевой режим",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = userName.ifEmpty { "Пользователь" },
                            style = MaterialTheme.typography.headlineSmall
                        )
                        if (isAdminUser) {
                            Text(
                                text = "Администратор",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary // выделяем цветом
                            )
                        } else {
                            Text(
                                text = "Пользователь",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "Еще",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Список опций
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Настройки - теперь ведёт на ThemeSettings
            ListItem(
                headlineContent = { Text("Тема и шрифты") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Тема и шрифты"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onThemeSettingsClick() } // <-- Изменено: вызывает новый колбэк
            )

            // О приложении
            ListItem(
                headlineContent = { Text("О приложении") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "О приложении"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAboutClick() }
            )

            // Поддержка
            ListItem(
                headlineContent = { Text("Поддержка") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Email, // <-- Новая иконка
                        contentDescription = "Поддержка"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Вызываем функцию отправки email
                        EmailHelper.sendSupportEmail(context)
                        // onSupportClick() // <-- Можно вызвать, если нужно
                    }
            )

            // --- КНОПКА "ДЛЯ РАЗРАБОТЧИКОВ" ---
            if (isAdminUser) {
                ListItem(
                    headlineContent = { Text("Для разработчиков") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Build, // <-- Иконка "инструменты"
                            contentDescription = "Для разработчиков"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDevToolsClick() } // <-- Новый колбэк
                )
            }
            // --- /КНОПКА "ДЛЯ РАЗРАБОТЧИКОВ" ---
        }

        // --- КНОПКА "ВЫХОД" ---
        Spacer(modifier = Modifier.height(8.dp)) // <-- Небольшой отступ перед "Выходом"

        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Выход")
        }
        // --- /КНОПКА "ВЫХОД" ---
    }
}