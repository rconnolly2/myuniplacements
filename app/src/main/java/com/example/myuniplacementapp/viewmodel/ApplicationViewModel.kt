package com.example.myuniplacementapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myuniplacementapp.data.repository.ApplicationRepository
import kotlinx.coroutines.launch

class ApplicationViewModel(
    private val repo: ApplicationRepository
) : ViewModel() {

    fun submitApplication(
        placementId: String,
        userEmail: String,
        coverLetter: String,
        screenshotBytes: ByteArray,
        appliedDate: Long
    ) {
        viewModelScope.launch {
            repo.submitApplication(
                placementId,
                userEmail,
                coverLetter,
                screenshotBytes,
                appliedDate
            )
        }
    }
}

class ApplicationViewModelFactory(
    private val repo: ApplicationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ApplicationViewModel(repo) as T
    }
}