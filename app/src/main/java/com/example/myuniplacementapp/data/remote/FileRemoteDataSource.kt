package com.example.myuniplacementapp.data.remote

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FileRemoteDataSource(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    suspend fun uploadScreenshot(bytes: ByteArray): String {
        if (bytes.isEmpty()) {
            throw IllegalArgumentException("Screenshot is empty!")
        }

        val ref = storage.reference.child("screenshots/${System.currentTimeMillis()}.jpg")
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }
}