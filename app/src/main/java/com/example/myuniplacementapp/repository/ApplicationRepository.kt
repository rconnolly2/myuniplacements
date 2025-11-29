package com.example.myuniplacementapp.repository

import com.example.myuniplacementapp.data.local.ApplicationDao
import com.example.myuniplacementapp.data.local.ApplicationEntity
import com.example.myuniplacementapp.data.remote.ApplicationRemoteDataSource
import com.example.myuniplacementapp.data.remote.FileRemoteDataSource
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ApplicationRepository(
    private val dao: ApplicationDao,
    private val remote: ApplicationRemoteDataSource,
    private val fileRemote: FileRemoteDataSource,
    private val isOnline: () -> Boolean
) {
    suspend fun submitApplication(
        placementId: String,
        userEmail: String,
        coverLetter: String,
        screenshotBytes: ByteArray,
        appliedDate: Long
    ) {
        val id = System.currentTimeMillis().toString()

        val screenshotUrl =
            if (isOnline()) fileRemote.uploadScreenshot(screenshotBytes) else ""

        val app = ApplicationEntity(
            id = id,
            placementId = placementId,
            userEmail = userEmail,
            coverLetter = coverLetter,
            screenshotUrl = screenshotUrl,
            appliedDate = appliedDate
        )

        if (isOnline()) remote.saveApplication(app)
        dao.insertApplication(app)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun submitAsync(
        placementId: String,
        userEmail: String,
        coverLetter: String,
        screenshotBytes: ByteArray,
        appliedDate: Long
    ) = GlobalScope.launch {
        submitApplication(
            placementId,
            userEmail,
            coverLetter,
            screenshotBytes,
            appliedDate
        )
    }

    fun getUserApplications(email: String): Flow<List<ApplicationEntity>> {
        return dao.getUserApplications(email)
    }
}