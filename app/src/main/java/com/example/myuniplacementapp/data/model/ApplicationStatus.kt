package com.example.myuniplacementapp.data.model

enum class ApplicationStatus {
    APPLIED,
    REJECTED,
    NO_RESPONSE,
    PRE_SCREEN,
    INTERVIEW,
    OFFER
}

fun ApplicationStatus.pretty(): String {
    return name.lowercase()
        .replace("_", " ")
        .replaceFirstChar { it.uppercase() }
}