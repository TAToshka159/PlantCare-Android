package com.example.plantcare.data

import android.content.Context
import android.content.Context.MODE_PRIVATE

private const val PREF_NAME = "app_prefs"
private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
private const val KEY_USER_NAME = "user_name"
private const val KEY_USER_PASSWORD = "user_password"
private const val KEY_CURRENT_USER_ID = "current_user_id"

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