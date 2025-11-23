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

    suspend fun getAnnouncement(id: String): AnnouncementEntity? {
        return if (isOnline()) {
            val item = remote.getAnnouncement(id)
            if (item != null) dao.insertAnnouncement(item)
            item
        } else {
            dao.getAnnouncementById(id)
        }
    }
}