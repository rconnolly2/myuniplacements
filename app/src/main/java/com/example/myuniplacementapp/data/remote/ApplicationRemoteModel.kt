package com.example.myuniplacementapp.data.remote

import com.example.myuniplacementapp.data.model.ApplicationStatus

data class ApplicationRemoteModel(
    val id: Int = 0,
    val placementId: Int = 0,
    val userEmail: String = "",
    val coverLetter: String = "",
    val cvLink: String = "",
    val status: ApplicationStatus = ApplicationStatus.APPLIED
)