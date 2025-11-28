package com.example.myuniplacementapp.data.local

import androidx.room.TypeConverter
import com.example.myuniplacementapp.data.model.ApplicationStatus

class ApplicationConverters {

    @TypeConverter
    fun fromStatus(status: ApplicationStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): ApplicationStatus {
        return ApplicationStatus.valueOf(value)
    }
}