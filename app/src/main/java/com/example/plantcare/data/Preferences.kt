package com.example.plantcare.data

import android.content.Context
import android.content.Context.MODE_PRIVATE

private const val PREF_NAME = "app_prefs"
private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
private const val KEY_USER_NAME = "user_name"
private const val KEY_USER_PASSWORD = "user_password"
private const val KEY_CURRENT_USER_ID = "current_user_id"
private const val KEY_DARK_THEME = "dark_theme_enabled"
private const val KEY_SELECTED_FONT_FAMILY = "selected_font_family"
private const val KEY_FONT_SIZE = "font_size" // <-- Новый ключ
private const val KEY_COLOR_THEME = "color_theme" // <-- Новый ключ

/**
 * Проверяет, прошёл ли пользователь онбординг
 */
fun Context.isOnboardingCompleted(): Boolean {
    return getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .getBoolean(KEY_ONBOARDING_COMPLETED, false)
}

/**
 * Сохраняет статус завершения онбординга
 */
fun Context.saveOnboardingCompleted(completed: Boolean) {
    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .edit()
        .putBoolean(KEY_ONBOARDING_COMPLETED, completed)
        .apply()
}

/**
 * Сохраняет имя пользователя
 */
fun Context.saveUserName(name: String) {
    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .edit()
        .putString(KEY_USER_NAME, name)
        .apply()
}

/**
 * Возвращает имя пользователя. По умолчанию — "Гость"
 */
fun Context.getUserName(): String {
    return getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .getString(KEY_USER_NAME, "Гость") ?: "Гость"
}

fun Context.saveUserPassword(password: String) {
    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .edit()
        .putString(KEY_USER_PASSWORD, password)
        .apply()
}

fun Context.getUserPassword(): String {
    return getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .getString(KEY_USER_PASSWORD, "") ?: ""
}

fun Context.saveCurrentUserId(userId: Long) {
    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .edit()
        .putLong(KEY_CURRENT_USER_ID, userId)
        .apply()
}

// Получение ID текущего пользователя
fun Context.getCurrentUserId(): Long {
    return getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .getLong(KEY_CURRENT_USER_ID, -1) // -1 = гость или не авторизован
}

/**
 * Сохраняет состояние тёмной темы
 */
fun Context.saveDarkThemeEnabled(enabled: Boolean) {
    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .edit()
        .putBoolean(KEY_DARK_THEME, enabled)
        .apply()
}

/**
 * Возвращает, включена ли тёмная тема. По умолчанию — false (светлая)
 */
fun Context.isDarkThemeEnabled(): Boolean {
    return getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .getBoolean(KEY_DARK_THEME, false)
}

/**
 * Сохраняет выбранный шрифт
 */
fun Context.saveSelectedFontFamily(fontFamily: String) {
    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .edit()
        .putString(KEY_SELECTED_FONT_FAMILY, fontFamily)
        .apply()
}

/**
 * Возвращает название выбранного шрифта. По умолчанию — "Default"
 */
fun Context.getSelectedFontFamily(): String {
    return getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .getString(KEY_SELECTED_FONT_FAMILY, "Default") ?: "Default"
}

/**
 * Сохраняет размер шрифта
 */
fun Context.saveFontSize(fontSize: Float) {
    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .edit()
        .putFloat(KEY_FONT_SIZE, fontSize)
        .apply()
}

/**
 * Возвращает размер шрифта. По умолчанию — 16f
 */
fun Context.getFontSize(): Float {
    return getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .getFloat(KEY_FONT_SIZE, 16f)
}

/**
 * Сохраняет выбранную цветовую тему
 */
fun Context.saveColorTheme(theme: String) {
    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .edit()
        .putString(KEY_COLOR_THEME, theme)
        .apply()
}

/**
 * Возвращает название выбранной цветовой темы. По умолчанию — "Forest"
 */
fun Context.getColorTheme(): String {
    return getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        .getString(KEY_COLOR_THEME, "Forest") ?: "Forest"
}