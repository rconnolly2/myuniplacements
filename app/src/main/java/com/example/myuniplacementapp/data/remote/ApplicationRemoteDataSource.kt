package com.example.myuniplacementapp.data.remote

import com.example.myuniplacementapp.data.local.ApplicationEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ApplicationRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val applications = firestore.collection("applications")

    suspend fun saveApplication(app: ApplicationEntity) {
        val remote = ApplicationRemoteModel(
            id = app.id.toInt(),
            placementId = app.placementId.toInt(),
            userEmail = app.userEmail,
            coverLetter = app.coverLetter,
            cvLink = app.cvLink,
            status = app.status
        )
        applications.document(app.id).set(remote).await()
    }

    suspend fun getApplication(id: String): ApplicationEntity? {
        val snapshot = applications.document(id).get().await()
        val remote = snapshot.toObject(ApplicationRemoteModel::class.java) ?: return null

        return ApplicationEntity(
            id = remote.id.toString(),
            placementId = remote.placementId.toString(),
            userEmail = remote.userEmail,
            coverLetter = remote.coverLetter,
            cvLink = remote.cvLink,
            status = remote.status
        )
    }

    suspend fun getUserApplications(email: String): List<ApplicationEntity> {
        val snapshot = applications.whereEqualTo("userEmail", email).get().await()

        return snapshot.documents.mapNotNull { doc ->
            val remote = doc.toObject(ApplicationRemoteModel::class.java) ?: return@mapNotNull null

            ApplicationEntity(
                id = remote.id.toString(),
                placementId = remote.placementId.toString(),
                userEmail = remote.userEmail,
                coverLetter = remote.coverLetter,
                cvLink = remote.cvLink,
                status = remote.status
            )
        }
    }

    suspend fun delete(id: String) {
        applications.document(id).delete().await()
    }
}