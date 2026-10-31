package com.audioflow.routing

import android.content.Context
import com.audioflow.discovery.AudioDiscovery
import com.audioflow.model.AudioOutput
import com.audioflow.model.AudioSource
import com.audioflow.model.RoutingRule

class RoutingManager(context: Context) {

    private val discovery = AudioDiscovery(context)
    private val router = AudioRouter()

    fun getAvailableOutputs(): List<AudioOutput> {
        return discovery.getAvailableOutputs()
    }

    fun getActiveSources(): List<AudioSource> {
        return discovery.getActiveAudioSources()
    }

    fun hasNotificationAccess(): Boolean {
        return discovery.hasNotificationAccess()
    }

    fun assign(
        source: AudioSource,
        output: AudioOutput
    ): RoutingRule {
        return router.assignOutput(source, output)
    }

    fun removeAssignment(sourceId: String) {
        router.removeAssignment(sourceId)
    }

    fun getAssignments(): List<RoutingRule> {
        return router.getAllAssignments()
    }

    fun clearAssignments() {
        router.clearAssignments()
    }
}
