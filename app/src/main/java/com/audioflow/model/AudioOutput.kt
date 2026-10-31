package com.audioflow.model

enum class AudioOutputType {
    SPEAKER,
    WIRED,
    BLUETOOTH,
    USB,
    OTHER
}

data class AudioOutput(
    val id: String,
    val name: String,
    val type: AudioOutputType,
    val isConnected: Boolean = true
)
