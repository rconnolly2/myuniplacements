package com.example.myuniplacementapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myuniplacementapp.data.local.ApplicationEntity
import com.example.myuniplacementapp.repository.ApplicationRepository
import kotlinx.coroutines.flow.Flow

class ApplicationViewModel(private val repo: ApplicationRepository) : ViewModel() {

    fun submitApplication(
        placementId: String,
        userEmail: String,
        coverLetter: String,
        screenshotBytes: ByteArray,
        appliedDate: Long
    ) {
        repo.submitAsync(placementId, userEmail, coverLetter, screenshotBytes, appliedDate)
    }

    fun getUserApplications(email: String): Flow<List<ApplicationEntity>> {
        return repo.getUserApplications(email)
    }
}

class ApplicationViewModelFactory(private val repo: ApplicationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ApplicationViewModel(repo) as T
    }
}