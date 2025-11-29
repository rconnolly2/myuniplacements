package com.example.myuniplacementapp.data.model

enum class LocationType {
    DUBLIN,
    LIMERICK,
    BELFAST,
    ERASMUS,
    OTHER
}

fun LocationType.pretty(): String {
    return name.lowercase()
        .replace("_", " ")
        .replaceFirstChar { it.uppercase() }
}