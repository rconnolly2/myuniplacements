package com.example.myuniplacementapp.repository

import com.example.myuniplacementapp.data.local.UserDao
import com.example.myuniplacementapp.data.local.UserEntity
import com.example.myuniplacementapp.data.remote.FileRemoteDataSource
import com.example.myuniplacementapp.data.remote.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class UserRepository(
    private val dao: UserDao,
    private val remote: UserRemoteDataSource,
    private val fileRemote: FileRemoteDataSource,
    private val isOnline: () -> Boolean
) {

    suspend fun addUser(user: UserEntity): UserEntity {
        dao.insertUser(user)
        if (isOnline()) {
            remote.saveUserProfile(user)
        }
        return user
    }

    suspend fun saveUser(
        user: UserEntity,
        imageBytes: ByteArray?,
        cvBytes: ByteArray?
    ): UserEntity {

        var imageUrl = user.profileImageUrl
        var cvUrl = user.cvFileUrl

        if (isOnline()) {
            if (imageBytes != null) {
                imageUrl = fileRemote.uploadProfileImage(imageBytes)
            }
            if (cvBytes != null) {
                cvUrl = fileRemote.uploadCv(cvBytes)
            }
        }

        val updated = user.copy(
            profileImageUrl = imageUrl,
            cvFileUrl = cvUrl
        )

        dao.insertUser(updated)
        if (isOnline()) remote.saveUserProfile(updated)

        return updated
    }

    suspend fun getUser(email: String): UserEntity? {
        return if (isOnline()) {
            val u = remote.getUserProfile(email)
            if (u != null) dao.insertUser(u)
            u
        } else dao.getUserByEmail(email)
    }

    fun getUserFlow(email: String): Flow<UserEntity?> = flow {
        emit(dao.getUserByEmail(email))
        if (isOnline()) {
            val u = remote.getUserProfile(email)
            if (u != null) {
                dao.insertUser(u)
                emit(u)
            }
        }
    }

    fun getAllUsers(): Flow<List<UserEntity>> =
        dao.getAllUsers().onStart {
            if (isOnline()) {
                val list = remote.getAllUsers()
                list.forEach { dao.insertUser(it) }
            }
        }

    suspend fun deleteUser(user: UserEntity) {
        dao.deleteUser(user)
        if (isOnline()) remote.deleteUserProfile(user)
    }
}