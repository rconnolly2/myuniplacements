package com.example.myuniplacementapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myuniplacementapp.data.local.ApplicationEntity
import com.example.myuniplacementapp.repository.ApplicationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ApplicationViewModel(private val repository: ApplicationRepository) : ViewModel() {

    fun getUserApplications(email: String): StateFlow<List<ApplicationEntity>> =
        repository.getUserApplications(email)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getApplication(id: String) {
        viewModelScope.launch {
            repository.getApplication(id)
        }
    }

    fun applyToPlacement(app: ApplicationEntity) {
        viewModelScope.launch {
            repository.insertApplication(app)
        }
    }

    fun updateApplication(app: ApplicationEntity) {
        viewModelScope.launch {
            repository.updateApplication(app)
        }
    }

    fun deleteApplication(app: ApplicationEntity) {
        viewModelScope.launch {
            repository.deleteApplication(app)
        }
    }
}

class ApplicationViewModelFactory(private val repository: ApplicationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApplicationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ApplicationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}