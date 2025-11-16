package com.example.myuniplacementapp.data.remote

import com.example.myuniplacementapp.data.local.PlacementEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class PlacementRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val placements = firestore.collection("placements")

    suspend fun savePlacement(placement: PlacementEntity) {
        val model = PlacementRemoteModel(
            id = placement.id,
            title = placement.title,
            company = placement.company,
            description = placement.description,
            addedDate = placement.addedDate.toString(),
            modifiedDate = placement.modifiedDate.toString()
        )
        placements.document(placement.id).set(model).await()
    }

    suspend fun getPlacement(id: String): PlacementEntity? {
        val snapshot = placements.document(id).get().await()
        val model = snapshot.toObject(PlacementRemoteModel::class.java) ?: return null
        val added = if (model.addedDate.isNotBlank()) LocalDate.parse(model.addedDate) else LocalDate.now()
        val modified = if (model.modifiedDate.isNotBlank()) LocalDate.parse(model.modifiedDate) else LocalDate.now()
        return PlacementEntity(
            id = model.id,
            title = model.title,
            company = model.company,
            description = model.description,
            addedDate = added,
            modifiedDate = modified
        )
    }

    suspend fun getAllPlacements(): List<PlacementEntity> {
        val snapshot = placements.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val model = doc.toObject(PlacementRemoteModel::class.java) ?: return@mapNotNull null
            val added = if (model.addedDate.isNotBlank()) LocalDate.parse(model.addedDate) else LocalDate.now()
            val modified = if (model.modifiedDate.isNotBlank()) LocalDate.parse(model.modifiedDate) else LocalDate.now()
            PlacementEntity(
                id = model.id,
                title = model.title,
                company = model.company,
                description = model.description,
                addedDate = added,
                modifiedDate = modified
            )
        }
    }

    suspend fun deletePlacement(placement: PlacementEntity) {
        placements.document(placement.id).delete().await()
    }
}