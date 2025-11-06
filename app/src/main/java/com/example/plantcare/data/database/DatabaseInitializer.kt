// DatabaseInitializer.kt
package com.example.plantcare.data.database

import com.example.plantcare.data.PasswordUtils
import com.example.plantcare.data.database.entity.User
import com.example.plantcare.data.encyclopediaEntries // Импортируем список
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {
    private const val ADMIN_LOGIN = "TAT"
    private const val ADMIN_PASSWORD = "19711971"

    suspend fun ensureDefaultAdmin(db: AppDatabase) {
        val dao = db.plantCareDao()
        val existingAdmin = dao.getUserByLogin(ADMIN_LOGIN)
        if (existingAdmin == null) {
            val admin = User(
                id = 1,
                login = ADMIN_LOGIN,
                passwordHash = PasswordUtils.hashPassword(ADMIN_PASSWORD),
                role = "admin"
            )
            dao.insertUser(admin)
        }
    }

    // Новый метод для инициализации энциклопедии
    suspend fun ensureEncyclopediaEntries(db: AppDatabase) {
        withContext(Dispatchers.IO) {
            // Проверяем, пуста ли таблица encyclopedia
            if (db.plantCareDao().getAllEncyclopediaEntries().isEmpty()) {
                println("DEBUG: Encyclopedia table is empty, inserting initial data...") // Отладка
                // Вставляем все записи из списка
                encyclopediaEntries.forEach { entry ->
                    db.plantCareDao().insertEncyclopediaEntry(entry)
                }
                println("DEBUG: Initial encyclopedia data inserted.") // Отладка
            } else {
                println("DEBUG: Encyclopedia table already has data, skipping insertion.") // Отладка
            }
        }
    }
}