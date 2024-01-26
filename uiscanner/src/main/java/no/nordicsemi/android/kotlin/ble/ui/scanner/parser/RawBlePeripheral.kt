package no.nordicsemi.android.kotlin.ble.ui.scanner.parser


/**
 * 可继承的原始数据类
 */
open class RawBlePeripheral {

    var needUpgrade: Boolean = false; // 是否需要升级
    var direction: Direction = Direction.None; // 耳机的角色
    var firmwareType: FirmwareType = FirmwareType.None; // 耳机是否需要Ota升级

    var macAddress: String = "AA:BB:CC:DD:EE:FF";  // mac地址
    var rssi: Int = -999; // 信号强度
    var timestampNanos: Long = 0L; // 发现的时间戳
    var isConnectable: Boolean = false;  // 是否可连接
    override fun toString(): String {
        return "RawBlePeripheral(needUpgrade=$needUpgrade, direction=$direction, firmwareType=$firmwareType, macAddress='$macAddress', rssi=$rssi, timestampNanos=$timestampNanos, isConnectable=$isConnectable)"
    }
}

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

