package com.audioflow.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.audioflow.model.AudioSource

@Composable
fun AudioSourceCard(
    source: AudioSource,
    selected: Boolean,
    assignedOutputName: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                androidx.compose.ui.graphics.Color(0xFF263D59)
            else
                androidx.compose.ui.graphics.Color(0xFF202633)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = source.name,
                color = androidx.compose.ui.graphics.Color.White
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = if (source.isPlaying) "● Playing" else "○ Not playing",
                color = if (source.isPlaying)
                    androidx.compose.ui.graphics.Color(0xFF4ADE80)
                else
                    androidx.compose.ui.graphics.Color.LightGray
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = assignedOutputName?.let { "Assigned to: $it" }
                    ?: "Not assigned",
                color = androidx.compose.ui.graphics.Color(0xFF94A3B8)
            )

            if (selected) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "SELECTED",
                    color = androidx.compose.ui.graphics.Color(0xFF60A5FA)
                )
            }
        }
    }
}
