package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConnectivityStatusBar(
    isOnline: Boolean,
    isForcedOffline: Boolean,
    lastSyncText: String,
    onRetry: () -> Unit,
    onToggleOfflineSimulator: () -> Unit
) {
    val effectiveOnline = isOnline && !isForcedOffline

    val backgroundColor = if (effectiveOnline) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = if (effectiveOnline) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("connectivity_status_bar"),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (effectiveOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                    contentDescription = if (effectiveOnline) "Online" else "Offline",
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (effectiveOnline) {
                        "Connected • Last synced: $lastSyncText"
                    } else {
                        "Offline • Last successful sync: $lastSyncText"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!effectiveOnline) {
                    TextButton(
                        onClick = onRetry,
                        modifier = Modifier.testTag("retry_sync_btn")
                    ) {
                        Text("Retry", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                TextButton(
                    onClick = onToggleOfflineSimulator,
                    modifier = Modifier.testTag("toggle_offline_btn")
                ) {
                    Text(
                        text = if (effectiveOnline) "Simulate Offline" else "Go Online",
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
