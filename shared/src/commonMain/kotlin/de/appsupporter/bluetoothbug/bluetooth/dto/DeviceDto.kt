package de.appsupporter.bluetoothbug.bluetooth.dto

import de.appsupporter.bluetoothbug.bluetooth.enums.DeviceStatus

data class DeviceDto(
    val uuid: String,
    val deviceStatus: DeviceStatus,
)
