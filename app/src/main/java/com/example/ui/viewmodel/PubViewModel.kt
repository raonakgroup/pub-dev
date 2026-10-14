package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PubApiService
import com.example.data.UiPackage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PubViewModel : ViewModel() {
    private val apiService = PubApiService()

    private val _packages = MutableStateFlow<List<UiPackage>>(emptyList())
    val packages: StateFlow<List<UiPackage>> = _packages.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedPackage = MutableStateFlow<UiPackage?>(null)
    val selectedPackage: StateFlow<UiPackage?> = _selectedPackage.asStateFlow()

    private val _favoriteNames = MutableStateFlow<Set<String>>(setOf("oxygen", "provider"))
    val favoriteNames: StateFlow<Set<String>> = _favoriteNames.asStateFlow()

    // Connectivity & Sync State
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isForcedOffline = MutableStateFlow(false)
    val isForcedOffline: StateFlow<Boolean> = _isForcedOffline.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long?>(System.currentTimeMillis())
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()

    init {
        loadPackages()
    }

    fun loadPackages(query: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            _searchQuery.value = query
            val list = apiService.fetchPackages(query)
            _packages.value = list
            if (_isOnline.value && !_isForcedOffline.value) {
                _lastSyncTime.value = System.currentTimeMillis()
            }
            _isLoading.value = false
        }
    }

    fun setConnectionStatus(online: Boolean) {
        _isOnline.value = online
        if (online && !_isForcedOffline.value) {
            _lastSyncTime.value = System.currentTimeMillis()
        }
    }

    fun toggleSimulatedOffline() {
        val next = !_isForcedOffline.value
        _isForcedOffline.value = next
        if (!next && _isOnline.value) {
            _lastSyncTime.value = System.currentTimeMillis()
        }
    }

    fun getFormattedLastSync(): String {
        val time = _lastSyncTime.value ?: return "Never"
        val diffSec = (System.currentTimeMillis() - time) / 1000
        return when {
            diffSec < 15 -> "Just now"
            diffSec < 60 -> "$diffSec seconds ago"
            diffSec < 3600 -> "${diffSec / 60} minutes ago"
            else -> SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(time))
        }
    }

    fun selectPackage(pkg: UiPackage?) {
        _selectedPackage.value = pkg
    }

    fun toggleFavorite(packageName: String) {
        val current = _favoriteNames.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _favoriteNames.value = current
    }
}
