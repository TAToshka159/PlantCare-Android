package com.example.plantcare.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.plantcare.data.database.dao.PlantCareDao
import com.example.plantcare.data.database.entity.*

@Database(
    entities = [
        Plant::class,
        CareEvent::class,
        EncyclopediaEntry::class,
        Photo::class,
        PlantStatus::class,
        User::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantCareDao(): PlantCareDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plantcare_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}