package com.example.myuniplacementapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "placement_table")
data class PlacementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val company: String,
    val companyLogo: String,
    val description: String,
    val addedDate: LocalDate,
    val modifiedDate: LocalDate
)