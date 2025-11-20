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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.plantcare.data.*
import com.example.plantcare.ui.screens.*
import com.example.plantcare.ui.theme.PlantCareTheme

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var onboardingCompleted by remember { mutableStateOf(context.isOnboardingCompleted()) }

    // --- Состояния пользователя: читаем из SharedPreferences при запуске ---
    var isGuestUser by remember { mutableStateOf(context.getIsGuest()) }
    var isAdminUser by remember { mutableStateOf(context.getUserRole()) }
    var userName by remember { mutableStateOf(context.getUserName()) }
    // ---

    // --- Новые состояния для темы, шрифта, цвета и размера ---
    var isDarkTheme by remember { mutableStateOf(context.isDarkThemeEnabled()) }
    var selectedFontFamilyName by remember { mutableStateOf(context.getSelectedFontFamily()) }
    var colorThemeName by remember { mutableStateOf(context.getColorTheme()) }
    var fontSizeMultiplier by remember { mutableStateOf(context.getFontSize() / 16f) } // <-- Нормализуем к 16sp
    // ---

    LaunchedEffect(Unit) {
        onboardingCompleted = context.isOnboardingCompleted()
        isDarkTheme = context.isDarkThemeEnabled()
        selectedFontFamilyName = context.getSelectedFontFamily()
        colorThemeName = context.getColorTheme()
        fontSizeMultiplier = context.getFontSize() / 16f // <-- Нормализуем к 16sp

        // --- Восстанавливаем состояние пользователя ---
        isGuestUser = context.getIsGuest()
        isAdminUser = context.getUserRole()
        userName = context.getUserName()
        // ---
    }

    var currentDestination by remember { mutableStateOf<AppDestination>(
        if (onboardingCompleted) AppDestination.BottomNavHome else AppDestination.OnboardingRegister
    ) }

    // --- Оборачиваем всё в PlantCareTheme с динамическими параметрами ---
    PlantCareTheme(
        darkTheme = isDarkTheme,
        colorThemeName = colorThemeName,
        fontSizeMultiplier = fontSizeMultiplier,
        selectedFontFamilyName = selectedFontFamilyName
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
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
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ) {
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
                                }
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
                                onAboutClick = { currentDestination = AppDestination.AboutApp },
                                onThemeSettingsClick = { currentDestination = AppDestination.ThemeSettings },
                                onSupportClick = { /* Можно оставить пустым, если не нужно */ }, // <-- Добавлен
                                onDevToolsClick = { currentDestination = AppDestination.DevTools }, // <-- Новый параметр
                                onLogoutClick = {
                                    context.saveOnboardingCompleted(false)
                                    onboardingCompleted = false
                                    currentDestination = AppDestination.OnboardingRegister
                                },
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
                                // --- Сохраняем в SharedPreferences ---
                                context.saveIsGuest(isGuest)
                                context.saveUserRole(isAdmin)
                                context.saveUserName(userName)
                                // ---
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
                                // --- Сохраняем в SharedPreferences ---
                                context.saveIsGuest(false)
                                context.saveUserRole(isAdmin)
                                context.saveUserName(login)
                                // ---
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
                            onPlantUpdated = { currentDestination = AppDestination.PlantDetail(plantId) },
                            onBack = { currentDestination = AppDestination.PlantDetail(plantId) }
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
                    is AppDestination.ThemeSettings -> { // <-- Новый экран
                        ThemeSettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onDarkThemeToggle = { enabled ->
                                isDarkTheme = enabled
                                context.saveDarkThemeEnabled(enabled)
                            },
                            currentFontName = selectedFontFamilyName,
                            onFontChange = { fontFamily ->
                                val fontFamilyName = when (fontFamily) {
                                    FontFamily.Default -> "Default"
                                    FontFamily.SansSerif -> "SansSerif"
                                    FontFamily.Serif -> "Serif"
                                    FontFamily.Monospace -> "Monospace"
                                    else -> "Default"
                                }
                                selectedFontFamilyName = fontFamilyName
                                context.saveSelectedFontFamily(fontFamilyName)
                            },
                            currentColorTheme = colorThemeName,
                            onColorThemeChange = { theme ->
                                colorThemeName = theme
                                context.saveColorTheme(theme)
                            },
                            currentFontSize = fontSizeMultiplier,
                            onFontSizeChange = { size ->
                                fontSizeMultiplier = size
                                val normalizedSize = size * 16f // <-- Возвращаем к нормальному размеру
                                context.saveFontSize(normalizedSize)
                            },
                            onBackClick = { currentDestination = AppDestination.BottomNavMore },
                            onShowSnackbar = { message -> /* TODO */ }
                        )
                    }
                    is AppDestination.AboutApp -> { // <-- Новый экран: О приложении
                        AboutAppScreen(
                            onBackClick = { currentDestination = AppDestination.BottomNavMore }
                        )
                    }
                    is AppDestination.DevTools -> { // <-- Новый экран: Для разработчиков
                        DevToolsScreen(
                            onBackClick = { currentDestination = AppDestination.BottomNavMore }
                        )
                    }
                    else -> {
                        // Можно добавить отображение ошибки или заглушку
                    }
                }
            }
        }
    }
}

// Обновлённый sealed interface AppDestination
sealed interface AppDestination {
    object OnboardingRegister : AppDestination
    object OnboardingLogin : AppDestination
    object BottomNavHome : AppDestination
    object BottomNavEncyclopedia : AppDestination
    object BottomNavMore : AppDestination
    object AddPlant : AppDestination
    object Profile : AppDestination
    object ThemeSettings : AppDestination
    object AboutApp : AppDestination // <-- Добавлено!
    object DevTools : AppDestination // <-- Новый элемент
    data class PlantDetail(val plantId: Long) : AppDestination
    data class EditPlant(val plantId: Long) : AppDestination
    data class FullScreenPhoto(
        val plantId: Long,
        val photoUris: List<String>,
        val initialPage: Int
    ) : AppDestination
    data class EncyclopediaPlantDetail(val entry: com.example.plantcare.data.database.entity.EncyclopediaEntry) : AppDestination
}

// Обновлённый enum класс BottomTab с привязкой к AppDestination
enum class BottomTab(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val destination: AppDestination
) {
    Home(Icons.Default.Home, "Растения", AppDestination.BottomNavHome),
    Encyclopedia(Icons.Default.Book, "Энциклопедия", AppDestination.BottomNavEncyclopedia),
    More(Icons.Default.MoreVert, "Еще", AppDestination.BottomNavMore)
}