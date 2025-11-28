package com.example.myuniplacementapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserEntity::class,
        AnnouncementEntity::class,
        PlacementEntity::class,
        ApplicationEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(LocalDateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun placementDao(): PlacementDao
    abstract fun applicationDao(): ApplicationDao
}


