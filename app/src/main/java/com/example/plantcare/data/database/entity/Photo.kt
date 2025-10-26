package com.example.plantcare.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey val id: Long = 0,
    val plantId: Long,
    val photoUri: String,
    val date: Long
)