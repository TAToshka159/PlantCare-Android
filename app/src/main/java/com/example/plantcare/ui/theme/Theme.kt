package com.example.plantcare.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.plantcare.data.getColorTheme
import com.example.plantcare.data.getFontSize
import com.example.plantcare.data.getSelectedFontFamily

// --- Цвета акцентов (кнопки, иконки) — зависят от выбранной темы ---
private fun getPrimaryColorForTheme(colorThemeName: String, darkTheme: Boolean): Color {
    return when (colorThemeName) {
        "Forest" -> if (darkTheme) Color(0xFF66BB6A) else Color(0xFF2E7D32) // Светло-зелёный / Темно-зелёный
        "Ocean" -> if (darkTheme) Color(0xFF4FC3F7) else Color(0xFF0288D1) // Светло-синий / Темно-синий
        "Sunset" -> if (darkTheme) Color(0xFFFFB74D) else Color(0xFFFF9800) // Светло-оранжевый / Оранжевый
        "Lavender" -> if (darkTheme) Color(0xFFBA68C8) else Color(0xFF7B1FA2) // Светло-фиолетовый / Фиолетовый
        "Rose" -> if (darkTheme) Color(0xFFF48FB1) else Color(0xFFD81B60) // Светло-розовый / Розовый
        else -> if (darkTheme) Color(0xFF66BB6A) else Color(0xFF2E7D32) // по умолчанию
    }
}

private fun getSecondaryColorForTheme(colorThemeName: String, darkTheme: Boolean): Color {
    return when (colorThemeName) {
        "Forest" -> if (darkTheme) Color(0xFF388E3C) else Color(0xFF81C784) // Темно-зелёный / Светло-зелёный
        "Ocean" -> if (darkTheme) Color(0xFF0288D1) else Color(0xFF4FC3F7) // Темно-синий / Светло-синий
        "Sunset" -> if (darkTheme) Color(0xFFFF9800) else Color(0xFFFFCC80) // Оранжевый / Светло-оранжевый
        "Lavender" -> if (darkTheme) Color(0xFF7B1FA2) else Color(0xFFBA68C8) // Фиолетовый / Светло-фиолетовый
        "Rose" -> if (darkTheme) Color(0xFFD81B60) else Color(0xFFF48FB1) // Розовый / Светло-розовый
        else -> if (darkTheme) Color(0xFF388E3C) else Color(0xFF81C784) // по умолчанию
    }
}

// --- Фон и поверхности — для светлой темы — мягкие, природные оттенки ---
private fun getLightBackground(colorThemeName: String): Color {
    return when (colorThemeName) {
        "Forest" -> Color(0xFFF5F9F2) // Очень светло-зелёный, как лист
        "Ocean" -> Color(0xFFE6F2F9) // Очень светло-голубой, как небо
        "Sunset" -> Color(0xFFF9F0E6) // Теплый крем, как закат
        "Lavender" -> Color(0xFFF3E8F7) // Светло-фиолетовый, как сирень
        "Rose" -> Color(0xFFF9E8F0) // Очень светло-розовый, как рассвет
        else -> Color(0xFFF5F9F2) // по умолчанию — зелёный
    }
}

private fun getLightSurface(colorThemeName: String): Color {
    return when (colorThemeName) {
        "Forest" -> Color(0xFFE8F2E5) // Легкий зелёный оттенок
        "Ocean" -> Color(0xFFDCE9F2) // Легкий синий оттенок
        "Sunset" -> Color(0xFFEEE1D4) // Кремовый
        "Lavender" -> Color(0xFFE9D8F0) // Легкий фиолетовый
        "Rose" -> Color(0xFFEED8E4) // Легкий розовый
        else -> Color(0xFFE8F2E5) // по умолчанию
    }
}

// --- Светлая и тёмная схемы — с разными фонами, но одинаковым акцентом ---
@Composable
fun getCustomColorScheme(darkTheme: Boolean, colorThemeName: String): ColorScheme {
    val primary = getPrimaryColorForTheme(colorThemeName, darkTheme)
    val secondary = getSecondaryColorForTheme(colorThemeName, darkTheme)

    return if (darkTheme) {
        // ТЁМНАЯ — чисто тёмная, только кнопки цветные
        darkColorScheme(
            primary = primary,
            onPrimary = Color.Black, // Текст на кнопке чёрный
            secondary = secondary,
            onSecondary = Color.White,
            tertiary = primary,
            onTertiary = Color.Black,
            background = Color(0xFF121212), // Чёрный фон
            onBackground = Color.White,
            surface = Color(0xFF1E1E1E), // Тёмная поверхность
            onSurface = Color.White,
        )
    } else {
        // СВЕТЛАЯ — мягкие, природные фоны, цветные кнопки
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White, // Белый текст на цветной кнопке
            secondary = secondary,
            onSecondary = Color.Black,
            tertiary = primary,
            onTertiary = Color.White,
            background = getLightBackground(colorThemeName), // Мягкий природный фон
            onBackground = Color(0xFF1A1A1A), // Почти чёрный текст — для контраста
            surface = getLightSurface(colorThemeName), // Мягкая поверхность
            onSurface = Color(0xFF1A1A1A), // Почти чёрный текст на поверхности
        )
    }
}
// ---

@Composable
fun PlantCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    colorThemeName: String = "Forest",
    fontSizeMultiplier: Float = 1f,
    selectedFontFamilyName: String = "Default",
    content: @Composable () -> Unit
) {
    val colorScheme = getCustomColorScheme(darkTheme, colorThemeName)

    val baseTypography = Typography
    val typography = remember(colorThemeName, fontSizeMultiplier, selectedFontFamilyName) {
        val fontFamily = when (selectedFontFamilyName) {
            "Default" -> FontFamily.Default
            "SansSerif" -> FontFamily.SansSerif
            "Serif" -> FontFamily.Serif
            "Monospace" -> FontFamily.Monospace
            else -> FontFamily.Default
        }
        Typography(
            displayLarge = baseTypography.displayLarge.copy(
                fontSize = baseTypography.displayLarge.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            displayMedium = baseTypography.displayMedium.copy(
                fontSize = baseTypography.displayMedium.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            displaySmall = baseTypography.displaySmall.copy(
                fontSize = baseTypography.displaySmall.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            headlineLarge = baseTypography.headlineLarge.copy(
                fontSize = baseTypography.headlineLarge.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            headlineMedium = baseTypography.headlineMedium.copy(
                fontSize = baseTypography.headlineMedium.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            headlineSmall = baseTypography.headlineSmall.copy(
                fontSize = baseTypography.headlineSmall.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            titleLarge = baseTypography.titleLarge.copy(
                fontSize = baseTypography.titleLarge.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            titleMedium = baseTypography.titleMedium.copy(
                fontSize = baseTypography.titleMedium.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            titleSmall = baseTypography.titleSmall.copy(
                fontSize = baseTypography.titleSmall.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            bodyLarge = baseTypography.bodyLarge.copy(
                fontSize = baseTypography.bodyLarge.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            bodyMedium = baseTypography.bodyMedium.copy(
                fontSize = baseTypography.bodyMedium.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            bodySmall = baseTypography.bodySmall.copy(
                fontSize = baseTypography.bodySmall.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            labelLarge = baseTypography.labelLarge.copy(
                fontSize = baseTypography.labelLarge.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            labelMedium = baseTypography.labelMedium.copy(
                fontSize = baseTypography.labelMedium.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
            labelSmall = baseTypography.labelSmall.copy(
                fontSize = baseTypography.labelSmall.fontSize * fontSizeMultiplier,
                fontFamily = fontFamily
            ),
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}