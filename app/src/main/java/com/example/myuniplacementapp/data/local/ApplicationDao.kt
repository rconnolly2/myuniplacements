package com.example.myuniplacementapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: ApplicationEntity)

    @Query("SELECT * FROM application_table WHERE userEmail = :email")
    fun getUserApplications(email: String): Flow<List<ApplicationEntity>>
}