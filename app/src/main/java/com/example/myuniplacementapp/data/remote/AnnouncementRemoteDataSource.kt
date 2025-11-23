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

        val added = model.addedDate
            .takeIf { it.isNotBlank() }
            ?.let { LocalDate.parse(it) }
            ?: LocalDate.now()

        val modified = model.modifiedDate
            .takeIf { it.isNotBlank() }
            ?.let { LocalDate.parse(it) }
            ?: LocalDate.now()

        return AnnouncementEntity(
            id = snapshot.id,
            title = model.title,
            content = model.content,
            image = model.image,
            addedDate = added,
            modifiedDate = modified
        )
    }

    suspend fun getAllAnnouncements(): List<AnnouncementEntity> {
        val snapshot = announcements.get().await()

        return snapshot.documents.mapNotNull { doc ->
            val model = doc.toObject(AnnouncementRemoteModel::class.java) ?: return@mapNotNull null

            val added = model.addedDate
                .takeIf { it.isNotBlank() }
                ?.let { LocalDate.parse(it) }
                ?: LocalDate.now()

            val modified = model.modifiedDate
                .takeIf { it.isNotBlank() }
                ?.let { LocalDate.parse(it) }
                ?: LocalDate.now()

            AnnouncementEntity(
                id = doc.id,
                title = model.title,
                content = model.content,
                image = model.image,
                addedDate = added,
                modifiedDate = modified
            )
        }
    }
}
