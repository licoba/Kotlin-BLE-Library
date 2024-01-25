package no.nordicsemi.android.kotlin.ble.app.client.screen.repository

import java.util.Locale

/**
 * 数据转换工具类
 *
 */
object DataConversion {

    /**
     * 字节转十六进制字符串
     *
     * @param num
     * @return
     */
    fun byteToHex(num: Byte): String {
        val hexDigits = CharArray(2)
        hexDigits[0] = Character.forDigit((num.toInt() shr 4) and 0xF, 16)
        hexDigits[1] = Character.forDigit(num.toInt() and 0xF, 16)
        return String(hexDigits).uppercase()
    }


    /**
     * 十六进制转byte字节
     *
     * @param hexString
     * @return
     */
    fun hexToByte(hexString: String): Byte {
        return hexString.toInt(16).toByte()
    }


    /**
     * 字节数组转十六进制
     *
     * @param byteArray
     * @return
     */
    fun encodeHexString(byteArray: ByteArray): String {
        val hexStringBuffer = StringBuffer()
        for (i in byteArray.indices) {
            hexStringBuffer.append(byteToHex(byteArray[i]))
        }
        return hexStringBuffer.toString().uppercase(Locale.getDefault())
    }

    /**
     * 十六进制转字节数组
     *
     * @param hexString
     * @return
     */
    fun decodeHexString(hexString: String): ByteArray {
        require(hexString.length % 2 != 1) { "Invalid hexadecimal String supplied.-->$hexString" }
        val bytes = ByteArray(hexString.length / 2)
        var i = 0
        while (i < hexString.length) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2))
            i += 2
        }
        return bytes
    }

    /**
     * 十进制转十六进制
     *
     * @param dec
     * @return
     */
    fun decToHex(dec: Int): String {
        var hex = Integer.toHexString(dec)
        if (hex.length == 1) {
            hex = "0$hex"
        }
        return hex.lowercase(Locale.getDefault())
    }

    /**
     * 十六进制转十进制Long
     *
     * @param hex
     * @return
     */
    fun hexToDec(hex: String): Long {
        return hex.toLong(16)
    }

    /**
     * 十六进制转十进制Int
     *
     * @param hex
     * @return
     */
    fun hexToInt(hex: String): Int {
        return hexToDec(hex).toInt()
    }


}


// 拓展函数
fun Byte.toHex(): String = "%02x".format(this).uppercase()
fun Short.toHex(): String = "%04x".format(this).uppercase()
fun UByte.toHex(): String = "%02x".format(toInt()).uppercase()
fun UInt.toHex(): String = "%08x".format(toInt()).uppercase()
fun ByteArray.toHex(): String = joinToString("") { String.format("%02X", it) }.uppercase()

fun Short.toByteArray(): ByteArray {
    return byteArrayOf((this.toInt() shr 8 and 0xFF).toByte(), (this.toInt() and 0xFF).toByte())
}
