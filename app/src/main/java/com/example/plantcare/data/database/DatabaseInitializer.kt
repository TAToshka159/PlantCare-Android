package com.example.plantcare.data.database

import com.example.plantcare.data.PasswordUtils
import com.example.plantcare.data.database.entity.User

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
}