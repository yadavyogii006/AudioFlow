package com.audioflow.discovery

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import com.audioflow.model.AudioOutput
import com.audioflow.model.AudioOutputType

class AudioDiscovery(context: Context) {

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
}
