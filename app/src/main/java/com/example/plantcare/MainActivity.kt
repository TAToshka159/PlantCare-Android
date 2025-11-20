package com.example.plantcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.plantcare.ui.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Отключаем системный ActionBar и делаем статус-бар прозрачным
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Убираем MaterialTheme и Surface — теперь они в AppNavigation
            // MaterialTheme {
            //     Surface(
            //         modifier = Modifier.fillMaxSize(),
            //         color = MaterialTheme.colorScheme.background
            //     ) {
            // Применяем padding под статус-бар и навигацию
            val view = LocalView.current
            if (!view.isInEditMode) {
                view.setPadding(0, 0, 0, 0) // сбрасываем, если был
            }
            AppNavigation(
                modifier = Modifier.padding(
                    top = 20.dp // или используй WindowInsets
                )
            )
            //     }
            // }
        }
    }
}