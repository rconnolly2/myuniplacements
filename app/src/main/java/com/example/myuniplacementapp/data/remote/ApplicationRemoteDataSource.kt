package com.example.myuniplacementapp.data.remote

import com.example.myuniplacementapp.data.local.ApplicationEntity
import com.example.myuniplacementapp.data.model.ApplicationStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ApplicationRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val col = firestore.collection("applications")

    suspend fun saveApplication(app: ApplicationRemoteModel) {
        col.document(app.id).set(app).await()
    }

    suspend fun updateStatus(id: String, status: ApplicationStatus) {
        col.document(id).update("status", status.name).await()
    }

    suspend fun getApplicationById(id: String): ApplicationEntity? {
        val model = col.document(id).get().await().toObject(ApplicationRemoteModel::class.java)
            ?: return null

        return ApplicationEntity(
            id = model.id,
            placementId = model.placementId,
            userEmail = model.userEmail,
            coverLetter = model.coverLetter,
            screenshotUrl = model.screenshotUrl,
            appliedDate = model.appliedDate,
            status = model.status
        )
    }

    suspend fun getApplicationsByUser(email: String): List<ApplicationEntity> {
        val snap = col.whereEqualTo("userEmail", email).get().await()
        return snap.documents.mapNotNull { d ->
            d.toObject(ApplicationRemoteModel::class.java)?.let {
                ApplicationEntity(
                    id = it.id,
                    placementId = it.placementId,
                    userEmail = it.userEmail,
                    coverLetter = it.coverLetter,
                    screenshotUrl = it.screenshotUrl,
                    appliedDate = it.appliedDate,
                    status = it.status
                )
            }
        }
    }
}