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

class PlacementViewModel(private val repo: PlacementRepository) : ViewModel() {

    val placements: StateFlow<List<PlacementEntity>> =
        repo.getAllPlacements().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun reload() {
        viewModelScope.launch {
            repo.refresh()
        }
    }
}

class PlacementViewModelFactory(private val repo: PlacementRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlacementViewModel(repo) as T
    }
}