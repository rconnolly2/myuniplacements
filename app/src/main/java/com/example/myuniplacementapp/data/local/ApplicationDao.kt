package com.example.myuniplacementapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {

    @Query("SELECT * FROM application_table WHERE userEmail = :email")
    fun getUserApplications(email: String): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM application_table WHERE id = :id LIMIT 1")
    suspend fun getApplicationById(id: String): ApplicationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: ApplicationEntity)

    @Update
    suspend fun updateApplication(application: ApplicationEntity)

    @Delete
    suspend fun deleteApplication(application: ApplicationEntity)
}