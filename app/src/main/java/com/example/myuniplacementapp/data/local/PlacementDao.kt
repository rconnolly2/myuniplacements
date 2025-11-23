package com.example.myuniplacementapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacementDao {
    @Query("SELECT * FROM placement_table")
    fun getAllPlacements(): Flow<List<PlacementEntity>>

    @Query("SELECT * FROM placement_table WHERE id = :id LIMIT 1")
    suspend fun getPlacementById(id: String): PlacementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacement(placement: PlacementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacements(list: List<PlacementEntity>)
}