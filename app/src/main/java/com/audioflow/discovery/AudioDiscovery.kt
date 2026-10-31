package com.audioflow.discovery

import com.audioflow.service.AudioMonitorService
import android.content.ComponentName
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.session.MediaSessionManager
import android.provider.Settings
import android.text.TextUtils
import com.audioflow.model.AudioOutput
import com.audioflow.model.AudioOutputType
import com.audioflow.model.AudioSource

class AudioDiscovery(private val context: Context) {

    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun getAvailableOutputs(): List<AudioOutput> {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

        return devices.mapNotNull { device ->
            val type = when (device.type) {
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER ->
                    AudioOutputType.PHONE_SPEAKER

                AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                AudioDeviceInfo.TYPE_WIRED_HEADSET ->
                    AudioOutputType.WIRED_HEADPHONES

                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
                AudioDeviceInfo.TYPE_BLE_HEADSET,
                AudioDeviceInfo.TYPE_BLE_SPEAKER ->
                    AudioOutputType.BLUETOOTH

                AudioDeviceInfo.TYPE_USB_DEVICE,
                AudioDeviceInfo.TYPE_USB_HEADSET ->
                    AudioOutputType.USB

                else -> AudioOutputType.OTHER
            }

            AudioOutput(
                id = device.id.toString(),
                name = device.productName.toString(),
                type = type,
                isConnected = true
            )
        }.distinctBy { it.id }
    }

    fun hasNotificationAccess(): Boolean {
        val enabledListeners =
            Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            ) ?: return false

        val component = ComponentName(
            context,
            AudioMonitorService::class.java
        )

        return enabledListeners.split(":").any {
            it == component.flattenToString()
        }
    }

    fun getActiveAudioSources(): List<AudioSource> {
        if (!hasNotificationAccess()) {
            return emptyList()
        }

        return try {
            val manager =
                context.getSystemService(Context.MEDIA_SESSION_SERVICE)
                        as MediaSessionManager

            val component = ComponentName(
                context,
                AudioMonitorService::class.java
            )

            manager.getActiveSessions(component).map { controller ->
                val packageName = controller.packageName

                val appName = try {
                    val appInfo = context.packageManager
                        .getApplicationInfo(packageName, 0)

                    context.packageManager
                        .getApplicationLabel(appInfo)
                        .toString()
                } catch (_: Exception) {
                    packageName
                }

                val isPlaying =
                    controller.playbackState?.state ==
                            android.media.session.PlaybackState.STATE_PLAYING

                AudioSource(
                    id = packageName,
                    name = appName,
                    packageName = packageName,
                    isPlaying = isPlaying,
                    isActive = true
                )
            }.distinctBy { it.packageName }

        } catch (_: SecurityException) {
            emptyList()
        }
    }
}
