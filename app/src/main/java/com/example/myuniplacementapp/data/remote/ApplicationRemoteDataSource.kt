package com.example.myuniplacementapp.data.remote

import com.example.myuniplacementapp.data.local.ApplicationEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ApplicationRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val col = firestore.collection("applications")

    suspend fun saveApplication(app: ApplicationEntity) {
        col.document(app.id).set(app).await()
    }
}