package com.audioflow.model

enum class AudioOutputType {
    PHONE_SPEAKER,
    WIRED_HEADPHONES,
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
