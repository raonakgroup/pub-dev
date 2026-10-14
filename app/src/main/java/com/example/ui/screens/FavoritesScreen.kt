package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.UiPackage
import com.example.ui.viewmodel.PubViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: PubViewModel,
    onPackageClick: (UiPackage) -> Unit
) {
    val allPackages by viewModel.packages.collectAsState()
    val favorites by viewModel.favoriteNames.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val isForcedOffline by viewModel.isForcedOffline.collectAsState()
    val lastSyncText = viewModel.getFormattedLastSync()

    val favoritePackages = allPackages.filter { favorites.contains(it.name) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Liked Packages", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                ConnectivityStatusBar(
                    isOnline = isOnline,
                    isForcedOffline = isForcedOffline,
                    lastSyncText = lastSyncText,
                    onRetry = { viewModel.loadPackages() },
                    onToggleOfflineSimulator = { viewModel.toggleSimulatedOffline() }
                )
            }
        }
    ) { paddingValues ->
        if (favoritePackages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No liked packages yet. Tap the heart icon to save packages!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoritePackages) { pkg ->
                    PackageCard(
                        pkg = pkg,
                        isFavorite = true,
                        onCardClick = { onPackageClick(pkg) },
                        onFavoriteClick = { viewModel.toggleFavorite(pkg.name) }
                    )
                }
            }
        }
    }
}
