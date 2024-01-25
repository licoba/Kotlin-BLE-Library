package no.nordicsemi.android.kotlin.ble.app.client.screen.repository

/*
 W 系列指令内容      header(0x74,0x6b)     cmd       paramLen  [+ param]
 W 系列指令长度         2                   2           1
 ------------------------------
 M 系列指令内容      裸数据
 */
/**
 * Packet类代表一个具有五个属性的数据结构：header, cmd, type, length 和 data。
 *
 * @property header 固定的包头，默认值是0x746b。
 * @property cmd 命令码。
 * @property type 类型码。
 * @property length 数据的长度，只计算数据的长度，不包括前5字节的头部。
 * @property data 数据，长度由具体的项目决定。
 */
data class W3Packet(
    val header: Short = 0x746b, // 固定的包头
    val cmd: W3CMD, // 命令码
    val type: Byte, // 类型码
    val length: Byte, // 数据的长度
    val data: ByteArray // 数据

) {
    override fun toString(): String {
        return "W3Packet(header=${header.toHex()}, cmd=$cmd, type=${type.toHex()}, length=${
            length.toHex()
        }, data=${data.toHex()})"
    }

}

enum class W3CMD(val byte: Byte) {
    Auth(0x19) // app认证
}