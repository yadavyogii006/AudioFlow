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
        val outputs = mutableListOf<AudioOutput>()
        var speakerFound = false

        devices.forEach { device ->
            when (device.type) {
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> {
                    speakerFound = true
                    outputs.add(
                        AudioOutput(
                            id = "phone-speaker",
                            name = "Phone Speaker",
                            type = AudioOutputType.PHONE_SPEAKER
                        )
                    )
                }

                AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                AudioDeviceInfo.TYPE_WIRED_HEADSET -> outputs.add(
                    AudioOutput(
                        id = "wired-${device.id}",
                        name = device.productName.toString().ifBlank { "Wired Headphones" },
                        type = AudioOutputType.WIRED_HEADPHONES
                    )
                )

                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
                AudioDeviceInfo.TYPE_BLE_HEADSET,
                AudioDeviceInfo.TYPE_BLE_SPEAKER -> {
                    val key = "bluetooth-${device.id}"

                    outputs.add(
                        AudioOutput(
                            id = "$key-left",
                            name = "Earbud Left",
                            type = AudioOutputType.BLUETOOTH
                        )
                    )
                    outputs.add(
                        AudioOutput(
                            id = "$key-right",
                            name = "Earbud Right",
                            type = AudioOutputType.BLUETOOTH
                        )
                    )
                }

                AudioDeviceInfo.TYPE_USB_DEVICE,
                AudioDeviceInfo.TYPE_USB_HEADSET -> outputs.add(
                    AudioOutput(
                        id = "usb-${device.id}",
                        name = device.productName.toString().ifBlank { "USB Audio" },
                        type = AudioOutputType.USB
                    )
                )
            }
        }

        if (!speakerFound) {
            outputs.add(
                0,
                AudioOutput(
                    id = "phone-speaker",
                    name = "Phone Speaker",
                    type = AudioOutputType.PHONE_SPEAKER
                )
            )
        }

        return outputs.distinctBy { it.id }
    }
}
