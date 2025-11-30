package com.example.myuniplacementapp.data.remote

import com.example.myuniplacementapp.data.local.PlacementEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class PlacementRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val col = firestore.collection("placements")

    suspend fun getAllPlacements(): List<PlacementEntity> {
        val snap = col.get().await()
        return snap.documents.mapNotNull {
            val model = it.toObject(PlacementRemoteModel::class.java) ?: return@mapNotNull null
            val added = model.addedDate.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) } ?: LocalDate.now()
            val modified = model.modifiedDate.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) } ?: LocalDate.now()

            PlacementEntity(
                id = model.id,
                title = model.title,
                company = model.company,
                companyLogo = model.companyLogo,
                description = model.description,
                placementUrl = model.placementUrl,
                location = model.location,
                addedDate = added,
                modifiedDate = modified
            )
        }
    }
}