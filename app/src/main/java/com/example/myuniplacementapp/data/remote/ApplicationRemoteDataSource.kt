package com.example.myuniplacementapp.data.remote

import com.example.myuniplacementapp.data.local.ApplicationEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ApplicationRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val applications = firestore.collection("applications")

    suspend fun saveApplication(app: ApplicationEntity) {
        val model = ApplicationRemoteModel(
            id = app.id,
            userEmail = app.userEmail,
            placementId = app.placementId,
            status = app.status
        )
        applications.document(app.id).set(model).await()
    }

    suspend fun getApplication(id: String): ApplicationEntity? {
        val snapshot = applications.document(id).get().await()
        val model = snapshot.toObject(ApplicationRemoteModel::class.java) ?: return null
        return ApplicationEntity(
            id = model.id,
            userEmail = model.userEmail,
            placementId = model.placementId,
            status = model.status
        )
    }

    suspend fun getAllApplications(): List<ApplicationEntity> {
        val snapshot = applications.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val model = doc.toObject(ApplicationRemoteModel::class.java) ?: return@mapNotNull null
            ApplicationEntity(
                id = model.id,
                userEmail = model.userEmail,
                placementId = model.placementId,
                status = model.status
            )
        }
    }

    suspend fun deleteApplication(app: ApplicationEntity) {
        applications.document(app.id).delete().await()
    }
}