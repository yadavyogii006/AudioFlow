package com.audioflow.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.audioflow.model.AudioOutput

@Composable
fun OutputZone(
    output: AudioOutput,
    assignmentCount: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                Color(0xFF174B3B)
            else
                Color(0xFF202633)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = when (output.type) {
                    com.audioflow.model.AudioOutputType.PHONE_SPEAKER ->
                        "🔊 ${output.name}"

                    com.audioflow.model.AudioOutputType.BLUETOOTH ->
                        "🎧 ${output.name}"

                    com.audioflow.model.AudioOutputType.WIRED_HEADPHONES ->
                        "🎧 ${output.name}"

                    com.audioflow.model.AudioOutputType.USB ->
                        "🔌 ${output.name}"

                    else -> "🔉 ${output.name}"
                },
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$assignmentCount app(s) assigned",
                color = Color(0xFF94A3B8)
            )

            if (selected) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "SELECTED OUTPUT",
                    color = Color(0xFF4ADE80)
                )
            }
        }
    }
}
