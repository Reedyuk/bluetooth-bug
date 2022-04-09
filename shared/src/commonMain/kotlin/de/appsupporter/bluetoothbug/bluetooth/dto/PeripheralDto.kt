package de.appsupporter.bluetoothbug.bluetooth.dto

import dev.bluefalcon.BluetoothCharacteristic
import dev.bluefalcon.BluetoothPeripheral
import dev.bluefalcon.BluetoothService
import de.appsupporter.bluetoothbug.bluetooth.enums.DeviceStatus

data class PeripheralDto(
    val peripheral: BluetoothPeripheral,
    val deviceStatus: DeviceStatus,

    var service: BluetoothService?,
    var writeCharacteristic: BluetoothCharacteristic?,
    var readCharacteristic: BluetoothCharacteristic?
)
