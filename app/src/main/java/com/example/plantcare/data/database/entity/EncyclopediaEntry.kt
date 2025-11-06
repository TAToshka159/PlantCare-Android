// EncyclopediaEntry.kt
package com.example.plantcare.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "encyclopedia")
data class EncyclopediaEntry(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val description: String,
    val careRules: String,
    val climateTips: String
)