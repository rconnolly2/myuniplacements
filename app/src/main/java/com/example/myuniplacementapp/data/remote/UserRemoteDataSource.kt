package com.example.myuniplacementapp.data.remote

import android.util.Base64
import com.example.myuniplacementapp.data.local.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class UserRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val users = firestore.collection("users")

    suspend fun saveUserProfile(user: UserEntity) {
        val blob = user.profileImageBlob?.let {
            Base64.encodeToString(it, Base64.DEFAULT)
        } ?: ""

        val profile = UserProfile(
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            phoneNumber = user.phoneNumber,
            dateOfBirth = user.dateOfBirth?.toString() ?: "",
            profileImageBlob = blob
        )

        users.document(user.email).set(profile).await()
    }

    suspend fun getUserProfile(email: String): UserEntity? {
        val snap = users.document(email).get().await()
        val p = snap.toObject(UserProfile::class.java) ?: return null

        val dob = if (p.dateOfBirth.isNotBlank()) LocalDate.parse(p.dateOfBirth) else null
        val bytes = if (p.profileImageBlob.isNotBlank())
            Base64.decode(p.profileImageBlob, Base64.DEFAULT)
        else null

        return UserEntity(
            email = p.email,
            firstName = p.firstName,
            lastName = p.lastName,
            phoneNumber = p.phoneNumber,
            dateOfBirth = dob,
            profileImageBlob = bytes
        )
    }

    suspend fun getAllUsers(): List<UserEntity> {
        val snap = users.get().await()

        return snap.documents.mapNotNull { doc ->
            val p = doc.toObject(UserProfile::class.java) ?: return@mapNotNull null

            val dob = if (p.dateOfBirth.isNotBlank()) LocalDate.parse(p.dateOfBirth) else null
            val bytes =
                if (p.profileImageBlob.isNotBlank()) Base64.decode(p.profileImageBlob, Base64.DEFAULT)
                else null

            UserEntity(
                email = p.email,
                firstName = p.firstName,
                lastName = p.lastName,
                phoneNumber = p.phoneNumber,
                dateOfBirth = dob,
                profileImageBlob = bytes
            )
        }
    }

    suspend fun deleteUserProfile(user: UserEntity) {
        users.document(user.email).delete().await()
    }
}