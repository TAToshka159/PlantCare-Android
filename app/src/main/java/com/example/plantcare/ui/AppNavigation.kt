package com.example.plantcare.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.plantcare.data.isOnboardingCompleted
import com.example.plantcare.ui.screens.HomeScreen
import com.example.plantcare.ui.screens.LoginScreen
import com.example.plantcare.ui.screens.RegisterScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var onboardingCompleted by remember { mutableStateOf(context.isOnboardingCompleted()) }

    // Принудительно перечитываем onboardingCompleted при каждом запуске экрана
    LaunchedEffect(Unit) {
        onboardingCompleted = context.isOnboardingCompleted()
    }

    if (onboardingCompleted) {
        HomeScreen(modifier = modifier)
    } else {
        var currentScreen by remember { mutableStateOf<OnboardingDestination>(OnboardingDestination.Register) }

        when (currentScreen) {
            OnboardingDestination.Register -> {
                RegisterScreen(
                    onNavigateToLogin = { currentScreen = OnboardingDestination.Login },
                    onRegisterSuccess = {
                        // После сохранения обновляем состояние
                        onboardingCompleted = true
                    }
                )
            }
            OnboardingDestination.Login -> {
                LoginScreen(
                    onNavigateToRegister = { currentScreen = OnboardingDestination.Register },
                    onLoginSuccess = {
                        onboardingCompleted = true
                    }
                )
            }
        }
    }
}

private sealed interface OnboardingDestination {
    object Register : OnboardingDestination
    object Login : OnboardingDestination
}