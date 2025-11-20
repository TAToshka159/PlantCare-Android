// PlantCareDao.kt
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
    @Query("SELECT * FROM encyclopedia") // <-- Использует правильное имя таблицы
    suspend fun getAllEncyclopediaEntries(): List<EncyclopediaEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncyclopediaEntry(entry: EncyclopediaEntry)

    // --- НОВЫЕ функции ---
    @Query("DELETE FROM encyclopedia")
    suspend fun deleteAllEncyclopediaEntries()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEncyclopediaEntries(entries: List<EncyclopediaEntry>)
    // --- /НОВЫЕ функции ---

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

    @Query("SELECT * FROM plants WHERE userId = :userId")
    fun getPlantsByUser(userId: Long): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :plantId")
    suspend fun getPlantById(plantId: Long): Plant?

    @Query("SELECT * FROM care_events WHERE plantId = :plantId ORDER BY datePlanned ASC LIMIT 2")
    suspend fun getUpcomingCareEvents(plantId: Long): List<CareEvent>

    @Update
    suspend fun updatePlant(plant: Plant)

    @Transaction
    suspend fun deletePlantAndRelatedData(plantId: Long) {
        deleteCareEventsByPlant(plantId)
        deletePhotosByPlant(plantId)
        deletePlantStatusByPlant(plantId)
        deletePlantById(plantId)
    }

    @Query("DELETE FROM care_events WHERE plantId = :plantId")
    suspend fun deleteCareEventsByPlant(plantId: Long)

    @Query("DELETE FROM photos WHERE plantId = :plantId")
    suspend fun deletePhotosByPlant(plantId: Long)

    @Query("DELETE FROM plant_status WHERE plantId = :plantId")
    suspend fun deletePlantStatusByPlant(plantId: Long)

    @Query("DELETE FROM plants WHERE id = :plantId")
    suspend fun deletePlantById(plantId: Long)

    @Query("DELETE FROM photos WHERE id = :photoId")
    suspend fun deletePhoto(photoId: Long)

    @Query("SELECT * FROM photos WHERE plantId = :plantId ORDER BY date ASC")
    suspend fun getPhotosByPlant(plantId: Long): List<Photo>

    // PlantCareDao.kt
    @Query("UPDATE care_events SET datePlanned = :newDate WHERE id = :eventId")
    suspend fun updateCareEventDate(eventId: Long, newDate: Long)

    @Update
    suspend fun updateCareEvent(event: CareEvent)

    @Query("SELECT * FROM encyclopedia WHERE name = :typeName LIMIT 1") // <-- Правильное имя таблицы
    suspend fun getEncyclopediaEntryByTypeName(typeName: String): EncyclopediaEntry?
}