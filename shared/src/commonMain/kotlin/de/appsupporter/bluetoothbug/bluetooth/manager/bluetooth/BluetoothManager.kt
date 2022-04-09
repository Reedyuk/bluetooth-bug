package de.appsupporter.bluetoothbug.bluetooth.manager.bluetooth

import dev.bluefalcon.*
import de.appsupporter.bluetoothbug.bluetooth.dto.DeviceDto
import de.appsupporter.bluetoothbug.bluetooth.dto.PeripheralDto
import de.appsupporter.bluetoothbug.bluetooth.enums.CharacteristicType
import de.appsupporter.bluetoothbug.bluetooth.enums.DeviceStatus

class BluetoothManager(private val blueFalcon: BlueFalcon) : IBluetoothManager {
    private val bluetoothDelegate = BluetoothManagerDelegate()

    private val callbacks: MutableList<IBluetoothCallback> = mutableListOf()

    private val discoveredDevices: MutableList<DeviceDto> = mutableListOf()
    private val connectedDevices: MutableList<DeviceDto> = mutableListOf()

    init {
        blueFalcon.delegates.add(bluetoothDelegate)
    }

    override fun isScanning(): Boolean {
        return blueFalcon.isScanning
    }

    override fun getDiscoveredDevices(): List<DeviceDto> {
        return discoveredDevices
    }

    override fun getConnectedDevices(): List<DeviceDto> {
        return connectedDevices
    }

    override fun registerForCallback(callback: IBluetoothCallback) {
        callbacks.add(callback)
    }

    override fun unregisterForCallback(callback: IBluetoothCallback) {
        callbacks.remove(callback)
    }

    override fun startScanning() {
        if (isScanning()) {
            return
        }

        try {
            blueFalcon.scan()
        } catch(e: Exception) {
            print(e)
        }
    }

    override fun stopScanning() {
        if (!isScanning()) {
            return
        }

        blueFalcon.stopScanning()
    }

    override fun connectTo(device: DeviceDto) {
        val peripheralDto = bluetoothDelegate.getPeripheralDtoFor(device.uuid) ?: return

        blueFalcon.connect(peripheralDto.peripheral)
    }

    override fun disconnectFrom(device: DeviceDto) {
        val peripheralDto = bluetoothDelegate.getPeripheralDtoFor(device.uuid) ?: return

        blueFalcon.disconnect(peripheralDto.peripheral)
    }

    override fun disconnectAll() {
        for (connectedDevice in connectedDevices) {
            disconnectFrom(connectedDevice)
        }
    }

    override fun write(data: ByteArray, device: DeviceDto): Boolean {
        val peripheralDto = bluetoothDelegate.getPeripheralDtoFor(device.uuid) ?: return false
        val characteristic = peripheralDto.writeCharacteristic ?: return false


        blueFalcon.writeCharacteristic(
            peripheralDto.peripheral,
            characteristic,
            data,
            0
        ) //CBCharacteristicWriteWithResponse

        return true
    }

    override fun startReadingFor(
        device: DeviceDto,
        characteristicType: CharacteristicType
    ): Boolean {
        val peripheralDto = bluetoothDelegate.getPeripheralDtoFor(device.uuid) ?: return false

        val characteristic = if (characteristicType == CharacteristicType.Write) {
            peripheralDto.writeCharacteristic
        } else {
            peripheralDto.readCharacteristic
        } ?: return false

        blueFalcon.readCharacteristic(peripheralDto.peripheral, characteristic)

        return true
    }

