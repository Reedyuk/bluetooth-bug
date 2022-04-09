//
//  ProgrammingViewModel.swift
//  iosApp
//
//  Created by Nils Kasseckert on 09.04.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI
import CoreBluetooth

@MainActor
class ProgrammingViewModel: ObservableObject {
    private var bluetoothService: BluetoothService!
    
    @Published var isScanning = false
    
    init() {
    
        bluetoothService = BluetoothService(blueFalcon: Blue_falconBlueFalcon(context: UIView(), serviceUUID: nil))
        bluetoothService.bluetoothManager.registerForCallback(callback: self)
        
        bluetoothService.bluetoothManager.startScanning()
      //  CBCentralManager(delegate: nil, queue: nil)
        isScanning = bluetoothService.bluetoothManager.isScanning()
    }
}

extension ProgrammingViewModel: IBluetoothCallback {
    func didDiscoverDevice(deviceDto: DeviceDto) {
        print("Did discover device: \(deviceDto)")
    }
    
    func didReadData(deviceDto: DeviceDto, characteristicType: CharacteristicType, value: KotlinByteArray) {
        print("Did read data from deviceDto: \(deviceDto) with value: \(value)")
    }
    
    
}

