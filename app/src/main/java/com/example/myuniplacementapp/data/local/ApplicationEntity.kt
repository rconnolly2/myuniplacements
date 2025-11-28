package com.example.myuniplacementapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myuniplacementapp.data.model.ApplicationStatus

@Entity(tableName = "application_table")
data class ApplicationEntity(
    @PrimaryKey val id: String,
    val placementId: String,
    val userEmail: String,
    val coverLetter: String,
    val cvLink: String,
    val status: ApplicationStatus = ApplicationStatus.APPLIED
)