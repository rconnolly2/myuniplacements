package com.example.myuniplacementapp.data.remote

import com.example.myuniplacementapp.data.model.LocationType

data class PlacementRemoteModel(
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val companyLogo: String = "",
    val description: String = "",
    val location: LocationType = LocationType.OTHER,
    val addedDate: String = "",
    val modifiedDate: String = ""
)