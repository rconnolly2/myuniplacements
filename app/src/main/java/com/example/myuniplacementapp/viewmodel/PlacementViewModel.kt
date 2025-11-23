package com.example.myuniplacementapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myuniplacementapp.data.local.PlacementEntity
import com.example.myuniplacementapp.repository.PlacementRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlacementViewModel(private val repository: PlacementRepository) : ViewModel() {

    val placements: StateFlow<List<PlacementEntity>> =
        repository.getAllPlacements().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getPlacement(id: String) {
        viewModelScope.launch {
            repository.getPlacement(id)
        }
    }
}

class PlacementViewModelFactory(private val repository: PlacementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlacementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlacementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}