package com.example.myuniplacementapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myuniplacementapp.data.local.UserEntity
import com.example.myuniplacementapp.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class UserViewModel(private val repo: UserRepository) : ViewModel() {

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user = _user.asStateFlow()

    fun addUser(user: UserEntity) {
        viewModelScope.launch {
            repo.addUser(user)
            _user.value = user
        }
    }

    fun saveUser(
        user: UserEntity,
        imageBytes: ByteArray?,
        cvBytes: ByteArray?
    ) {
        viewModelScope.launch {
            val updated = repo.saveUser(user, imageBytes, cvBytes)
            _user.value = updated
        }
    }

    fun getUser(email: String) {
        viewModelScope.launch {
            _user.value = repo.getUser(email)
        }
    }

    fun getUserFlow(email: String): Flow<UserEntity?> =
        repo.getUserFlow(email)

}

class UserViewModelFactory(private val repo: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}