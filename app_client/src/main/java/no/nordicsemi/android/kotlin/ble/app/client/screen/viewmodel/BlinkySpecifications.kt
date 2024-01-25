package no.nordicsemi.android.kotlin.ble.app.client.screen.viewmodel

import java.util.UUID

object BlinkySpecifications {
    /** Nordic Blinky Service UUID. */
//    val UUID_SERVICE_DEVICE: UUID = UUID.fromString("00001523-1212-efde-1523-785feabcd123")

    /** LED characteristic UUID. */
    val UUID_LED_CHAR: UUID = UUID.fromString("00001525-1212-efde-1523-785feabcd123")

    /** BUTTON characteristic UUID. */
    val UUID_BUTTON_CHAR: UUID = UUID.fromString("00001524-1212-efde-1523-785feabcd123")

    val UUID_BATTERY_CHAR: UUID = UUID.fromString("592e0001-432f-4669-84b1-ddc45cc40dd9")

    val UUID_SERVICE_DEVICE: UUID = UUID_BATTERY_CHAR

}

object StandServiceUUID {
    val S_DEVICE_INFO: UUID = StandardUUIDWith("180A") // 设备信息
    val C_Manufacturer_Name: UUID = StandardUUIDWith("2a29") // 厂商名
    val C_Serial_Number: UUID = StandardUUIDWith("2a25") // 序列号
}


object WSeriesServiceUUID {
    // 音频数据服务
    val AUDIO_DATA: UUID = TmkUUIDWith("0001") // 音频数据服务的UID
    val AUDIO_DATA_WRITE: UUID = TmkUUIDWith("0002") // 音频数据服务Write特征
    val AUDIO_DATA_NOTIFY: UUID = TmkUUIDWith("0003") // 音频数据服务Notify特征
    // 命令数据服务
    val COMMAND_DATA: UUID = TmkUUIDWith("0004") // 命令数据服务的UID
    val COMMAND_DATA_WRITE: UUID = TmkUUIDWith("0005") // 命令数据服务Write特征
    val COMMAND_DATA_NOTIFY: UUID = TmkUUIDWith("0006") // 命令数据服务Notify特征


}



enum class ScanUUIDFilter(val uuid: UUID) {
    WT2(TmkUUIDWith("6f25")),
    WT2_BK(TmkUUIDWith("6f26")),
    M2(TmkUUIDWith("6f27")),
    WT2_Edge(TmkUUIDWith("6f28")),
    WT2_Edge_Fac(TmkUUIDWith("6f20")),
    M2P(TmkUUIDWith("6f29")),
    M3(TmkUUIDWith("6f2a")),
    W3Pro(TmkUUIDWith("6f31")),
    W3Pro_Fac(TmkUUIDWith("6f32")),
    W_UPGRADE(StandardUUIDWith("FE59"));

}

fun TmkUUIDWith(channel: String): UUID {
    return UUID.fromString("592e$channel-432f-4669-84b1-ddc45cc40dd9")
}

fun StandardUUIDWith(channel: String): UUID {
    return UUID.fromString("0000$channel-0000-1000-8000-00805f9b34fb")
}