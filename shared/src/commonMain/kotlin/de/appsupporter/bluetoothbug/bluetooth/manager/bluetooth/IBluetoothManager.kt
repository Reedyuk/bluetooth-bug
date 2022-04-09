package de.appsupporter.bluetoothbug.bluetooth.manager.bluetooth

import de.appsupporter.bluetoothbug.bluetooth.dto.DeviceDto
import de.appsupporter.bluetoothbug.bluetooth.enums.CharacteristicType

interface IBluetoothManager {
    fun isScanning(): Boolean
    fun getDiscoveredDevices(): List<DeviceDto>
    fun getConnectedDevices(): List<DeviceDto>

    fun registerForCallback(callback: IBluetoothCallback)
    fun unregisterForCallback(callback: IBluetoothCallback)

    fun startScanning()
    fun stopScanning()

    fun connectTo(device: DeviceDto)
    fun disconnectFrom(device: DeviceDto)
    fun disconnectAll()

    fun write(data: ByteArray, device: DeviceDto): Boolean
    fun startReadingFor(device: DeviceDto, characteristicType: CharacteristicType): Boolean
}