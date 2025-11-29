package com.example.myuniplacementapp.repository

import com.example.myuniplacementapp.data.local.PlacementDao
import com.example.myuniplacementapp.data.local.PlacementEntity
import com.example.myuniplacementapp.data.remote.PlacementRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class PlacementRepository(
    private val dao: PlacementDao,
    private val remote: PlacementRemoteDataSource,
    private val isOnline: () -> Boolean
) {
    fun getAllPlacements(): Flow<List<PlacementEntity>> =
        dao.getAllPlacements().onStart {
            if (isOnline()) {
                val list = remote.getAllPlacements()
                dao.insertPlacements(list)
            }
        }

    suspend fun getPlacement(id: String): PlacementEntity? {
        return if (isOnline()) {
            val item = remote.getPlacement(id)
            if (item != null) dao.insertPlacement(item)
            item
        } else {
            dao.getPlacementById(id)
        }
    }
}