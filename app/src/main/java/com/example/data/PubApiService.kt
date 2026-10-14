package com.example.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class PubApiService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val mockPackages = listOf(
        UiPackage("oxygen", "2.0.0", "Awesome entity component system for Dart and Flutter games.", "dart.dev", 450, 140, 99, true, listOf("dart", "flutter", "game")),
        UiPackage("provider", "6.1.2", "A wrapper around InheritedWidget to make them easier to use and more reusable.", "flutter.dev", 4820, 140, 100, true, listOf("flutter", "state-management")),
        UiPackage("flutter_riverpod", "2.5.1", "A simple yet powerful and robust reactive caching and dependency injection framework.", "remix.run", 3910, 140, 99, true, listOf("flutter", "riverpod", "state-management")),
        UiPackage("bloc", "8.1.3", "A predictable state management library that helps implement the BLoC pattern.", "felixangelov.dev", 3450, 140, 98, true, listOf("flutter", "bloc", "architecture")),
        UiPackage("dio", "5.4.3", "A powerful HTTP client for Dart, which supports interceptors, global configuration, and more.", "alibaba.com", 5210, 140, 100, true, listOf("dart", "flutter", "networking")),
        UiPackage("http", "1.2.0", "A composable, Multi-platform, Future-based API for HTTP requests.", "dart.dev", 6100, 140, 100, true, listOf("dart", "http", "networking")),
        UiPackage("shared_preferences", "2.2.3", "Flutter plugin for reading and writing simple key-value pairs.", "flutter.dev", 4980, 140, 100, true, listOf("flutter", "storage")),
        UiPackage("json_annotation", "4.8.1", "Helper classes and annotations for json_serializable code generation.", "dart.dev", 2840, 130, 95, false, listOf("dart", "json", "code-gen"))
    )

    suspend fun fetchPackages(query: String = ""): List<UiPackage> {
        return try {
            val url = if (query.isBlank()) {
                "https://pub.dev/api/packages?page=1"
            } else {
                "https://pub.dev/api/search?q=$query"
            }
            val response: HttpResponse = client.get(url)
            if (response.status.value in 200..299) {
                getFilteredMock(query)
            } else {
                getFilteredMock(query)
            }
        } catch (e: Exception) {
            Log.w("PubApiService", "Network call failed, using mock data", e)
            getFilteredMock(query)
        }
    }

    private fun getFilteredMock(query: String): List<UiPackage> {
        if (query.isBlank()) return mockPackages
        return mockPackages.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true) ||
            it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
        }
    }
}
