package com.example.data

import kotlinx.serialization.Serializable

data class UiPackage(
    val name: String,
    val version: String,
    val description: String,
    val publisher: String = "dart.dev",
    val likes: Int = 120,
    val pubPoints: Int = 130,
    val popularity: Int = 98,
    val isFlutterFavorite: Boolean = false,
    val tags: List<String> = listOf("dart", "flutter")
)
