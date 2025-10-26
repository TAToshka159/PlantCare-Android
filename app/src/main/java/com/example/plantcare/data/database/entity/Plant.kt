package com.example.plantcare.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val type: String,
    val photoUri: String?,
    val room: String,
    val createdAt: Long
)