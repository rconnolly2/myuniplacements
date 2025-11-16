package com.example.myuniplacementapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "application_table")
data class ApplicationEntity(
    @PrimaryKey val id: String,
    val userEmail: String,
    val placementId: String,
    val status: String
)