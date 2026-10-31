package com.audioflow.model

data class RoutingRule(
    val sourceId: String,
    val outputId: String,
    val enabled: Boolean = true
)
