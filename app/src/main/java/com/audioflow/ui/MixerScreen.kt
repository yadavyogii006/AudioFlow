package com.audioflow.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.audioflow.model.AudioOutput
import com.audioflow.model.AudioSource
import com.audioflow.model.RoutingRule
import com.audioflow.routing.RoutingManager

private val defaultAudioSources = listOf(
    AudioSource("preset-youtube", "YouTube", "com.google.android.youtube"),
    AudioSource("preset-instagram", "Instagram", "com.instagram.android"),
    AudioSource("preset-spotify", "Spotify", "com.spotify.music"),
    AudioSource("preset-jiohotstar", "JioHotstar", "in.startv.hotstar")
)

@Composable
fun MixerScreen() {
    val context = LocalContext.current
    val manager = remember(context) { RoutingManager(context) }

    val sources = remember {
        mutableStateListOf<AudioSource>().apply {
            addAll(defaultAudioSources)
        }
    }
    val outputs = remember { mutableStateListOf<AudioOutput>() }
    val rules = remember { mutableStateListOf<RoutingRule>() }

    var selectedSourceId by remember { mutableStateOf<String?>(null) }
    var selectedOutputId by remember { mutableStateOf<String?>(null) }
    var showAddSourceDialog by remember { mutableStateOf(false) }
    var newSourceName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("Choose an app and an output zone") }

    fun refresh() {
        outputs.clear()
        outputs.addAll(manager.getAvailableOutputs())
        rules.clear()
        rules.addAll(manager.getAssignments())
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF60A5FA),
            background = Color(0xFF111722),
            surface = Color(0xFF1B2330)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF111722)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 24.dp)
                ) {
                    item {
                        Text(
                            "AUDIOFLOW",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                        Text(
                            "Personal Audio Routing Console",
                            color = Color(0xFF94A3B8)
                        )
                    }

                    item {
                        Button(
                            onClick = { refresh() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("↻ Refresh output devices")
                        }
                    }

                    item {
                        Text(
                            "YOUR AUDIO APPS",
                            color = Color(0xFF60A5FA),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Select an app, then choose its destination.",
                            color = Color(0xFF94A3B8)
                        )
                    }

                    items(sources, key = { it.id }) { source ->
                        val assignedOutput = rules
                            .firstOrNull { it.sourceId == source.id }
                            ?.outputId
                            ?.let { id -> outputs.firstOrNull { it.id == id }?.name }

                        AudioSourceCard(
                            source = source,
                            selected = selectedSourceId == source.id,
                            assignedOutputName = assignedOutput,
                            onClick = {
                                selectedSourceId = source.id
                                selectedOutputId = null
                                message = "${source.name} selected. Choose an output."
                            }
                        )
                    }

                    item {
                        OutlinedButton(
                            onClick = { showAddSourceDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("+ Add another source")
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "OUTPUT ZONES",
                            color = Color(0xFF4ADE80),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Phone speaker and connected audio devices.",
                            color = Color(0xFF94A3B8)
                        )
                    }

                    if (outputs.isEmpty()) {
                        item {
                            Text(
                                "No output zones detected.",
                                color = Color.LightGray
                            )
                        }
                    }

                    items(outputs, key = { it.id }) { output ->
                        OutputZone(
                            output = output,
                            assignmentCount = rules.count {
                                it.outputId == output.id
                            },
                            selected = selectedOutputId == output.id,
                            onClick = {
                                val source = sources.firstOrNull {
                                    it.id == selectedSourceId
                                }

                                if (source == null) {
                                    message = "Select an app first."
                                } else {
                                    manager.assign(source, output)
                                    rules.clear()
                                    rules.addAll(manager.getAssignments())
                                    selectedOutputId = output.id
                                    message =
                                        "${source.name} logically assigned to ${output.name}"
                                }
                            }
                        )
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF202633)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "ROUTING STATUS",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(message, color = Color(0xFF4ADE80))
                                Text(
                                    "${rules.size} logical assignment(s)",
                                    color = Color.LightGray
                                )
                                Text(
                                    "Important: Android does not let this app independently redirect other apps' audio. Left and right earbuds are logical display zones, not separate physical audio routes.",
                                    color = Color(0xFFFBBF24)
                                )
                                OutlinedButton(
                                    onClick = {
                                        manager.clearAssignments()
                                        rules.clear()
                                        selectedSourceId = null
                                        selectedOutputId = null
                                        message = "All assignments cleared."
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Clear all assignments")
                                }
                            }
                        }
                    }
                }

                if (showAddSourceDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showAddSourceDialog = false
                            newSourceName = ""
                        },
                        title = { Text("Add audio source") },
                        text = {
                            OutlinedTextField(
                                value = newSourceName,
                                onValueChange = { newSourceName = it },
                                label = { Text("App or source name") },
                                singleLine = true
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val name = newSourceName.trim()
                                    if (name.isNotEmpty()) {
                                        sources.add(
                                            AudioSource(
                                                id = "manual-${System.currentTimeMillis()}",
                                                name = name,
                                                packageName = "manual"
                                            )
                                        )
                                        message = "$name added."
                                    }
                                    newSourceName = ""
                                    showAddSourceDialog = false
                                }
                            ) {
                                Text("Add")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showAddSourceDialog = false
                                    newSourceName = ""
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}
