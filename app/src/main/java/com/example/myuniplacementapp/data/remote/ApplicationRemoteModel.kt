package com.example.myuniplacementapp.data.remote

import com.example.myuniplacementapp.data.model.ApplicationStatus

data class ApplicationRemoteModel(
    val id: String = "",
    val placementId: String = "",
    val userEmail: String = "",
    val coverLetter: String = "",
    val screenshotUrl: String = "",
    val appliedDate: Long = 0L,
    val status: ApplicationStatus = ApplicationStatus.APPLIED
)