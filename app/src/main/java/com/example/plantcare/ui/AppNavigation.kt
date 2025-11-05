// AppNavigation.kt
package com.example.plantcare.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.plantcare.data.isOnboardingCompleted
import com.example.plantcare.data.saveOnboardingCompleted
import com.example.plantcare.ui.screens.AddPlantScreen
import com.example.plantcare.ui.screens.EditPlantScreen
import com.example.plantcare.ui.screens.HomeScreen
import com.example.plantcare.ui.screens.LoginScreen
import com.example.plantcare.ui.screens.PlantDetailScreen
import com.example.plantcare.ui.screens.RegisterScreen
import com.example.plantcare.PlantCareApplication

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var onboardingCompleted by remember { mutableStateOf(context.isOnboardingCompleted()) }

    LaunchedEffect(Unit) {
        onboardingCompleted = context.isOnboardingCompleted()
    }

    var currentDestination by remember { mutableStateOf<AppDestination>(
        if (onboardingCompleted) AppDestination.Home else AppDestination.OnboardingRegister
    ) }

    val destination = currentDestination

    when (destination) {
        AppDestination.OnboardingRegister -> {
            RegisterScreen(
                onNavigateToLogin = { currentDestination = AppDestination.OnboardingLogin },
                onRegisterSuccess = {
                    onboardingCompleted = true
                    currentDestination = AppDestination.Home
                }
            )
        }
        AppDestination.OnboardingLogin -> {
            LoginScreen(
                onNavigateToRegister = { currentDestination = AppDestination.OnboardingRegister },
                onLoginSuccess = {
                    onboardingCompleted = true
                    currentDestination = AppDestination.Home
                }
            )
        }
        AppDestination.Home -> {
            HomeScreen(
                onAddPlantClick = { currentDestination = AppDestination.AddPlant },
                onPlantClick = { plantId ->
                    currentDestination = AppDestination.PlantDetail(plantId)
                },
                onReturnToOnboarding = {
                    context.saveOnboardingCompleted(false)
                    onboardingCompleted = false
                    currentDestination = AppDestination.OnboardingRegister
                },
                modifier = modifier
            )
        }
        AppDestination.AddPlant -> {
            AddPlantScreen(
                onPlantAdded = { currentDestination = AppDestination.Home }
            )
        }
        is AppDestination.PlantDetail -> {
            val plantId = destination.plantId
            PlantDetailScreen(
                plantId = plantId,
                onBack = { currentDestination = AppDestination.Home },
                onEdit = { currentDestination = AppDestination.EditPlant(plantId) }
            )
        }
        is AppDestination.EditPlant -> {
            EditPlantScreen(
                plantId = destination.plantId,
                onPlantUpdated = { currentDestination = AppDestination.Home }
            )
        }
    }
}

private sealed interface AppDestination {
    object OnboardingRegister : AppDestination
    object OnboardingLogin : AppDestination
    object Home : AppDestination
    object AddPlant : AppDestination
    data class PlantDetail(val plantId: Long) : AppDestination
    data class EditPlant(val plantId: Long) : AppDestination
}