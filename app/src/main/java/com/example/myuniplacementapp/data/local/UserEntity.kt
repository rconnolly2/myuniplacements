package com.example.myuniplacementapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val dateOfBirth: LocalDate? = null,
    val profileImageBlob: ByteArray? = null
)