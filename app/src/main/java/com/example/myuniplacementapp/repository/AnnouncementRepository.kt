package com.example.myuniplacementapp.repository

import com.example.myuniplacementapp.data.local.AnnouncementDao
import com.example.myuniplacementapp.data.local.AnnouncementEntity
import com.example.myuniplacementapp.data.remote.AnnouncementRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class AnnouncementRepository(
    private val dao: AnnouncementDao,
    private val remote: AnnouncementRemoteDataSource,
    private val isOnline: () -> Boolean
) {

    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>> =
        dao.getAllAnnouncements().onStart {
            if (isOnline()) {
                val list = remote.getAllAnnouncements()
                dao.insertAnnouncements(list)
            }
        }

    suspend fun refresh() {
        if (isOnline()) {
            val placements = remote.getAllAnnouncements()
            dao.deleteAllAnnouncements()
            dao.insertAnnouncements(placements)
        }
    }
}