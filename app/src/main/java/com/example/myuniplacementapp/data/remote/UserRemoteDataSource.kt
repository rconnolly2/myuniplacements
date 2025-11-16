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
        val userProfile = UserProfile(
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            phoneNumber = user.phoneNumber,
            dateOfBirth = user.dateOfBirth?.toString() ?: ""
        )
        users.document(user.email).set(userProfile).await()
    }

    suspend fun getUserProfile(email: String): UserEntity? {
        val snapshot = users.document(email).get().await()
        val profile = snapshot.toObject(UserProfile::class.java) ?: return null
        // I parse date of birth if it's not blank
        val dateOfBirth = profile.dateOfBirth.let { dob ->
            if (dob.isNotBlank()) LocalDate.parse(dob) else null
        }

        return UserEntity(
            firstName = profile.firstName,
            lastName = profile.lastName,
            email = profile.email,
            phoneNumber = profile.phoneNumber,
            dateOfBirth = dateOfBirth
        )
    }

    suspend fun getAllUsers(): List<UserEntity> {
        val snapshot = users.get().await()

        return snapshot.documents.mapNotNull { doc ->
            // I convert document snapshot to UserProfile
            val profile = doc.toObject(UserProfile::class.java) ?: return@mapNotNull null

            // I parse DOB if it's not blank
            val dateOfBirth = profile.dateOfBirth.let { dob ->
                if (dob.isNotBlank()) LocalDate.parse(dob) else null
            }

            UserEntity(
                firstName = profile.firstName,
                lastName = profile.lastName,
                email = profile.email,
                phoneNumber = profile.phoneNumber,
                dateOfBirth = dateOfBirth
            )
        }
    }

    suspend fun deleteUserProfile(user: UserEntity) {
        users.document(user.email).delete().await()
    }
}
