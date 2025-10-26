package com.example.plantcare.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "care_events")
data class CareEvent(
    @PrimaryKey val id: Long = 0,
    val plantId: Long,
    val type: String,         // "watering", "fertilizing", "repotting"
    val datePlanned: Long,
    val dateDone: Long? = null
)