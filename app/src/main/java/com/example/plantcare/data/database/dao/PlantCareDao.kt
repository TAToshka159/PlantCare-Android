package com.example.plantcare.data.database.dao

import androidx.room.*
import com.example.plantcare.data.database.entity.*
import kotlinx.coroutines.flow.Flow
import com.example.plantcare.data.database.entity.User

@Dao
interface PlantCareDao {

    // Plants
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: Plant): Long

    @Query("SELECT * FROM plants")
    fun getAllPlants(): Flow<List<Plant>>

    // Care Events
    @Insert
    suspend fun insertCareEvent(event: CareEvent)

    // Encyclopedia
    @Query("SELECT * FROM encyclopedia")
    suspend fun getAllEncyclopediaEntries(): List<EncyclopediaEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncyclopediaEntry(entry: EncyclopediaEntry)

    // Photos
    @Insert
    suspend fun insertPhoto(photo: Photo)

    // Plant Status
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlantStatus(status: PlantStatus)

    // Users
    @Query("SELECT * FROM users WHERE login = :login")
    suspend fun getUserByLogin(login: String): User?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}