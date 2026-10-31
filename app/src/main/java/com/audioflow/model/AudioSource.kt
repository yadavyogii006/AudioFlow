package com.audioflow.model

data class AudioSource(
    val id: String,
    val name: String,
    val packageName: String,
    val isPlaying: Boolean = false,
    val isActive: Boolean = false
)
