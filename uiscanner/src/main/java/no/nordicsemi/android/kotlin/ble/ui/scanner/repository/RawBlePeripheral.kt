package no.nordicsemi.android.kotlin.ble.ui.scanner.repository

import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanResultData
import java.io.Serializable


/**
 * 可继承的原始数据类
 */
open class RawBlePeripheral(
    var needUpgrade: Boolean = false, // 是否需要升级
    var direction: Direction = Direction.None, // 耳机的角色
    var firmwareType: FirmwareType = FirmwareType.None, // 耳机是否需要Ota升级
    var firmwareVersion: String = "0.0.0",  // 固件版本号
    var macAddress: String = "AA:BB:CC:DD:EE:FF"  // mac地址
)

// 耳机角色
enum class Direction {
    Left,
    Right,
    None
}

// 耳机固件是工厂还是用户
enum class FirmwareType {
    Factory,
    User,
    None
}

