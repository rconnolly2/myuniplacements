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

    val users = repo.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user = _user.asStateFlow()

    fun getUserFlow(email: String): Flow<UserEntity?> = repo.getUserFlow(email)

    fun getUser(email: String) {
        viewModelScope.launch {
            _user.value = repo.getUser(email)
        }
    }

    fun saveUser(user: UserEntity) {
        viewModelScope.launch {
            repo.saveUser(user)
            _user.value = user
        }
    }

    fun addUser(
        first: String,
        last: String,
        email: String,
        phone: String = "",
        dob: String = "",
        image: ByteArray? = null
    ) {
        viewModelScope.launch {
            val parsedDob = if (dob.isNotBlank()) LocalDate.parse(dob) else null
            val user = UserEntity(
                email = email,
                firstName = first,
                lastName = last,
                phoneNumber = phone,
                dateOfBirth = parsedDob,
                profileImageBlob = image
            )
            repo.saveUser(user)
            _user.value = user
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            repo.deleteUser(user)
        }
    }
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