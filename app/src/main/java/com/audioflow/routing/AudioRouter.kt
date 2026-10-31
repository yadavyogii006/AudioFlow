package com.audioflow.routing

import com.audioflow.model.AudioOutput
import com.audioflow.model.AudioSource
import com.audioflow.model.RoutingRule

class AudioRouter {

    private val routingRules = mutableMapOf<String, RoutingRule>()

    fun assignOutput(
        source: AudioSource,
        output: AudioOutput
    ): RoutingRule {
        val rule = RoutingRule(
            sourceId = source.id,
            outputId = output.id,
            enabled = true
        )

        routingRules[source.id] = rule
        return rule
    }

    fun removeAssignment(sourceId: String) {
        routingRules.remove(sourceId)
    }

    fun getAssignment(sourceId: String): RoutingRule? {
        return routingRules[sourceId]
    }

    fun getAllAssignments(): List<RoutingRule> {
        return routingRules.values.toList()
    }

    fun clearAssignments() {
        routingRules.clear()
    }
}
