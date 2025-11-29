package com.example.myuniplacementapp.repository

import com.example.myuniplacementapp.data.local.ApplicationDao
import com.example.myuniplacementapp.data.local.ApplicationEntity
import com.example.myuniplacementapp.data.model.ApplicationStatus
import com.example.myuniplacementapp.data.remote.ApplicationRemoteDataSource
import com.example.myuniplacementapp.data.remote.ApplicationRemoteModel
import com.example.myuniplacementapp.data.remote.FileRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ApplicationRepository(
    private val dao: ApplicationDao,
    private val remote: ApplicationRemoteDataSource,
    private val fileRemote: FileRemoteDataSource,
    private val isOnline: () -> Boolean
) {

    fun getUserApplications(email: String): Flow<List<ApplicationEntity>> =
        dao.getUserApplications(email).onStart {
            if (isOnline()) {
                val list = remote.getApplicationsByUser(email)
                list.forEach { dao.insertApplication(it) }
            }
        }

    fun getApplicationById(id: String): Flow<ApplicationEntity?> =
        dao.getApplicationById(id).onStart {
            if (isOnline()) {
                remote.getApplicationById(id)?.let {
                    dao.insertApplication(
                        ApplicationEntity(
                            id = it.id,
                            placementId = it.placementId,
                            userEmail = it.userEmail,
                            coverLetter = it.coverLetter,
                            screenshotUrl = it.screenshotUrl,
                            appliedDate = it.appliedDate,
                            status = it.status
                        )
                    )
                }
            }
        }

    suspend fun updateStatus(id: String, status: ApplicationStatus) {
        if (isOnline()) remote.updateStatus(id, status)
        dao.updateStatus(id, status)
    }

    suspend fun submitApplication(
        placementId: String,
        userEmail: String,
        coverLetter: String,
        screenshotBytes: ByteArray,
        appliedDate: Long
    ) {
        val id = System.currentTimeMillis().toString()
        val screenshotUrl = if (isOnline()) fileRemote.uploadScreenshot(screenshotBytes) else ""

        val entity = ApplicationEntity(
            id = id,
            placementId = placementId,
            userEmail = userEmail,
            coverLetter = coverLetter,
            screenshotUrl = screenshotUrl,
            appliedDate = appliedDate
        )

        if (isOnline()) {
            remote.saveApplication(
                ApplicationRemoteModel(
                    id = id,
                    placementId = placementId,
                    userEmail = userEmail,
                    coverLetter = coverLetter,
                    screenshotUrl = screenshotUrl,
                    appliedDate = appliedDate
                )
            )
        }

        dao.insertApplication(entity)
    }

    fun submitAsync(
        placementId: String,
        userEmail: String,
        coverLetter: String,
        screenshotBytes: ByteArray,
        appliedDate: Long
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            submitApplication(
                placementId,
                userEmail,
                coverLetter,
                screenshotBytes,
                appliedDate
            )
        }
    }
}