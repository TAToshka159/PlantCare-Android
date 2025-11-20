// MainActivity.kt
package com.example.plantcare

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.plantcare.ui.AppNavigation
import com.example.plantcare.util.NotificationHelper

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Разрешение получено, можно отправлять уведомления
            println("DEBUG: Notifications permission granted.")
        } else {
            // Разрешение не получено
            println("DEBUG: Notifications permission denied.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Отключаем системный ActionBar и делаем статус-бар прозрачным
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Запрашиваем разрешение на уведомления
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Создаём канал уведомлений
        NotificationHelper.createNotificationChannel(this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Применяем padding под статус-бар и навигацию
                    AppNavigation(
                        modifier = Modifier.padding(
                            top = 20.dp // или используй WindowInsets
                        )
                    )
                }
            }
        }
    }
}