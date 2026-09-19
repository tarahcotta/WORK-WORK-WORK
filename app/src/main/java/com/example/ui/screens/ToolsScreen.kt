package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToolsScreen(
    onNavigateToPlateCalc: () -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToPhotos: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Utility Tools",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        item {
            ToolCard(
                title = "Plate Calculator",
                description = "Quickly calculate barbell plate requirements.",
                icon = Icons.Default.Calculate,
                onClick = onNavigateToPlateCalc
            )
        }

        item {
            ToolCard(
                title = "Bone Science Guide",
                description = "Learn the science behind your longevity.",
                icon = Icons.Default.HealthAndSafety,
                onClick = onNavigateToGuide
            )
        }
        
        item {
            ToolCard(
                title = "Progress Photos",
                description = "Track your physical transformation.",
                icon = Icons.Default.PhotoLibrary,
                onClick = onNavigateToPhotos
            )
        }
    }
}

@Composable
fun ToolCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
