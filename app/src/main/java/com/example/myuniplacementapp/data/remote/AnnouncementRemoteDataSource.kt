package com.example.myuniplacementapp.data.remote

import com.example.myuniplacementapp.data.local.AnnouncementEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class AnnouncementRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val announcements = firestore.collection("announcements")

    suspend fun saveAnnouncement(a: AnnouncementEntity) {
        val model = AnnouncementRemoteModel(
            id = a.id,
            title = a.title,
            content = a.content,
            addedDate = a.addedDate.toString(),
            modifiedDate = a.modifiedDate.toString()
        )
        announcements.document(a.id).set(model).await()
    }

    suspend fun getAnnouncement(id: String): AnnouncementEntity? {
        val snapshot = announcements.document(id).get().await()
        val model = snapshot.toObject(AnnouncementRemoteModel::class.java) ?: return null
        val added = if (model.addedDate.isNotBlank()) LocalDate.parse(model.addedDate) else LocalDate.now()
        val modified = if (model.modifiedDate.isNotBlank()) LocalDate.parse(model.modifiedDate) else LocalDate.now()
        return AnnouncementEntity(
            id = model.id,
            title = model.title,
            content = model.content,
            addedDate = added,
            modifiedDate = modified
        )
    }

    suspend fun getAllAnnouncements(): List<AnnouncementEntity> {
        val snapshot = announcements.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val model = doc.toObject(AnnouncementRemoteModel::class.java) ?: return@mapNotNull null
            val added = if (model.addedDate.isNotBlank()) LocalDate.parse(model.addedDate) else LocalDate.now()
            val modified = if (model.modifiedDate.isNotBlank()) LocalDate.parse(model.modifiedDate) else LocalDate.now()
            AnnouncementEntity(
                id = model.id,
                title = model.title,
                content = model.content,
                addedDate = added,
                modifiedDate = modified
            )
        }
    }

    suspend fun deleteAnnouncement(a: AnnouncementEntity) {
        announcements.document(a.id).delete().await()
    }
}