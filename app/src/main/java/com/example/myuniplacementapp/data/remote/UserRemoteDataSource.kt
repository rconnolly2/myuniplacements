package com.example.myuniplacementapp.data.remote

import com.example.myuniplacementapp.data.local.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class UserRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val users = firestore.collection("users")

    suspend fun saveUserProfile(user: UserEntity) {

        val model = UserRemoteModel(
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            phoneNumber = user.phoneNumber,
            dateOfBirth = user.dateOfBirth?.toString() ?: "",
            profileImageUrl = user.profileImageUrl ?: "",
            cvFileUrl = user.cvFileUrl ?: ""
        )

        users.document(user.email).set(model).await()
    }

    suspend fun getUserProfile(email: String): UserEntity? {
        val snap = users.document(email).get().await()
        val m = snap.toObject(UserRemoteModel::class.java) ?: return null

        val dob = if (m.dateOfBirth.isNotBlank()) LocalDate.parse(m.dateOfBirth) else null

        return UserEntity(
            email = m.email,
            firstName = m.firstName,
            lastName = m.lastName,
            phoneNumber = m.phoneNumber,
            dateOfBirth = dob,
            profileImageUrl = m.profileImageUrl,
            cvFileUrl = m.cvFileUrl
        )
    }

    suspend fun getAllUsers(): List<UserEntity> {
        val snap = users.get().await()

        return snap.documents.mapNotNull { doc ->
            val m = doc.toObject(UserRemoteModel::class.java) ?: return@mapNotNull null

            val dob = if (m.dateOfBirth.isNotBlank()) LocalDate.parse(m.dateOfBirth) else null

            UserEntity(
                email = m.email,
                firstName = m.firstName,
                lastName = m.lastName,
                phoneNumber = m.phoneNumber,
                dateOfBirth = dob,
                profileImageUrl = m.profileImageUrl,
                cvFileUrl = m.cvFileUrl
            )
        }
    }

    suspend fun deleteUserProfile(user: UserEntity) {
        users.document(user.email).delete().await()
    }
}