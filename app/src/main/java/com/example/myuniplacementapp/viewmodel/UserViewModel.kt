package com.example.myuniplacementapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myuniplacementapp.data.local.UserEntity
import com.example.myuniplacementapp.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    val users: StateFlow<List<UserEntity>> =
        repository.getAllUsers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addUser(first: String, last: String, email: String, phone: String = "", dob: String = "") {
        if (email.isNotBlank()) {
            viewModelScope.launch {
                val parsedDob = dob.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
                repository.saveUser(UserEntity(firstName = first, lastName = last, email = email, phoneNumber = phone, dateOfBirth = parsedDob))
            }
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    fun getUser(email: String) {
        viewModelScope.launch {
            repository.getUser(email)
        }
    }
}



class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
