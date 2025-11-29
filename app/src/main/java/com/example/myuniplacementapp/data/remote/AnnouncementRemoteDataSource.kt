package com.example.myuniplacementapp.data.remote

import com.example.myuniplacementapp.data.local.AnnouncementEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class AnnouncementRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val announcements = firestore.collection("announcements")

    suspend fun getAnnouncement(id: String): AnnouncementEntity? {
        val snapshot = announcements.document(id).get().await()
        val model = snapshot.toObject(AnnouncementRemoteModel::class.java) ?: return null

        return AnnouncementEntity(
            id = model.id,
            title = model.title,
            content = model.content,
            image = model.image,
            addedDate = LocalDate.parse(model.addedDate),
            modifiedDate = LocalDate.parse(model.modifiedDate)
        )
    }

    suspend fun getAllAnnouncements(): List<AnnouncementEntity> {
        val snapshot = announcements.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val model = doc.toObject(AnnouncementRemoteModel::class.java) ?: return@mapNotNull null
            AnnouncementEntity(
                id = model.id,
                title = model.title,
                content = model.content,
                image = model.image,
                addedDate = LocalDate.parse(model.addedDate),
                modifiedDate = LocalDate.parse(model.modifiedDate)
            )
        }
    }

    suspend fun saveAnnouncement(model: AnnouncementRemoteModel) {
        announcements.document(model.id).set(model).await()
    }
}
