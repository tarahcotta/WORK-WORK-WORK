package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.R

@Composable
fun ToolsScreen(
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToPlateCalc: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    onNavigateToPhotos: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAssessment: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Tools & Resources",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Explore exercise library, calculators, science guides, and profile settings.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            ToolCard(
                title = "Exercise Library",
                description = "Browse form guides, videos, and targeted muscle movements.",
                icon = Icons.Default.MenuBook,
                onClick = onNavigateToLibrary
            )
        }

        item {
            ToolCard(
                title = stringResource(id = R.string.title_plate_calc),
                description = stringResource(id = R.string.desc_plate_calc),
                icon = Icons.Default.Calculate,
                onClick = onNavigateToPlateCalc
            )
        }
        item {
            ToolCard(
                title = stringResource(id = R.string.title_bone_guide),
                description = stringResource(id = R.string.desc_bone_guide),
                icon = Icons.Default.HealthAndSafety,
                onClick = onNavigateToGuide
            )
        }
        item {
            ToolCard(
                title = stringResource(id = R.string.title_progress_photos),
                description = stringResource(id = R.string.desc_progress_photos),
                icon = Icons.Default.PhotoLibrary,
                onClick = onNavigateToPhotos
            )
        }

        item {
            ToolCard(
                title = "My Profile & Goals",
                description = "Manage your longevity targets, weight units, and preferences.",
                icon = Icons.Default.AccountCircle,
                onClick = onNavigateToProfile
            )
        }

        item {
            ToolCard(
                title = "Health Assessment",
                description = "Review baseline strength, bone density status, and onboarding assessment.",
                icon = Icons.Default.Assignment,
                onClick = onNavigateToAssessment
            )
        }
    }
}

@Composable
fun ToolCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
