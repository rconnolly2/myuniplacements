package com.example.myuniplacementapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM application_table")
    fun getAllApplications(): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM application_table WHERE id = :id LIMIT 1")
    suspend fun getApplicationById(id: String): ApplicationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: ApplicationEntity)

    @Delete
    suspend fun deleteApplication(application: ApplicationEntity)
}