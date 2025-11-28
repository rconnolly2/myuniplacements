package com.example.myuniplacementapp.repository

import com.example.myuniplacementapp.data.local.ApplicationDao
import com.example.myuniplacementapp.data.local.ApplicationEntity
import com.example.myuniplacementapp.data.remote.ApplicationRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class ApplicationRepository(
    private val dao: ApplicationDao,
    private val remote: ApplicationRemoteDataSource,
    private val isOnline: () -> Boolean
) {

    fun getUserApplications(email: String): Flow<List<ApplicationEntity>> =
        dao.getUserApplications(email).onStart {
            if (isOnline()) {
                val list = remote.getUserApplications(email)
                list.forEach { dao.insertApplication(it) }
            }
        }

    suspend fun getApplication(id: String): ApplicationEntity? {
        return if (isOnline()) {
            val item = remote.getApplication(id)
            if (item != null) dao.insertApplication(item)
            item
        } else {
            dao.getApplicationById(id)
        }
    }

    suspend fun insertApplication(app: ApplicationEntity) {
        dao.insertApplication(app)
        if (isOnline()) remote.saveApplication(app)
    }

    suspend fun updateApplication(app: ApplicationEntity) {
        dao.updateApplication(app)
        if (isOnline()) remote.saveApplication(app)
    }

    suspend fun deleteApplication(app: ApplicationEntity) {
        dao.deleteApplication(app)
        if (isOnline()) remote.delete(app.id)
    }
}