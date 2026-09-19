package com.example.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun FormGuideDialog(exerciseName: String, cues: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "$exerciseName: Form Tips") },
        text = { Text(text = cues) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Got it") }
        }
    )
}
