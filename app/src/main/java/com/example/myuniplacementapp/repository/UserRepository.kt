package com.example.myuniplacementapp.repository

import com.example.myuniplacementapp.data.local.UserDao
import com.example.myuniplacementapp.data.local.UserEntity
import com.example.myuniplacementapp.data.remote.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart


class UserRepository(
    private val dao: UserDao,
    private val remote: UserRemoteDataSource,
    private val isOnline: () -> Boolean
) {

    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers().onStart {
        if (isOnline()) {
            val remoteUsers = remote.getAllUsers()
            remoteUsers.forEach { dao.insertUser(it) }
        }
    }

    suspend fun getUser(email: String): UserEntity? {
        return if (isOnline()) {
            val remoteUser = remote.getUserProfile(email)
            if (remoteUser != null) dao.insertUser(remoteUser)
            remoteUser
        } else {
            dao.getUserByEmail(email)
        }
    }

    suspend fun saveUser(user: UserEntity) {
        dao.insertUser(user)
        remote.saveUserProfile(user)
    }

    suspend fun deleteUser(user: UserEntity) {
        dao.deleteUser(user)
        if (isOnline()) remote.deleteUserProfile(user)
    }
}