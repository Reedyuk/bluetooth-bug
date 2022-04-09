package de.appsupporter.bluetoothbug.bluetooth.manager.bluetooth

import de.appsupporter.bluetoothbug.bluetooth.dto.DeviceDto
import de.appsupporter.bluetoothbug.bluetooth.enums.CharacteristicType

interface IBluetoothCallback {
    fun didDiscoverDevice(deviceDto: DeviceDto)
    fun didReadData(deviceDto: DeviceDto, characteristicType: CharacteristicType, value: ByteArray)
}