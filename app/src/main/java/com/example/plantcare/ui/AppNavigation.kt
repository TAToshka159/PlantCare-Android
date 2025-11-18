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
import com.example.plantcare.data.isOnboardingCompleted
import com.example.plantcare.data.saveOnboardingCompleted
import com.example.plantcare.ui.screens.*

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var onboardingCompleted by remember { mutableStateOf(context.isOnboardingCompleted()) }

    // Состояния пользователя
    var isGuestUser by remember { mutableStateOf(false) } // по умолчанию не гость
    var isAdminUser by remember { mutableStateOf(false) } // по умолчанию не админ
    var userName by remember { mutableStateOf("") }       // по умолчанию пустое имя

    LaunchedEffect(Unit) {
        onboardingCompleted = context.isOnboardingCompleted()
    }

    var currentDestination by remember { mutableStateOf<AppDestination>(
        if (onboardingCompleted) AppDestination.BottomNavHome else AppDestination.OnboardingRegister
    ) }

    // --- Основной экран с нижней навигацией ---
    if (currentDestination is AppDestination.BottomNavHome ||
        currentDestination is AppDestination.BottomNavEncyclopedia ||
        currentDestination is AppDestination.BottomNavMore) {

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
                    BottomTab.Encyclopedia -> EncyclopediaScreen(
                        onPlantClick = { entry ->
                            currentDestination = AppDestination.EncyclopediaPlantDetail(entry)
                        }
                    )
                    BottomTab.More -> MoreScreen(
                        isGuestUser = isGuestUser,
                        isAdminUser = isAdminUser,
                        userName = userName,
                        onProfileClick = { /* TODO: реализуй переход к профилю или оставь пустым */ },
                        onSettingsClick = { /* TODO: добавь переход к настройкам */ },
                        onAboutClick = { /* TODO: добавь переход к "О приложении" */ },
                        onShowSnackbar = { message -> /* TODO: реализуй отображение сообщения */ }
                    )
                }
            }
        }
    }
    // --- Экраны, не связанные с вкладками ---
    else {
        when (currentDestination) {
            is AppDestination.OnboardingRegister -> {
                RegisterScreen(
                    onNavigateToLogin = { currentDestination = AppDestination.OnboardingLogin },
                    onRegisterSuccess = { login, isGuest, isAdmin ->
                        onboardingCompleted = true
                        isGuestUser = isGuest          // <-- Обновляем состояние гостя
                        isAdminUser = isAdmin          // <-- Обновляем состояние админа
                        userName = if (isGuest) "Гость" else login // <-- Обновляем имя
                        currentDestination = AppDestination.BottomNavHome
                    }
                )
            }
            is AppDestination.OnboardingLogin -> {
                LoginScreen(
                    onNavigateToRegister = { currentDestination = AppDestination.OnboardingRegister },
                    onLoginSuccess = { login, isAdmin ->
                        onboardingCompleted = true
                        isGuestUser = false          // <-- Обновляем состояние (не гость)
                        isAdminUser = isAdmin        // <-- Обновляем состояние админа
                        userName = login             // <-- Обновляем имя
                        currentDestination = AppDestination.BottomNavHome
                    }
                )
            }
            is AppDestination.AddPlant -> {
                AddPlantScreen(
                    onPlantAdded = { currentDestination = AppDestination.BottomNavHome }
                )
            }
            is AppDestination.PlantDetail -> {
                val plantId = (currentDestination as AppDestination.PlantDetail).plantId
                PlantDetailScreen(
                    plantId = plantId,
                    onBack = { currentDestination = AppDestination.BottomNavHome },
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
                    onPlantUpdated = { currentDestination = AppDestination.BottomNavHome }
                )
            }
            is AppDestination.FullScreenPhoto -> {
                val dest = currentDestination as AppDestination.FullScreenPhoto
                FullScreenPhotoScreen(
                    photoUris = dest.photoUris,
                    initialPage = dest.initialPage,
                    onBack = { currentDestination = AppDestination.PlantDetail(dest.plantId) }
                )
            }
            is AppDestination.EncyclopediaPlantDetail -> {
                val entry = (currentDestination as AppDestination.EncyclopediaPlantDetail).entry
                EncyclopediaPlantDetailScreen(
                    entry = entry,
                    onBack = { currentDestination = AppDestination.BottomNavEncyclopedia }
                )
            }
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
    object AddPlant : AppDestination
    object Profile : AppDestination
    data class PlantDetail(val plantId: Long) : AppDestination
    data class EditPlant(val plantId: Long) : AppDestination
    data class FullScreenPhoto(
        val plantId: Long,
        val photoUris: List<String>,
        val initialPage: Int
    ) : AppDestination
    data class EncyclopediaPlantDetail(val entry: com.example.plantcare.data.database.entity.EncyclopediaEntry) : AppDestination
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
}