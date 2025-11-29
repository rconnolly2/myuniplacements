package com.example.myuniplacementapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myuniplacementapp.data.model.ApplicationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: ApplicationEntity)

    @Query("SELECT * FROM application_table WHERE userEmail = :email")
    fun getUserApplications(email: String): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM application_table WHERE id = :id")
    fun getApplicationById(id: String): Flow<ApplicationEntity?>

    @Query("UPDATE application_table SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: ApplicationStatus)
}
