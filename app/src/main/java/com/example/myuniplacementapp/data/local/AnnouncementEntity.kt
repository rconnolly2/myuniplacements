package com.example.myuniplacementapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "announcement_table")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val image: String,
    val addedDate: LocalDate,
    val modifiedDate: LocalDate
)