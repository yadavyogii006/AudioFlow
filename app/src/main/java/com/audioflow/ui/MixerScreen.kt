package com.audioflow.ui

import android.content.Intent
import android.provider.Settings
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

@Composable
fun MixerScreen() {
    val context = LocalContext.current

    val manager = remember(context) {
        RoutingManager(context)
    }

    val sources = remember { mutableStateListOf<AudioSource>() }
    val outputs = remember { mutableStateListOf<AudioOutput>() }
    val rules = remember { mutableStateListOf<RoutingRule>() }

    var selectedSourceId by remember { mutableStateOf<String?>(null) }
    var selectedOutputId by remember { mutableStateOf<String?>(null) }
    var notificationAccess by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("Ready to configure audio") }

    fun refresh() {
        notificationAccess = manager.hasNotificationAccess()

        sources.clear()
        sources.addAll(manager.getActiveSources())

        outputs.clear()
        outputs.addAll(manager.getAvailableOutputs())

        rules.clear()
        rules.addAll(manager.getAssignments())
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    val background = Color(0xFF111722)

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF60A5FA),
            background = background,
            surface = Color(0xFF1B2330)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = background
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                item {
                    Text(
                        text = "AUDIOFLOW",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )

                    Text(
                        text = "Personal Audio Routing Console",
                        color = Color(0xFF94A3B8)
                    )
                }

                item {
                    Button(
                        onClick = { refresh() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("↻ Refresh devices and apps")
                    }
                }

                if (!notificationAccess) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF49351B)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Notification access required",
                                    color = Color.White
                                )

                                Text(
                                    "Allow AudioFlow to access media sessions " +
                                        "to discover supported active media apps.",
                                    color = Color(0xFFE5D5B8)
                                )

                                Button(
                                    onClick = {
                                        context.startActivity(
                                            Intent(
                                                Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                                            )
                                        )
                                    }
                                ) {
                                    Text("Grant access")
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        "ACTIVE AUDIO SOURCES",
                        color = Color(0xFF60A5FA),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "Select an app to choose its destination.",
                        color = Color(0xFF94A3B8)
                    )
                }

                if (sources.isEmpty()) {
                    item {
                        Text(
                            if (notificationAccess)
                                "No active media sessions found. Start playback " +
                                    "in a supported app and refresh."
                            else
                                "Grant notification access to discover media sessions.",
                            color = Color.LightGray
                        )
                    }
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
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "AVAILABLE OUTPUTS",
                        color = Color(0xFF4ADE80),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "Choose where the selected app should be assigned.",
                        color = Color(0xFF94A3B8)
                    )
                }

                if (outputs.isEmpty()) {
                    item {
                        Text(
                            "No output devices detected.",
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
                                selectedOutputId = output.id
                                message = "Select an app first."
                            } else {
                                manager.assign(source, output)
                                rules.clear()
                                rules.addAll(manager.getAssignments())

                                selectedOutputId = output.id
                                message =
                                    "${source.name} assigned to ${output.name}"
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

                            Text(
                                message,
                                color = Color(0xFF4ADE80)
                            )

                            Text(
                                "${rules.size} routing assignment(s)",
                                color = Color.LightGray
                            )

                            Text(
                                "Note: These are logical assignments only. " +
                                    "Android has not redirected the apps' audio.",
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
        }
    }
}