    inner class BluetoothManagerDelegate : BlueFalconDelegate {

        private val peripherals: MutableMap<String, PeripheralDto> = mutableMapOf()

        override fun didDiscoverDevice(bluetoothPeripheral: BluetoothPeripheral) {
            println("didDiscoverDevice")

            if (peripherals[bluetoothPeripheral.uuid] != null) {
                //Already added
                return
            }

            peripherals[bluetoothPeripheral.uuid] =
                PeripheralDto(
                    bluetoothPeripheral,
                    deviceStatusFrom(bluetoothPeripheral),
                    null,
                    null,
                    null
                )
        }

        override fun didConnect(bluetoothPeripheral: BluetoothPeripheral) {
            println("didConnect")

            connectedDevices.add(deviceDtoFrom(bluetoothPeripheral))
        }

        override fun didDiscoverServices(bluetoothPeripheral: BluetoothPeripheral) {
            println("didDiscoverServices")

            if (peripherals[bluetoothPeripheral.uuid] == null) {
                //No ces device
                return
            }

            var serviceFound = false
            for (service in bluetoothPeripheral.services) {
                if (service.name == "EXAMPLE") {
                    val peripheralDto = peripherals[bluetoothPeripheral.uuid] ?: return
                    peripheralDto.service = service
                    peripherals[bluetoothPeripheral.uuid] = peripheralDto

                    val deviceDto = deviceDtoFrom(bluetoothPeripheral)
                    discoveredDevices.add(deviceDto)
                    for (callback in callbacks) {
                        callback.didDiscoverDevice(deviceDto)
                    }

                    serviceFound = true
                }
            }

            if (!serviceFound) {
                peripherals.remove(bluetoothPeripheral.uuid)
            }
        }

        override fun didReadDescriptor(
            bluetoothPeripheral: BluetoothPeripheral,
            bluetoothCharacteristicDescriptor: BluetoothCharacteristicDescriptor
        ) {
         //   println("read descriptor ${bluetoothCharacteristicDescriptor}")
        }

        override fun didCharacteristcValueChanged(
            bluetoothPeripheral: BluetoothPeripheral,
            bluetoothCharacteristic: BluetoothCharacteristic
        ) {
            val deviceDto = deviceDtoFrom(bluetoothPeripheral)

            val characteristicType =
                if (bluetoothCharacteristic.name == "EXAMPLE") {
                    CharacteristicType.Read
                } else {
                    CharacteristicType.Write
                }

            bluetoothCharacteristic.value?.let {
                for (callback in callbacks) {
                    callback.didReadData(deviceDto, characteristicType, it)
                }
            }
        }

        override fun didDisconnect(bluetoothPeripheral: BluetoothPeripheral) {
            println("did disconnect")
            val removeIndex =
                connectedDevices.indexOfFirst { dto -> dto.uuid == bluetoothPeripheral.uuid }
            connectedDevices.removeAt(removeIndex)
        }

        override fun didDiscoverCharacteristics(bluetoothPeripheral: BluetoothPeripheral) {
            println("didDiscoverCharacteristics")

            if (peripherals[bluetoothPeripheral.uuid] == null) {
                //No ces device
                return
            }

            val peripheralDto = peripherals[bluetoothPeripheral.uuid] ?: return
            val service = peripheralDto.service ?: return

            for (characteristic in service.characteristics) {

            }
        }

        override fun didUpdateMTU(bluetoothPeripheral: BluetoothPeripheral) {
            println("didUpdateMTU")
        }

        override fun didRssiUpdate(bluetoothPeripheral: BluetoothPeripheral) {
            println("didRssiUpdate")

        }

        //Helper
        private fun deviceDtoFrom(bluetoothPeripheral: BluetoothPeripheral): DeviceDto {
            return DeviceDto(bluetoothPeripheral.uuid, deviceStatusFrom(bluetoothPeripheral))
        }

        private fun deviceStatusFrom(bluetoothPeripheral: BluetoothPeripheral): DeviceStatus {
            return if (bluetoothPeripheral.name == "EXAMPLE") {
                DeviceStatus.EXAMPLEService
            } else {
                DeviceStatus.EXAMPLEService
            }
        }

        fun getPeripheralDtoFor(uuid: String): PeripheralDto? {
            return peripherals[uuid]
        }
    }
}