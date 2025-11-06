// AppNavigation.kt
package com.example.plantcare.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantcare.data.isOnboardingCompleted
import com.example.plantcare.data.saveOnboardingCompleted
import com.example.plantcare.ui.screens.*

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var onboardingCompleted by remember { mutableStateOf(context.isOnboardingCompleted()) }

    LaunchedEffect(Unit) {
        onboardingCompleted = context.isOnboardingCompleted()
    }

    var currentDestination by remember { mutableStateOf<AppDestination>(
        if (onboardingCompleted) AppDestination.BottomNavHome else AppDestination.OnboardingRegister
    ) }

    // --- Основной экран с нижней навигацией ---
    if (currentDestination is AppDestination.BottomNavHome ||
        currentDestination is AppDestination.BottomNavEncyclopedia ||
        currentDestination is AppDestination.BottomNavMore) { // <-- Убрана проверка на Profile

        val currentTab = when (currentDestination) {
            is AppDestination.BottomNavHome -> BottomTab.Home
            is AppDestination.BottomNavEncyclopedia -> BottomTab.Encyclopedia
            is AppDestination.BottomNavMore -> BottomTab.More
            else -> BottomTab.Home // Резервное значение
        }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    BottomTab.entries.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            selected = currentTab == tab,
                            onClick = { currentDestination = tab.destination }
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (currentTab) {
                    BottomTab.Home -> HomeScreen(
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
                    BottomTab.Encyclopedia -> EncyclopediaScreen()
                    BottomTab.More -> MoreScreen()
                }
            }
        }
    }
    // --- Экраны, не связанные с вкладками (пока оставим как было) ---
    else {
        when (currentDestination) {
            is AppDestination.OnboardingRegister -> {
                RegisterScreen(
                    onNavigateToLogin = { currentDestination = AppDestination.OnboardingLogin },
                    onRegisterSuccess = {
                        onboardingCompleted = true
                        currentDestination = AppDestination.BottomNavHome // Переход к вкладке Home после регистрации
                    }
                )
            }
            is AppDestination.OnboardingLogin -> {
                LoginScreen(
                    onNavigateToRegister = { currentDestination = AppDestination.OnboardingRegister },
                    onLoginSuccess = {
                        onboardingCompleted = true
                        currentDestination = AppDestination.BottomNavHome // Переход к вкладке Home после входа
                    }
                )
            }
            is AppDestination.AddPlant -> {
                AddPlantScreen(
                    onPlantAdded = { currentDestination = AppDestination.BottomNavHome } // Возврат к вкладке Home
                )
            }
            is AppDestination.PlantDetail -> {
                val plantId = (currentDestination as AppDestination.PlantDetail).plantId
                PlantDetailScreen(
                    plantId = plantId,
                    onBack = { currentDestination = AppDestination.BottomNavHome }, // Возврат к вкладке Home
                    onEdit = { currentDestination = AppDestination.EditPlant(plantId) },
                    onPhotoClick = { photoUris, index ->
                        currentDestination = AppDestination.FullScreenPhoto(
                            plantId = plantId,
                            photoUris = photoUris,
                            initialPage = index
                        )
                    }
                )
            }
            is AppDestination.EditPlant -> {
                val plantId = (currentDestination as AppDestination.EditPlant).plantId
                EditPlantScreen(
                    plantId = plantId,
                    onPlantUpdated = { currentDestination = AppDestination.BottomNavHome } // Возврат к вкладке Home
                )
            }
            is AppDestination.FullScreenPhoto -> {
                val dest = currentDestination as AppDestination.FullScreenPhoto
                FullScreenPhotoScreen(
                    photoUris = dest.photoUris,
                    initialPage = dest.initialPage,
                    onBack = { currentDestination = AppDestination.PlantDetail(dest.plantId) } // Возврат к деталям растения
                )
            }
            // Обработка других возможных состояний, если необходимо
            else -> {
                // Можно добавить отображение ошибки или заглушку
            }
        }
    }
}

// Обновленный sealed interface AppDestination
private sealed interface AppDestination {
    object OnboardingRegister : AppDestination
    object OnboardingLogin : AppDestination
    object BottomNavHome : AppDestination
    object BottomNavEncyclopedia : AppDestination
    object BottomNavMore : AppDestination
    // object BottomNavProfile : AppDestination // <-- Удалено
    object AddPlant : AppDestination
    data class PlantDetail(val plantId: Long) : AppDestination
    data class EditPlant(val plantId: Long) : AppDestination
    data class FullScreenPhoto(
        val plantId: Long,
        val photoUris: List<String>,
        val initialPage: Int
    ) : AppDestination
}

// Обновленный enum класс BottomTab с привязкой к AppDestination
private enum class BottomTab(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val destination: AppDestination
) {
    Home(Icons.Default.Home, "Растения", AppDestination.BottomNavHome),
    Encyclopedia(Icons.Default.Book, "Энциклопедия", AppDestination.BottomNavEncyclopedia),
    More(Icons.Default.MoreVert, "Еще", AppDestination.BottomNavMore)
    // Profile(Icons.Default.AccountCircle, "Профиль", AppDestination.BottomNavProfile) // <-- Удалено
}