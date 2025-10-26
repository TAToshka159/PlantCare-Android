package com.example.plantcare.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_status")
data class PlantStatus(
    @PrimaryKey val id: Long = 0,
    val plantId: Long,
    val status: String,       // "happy", "neutral", "sad"
    val updatedAt: Long
)