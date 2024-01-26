package no.nordicsemi.android.kotlin.ble.ui.scanner.parser

import no.nordicsemi.android.kotlin.ble.ui.scanner.parser.RawBlePeripheral

class WT2BlePeripheral : RawBlePeripheral() {
    var firmwareVersion: String = "0.0.0";  // 固件版本号（软件版本号）
    val stVersion: Int = -1;  // 硬件版本号
}
