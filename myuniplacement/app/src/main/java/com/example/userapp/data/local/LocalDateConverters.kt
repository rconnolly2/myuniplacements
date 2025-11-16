package com.example.userapp.data.local

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        if (date == null) {
            return null
        }
        return date.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        if (dateString == null || dateString.isBlank()) {
            return null
        }
        return LocalDate.parse(dateString)
    }
}
