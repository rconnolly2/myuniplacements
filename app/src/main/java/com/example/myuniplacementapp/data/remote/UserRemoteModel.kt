package com.example.myuniplacementapp.data.remote

data class UserRemoteModel(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val profileImageUrl: String = "",
    val cvFileUrl: String = ""
)