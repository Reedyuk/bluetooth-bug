package de.appsupporter.bluetoothbug

import de.appsupporter.bluetoothbug.bluetooth.manager.bluetooth.BluetoothManager
import dev.bluefalcon.BlueFalcon

class BluetoothService(private val blueFalcon: BlueFalcon) {
    val bluetoothManager = BluetoothManager(blueFalcon)
}