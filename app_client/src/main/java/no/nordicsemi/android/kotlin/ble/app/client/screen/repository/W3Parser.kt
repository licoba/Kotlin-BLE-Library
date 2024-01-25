package no.nordicsemi.android.kotlin.ble.app.client.screen.repository

import android.util.Log
import no.nordicsemi.android.common.core.DataByteArray

object W3Parser {

    val TAG = "W3Parser"

    fun parseW3Packet(dataByteArray: DataByteArray): W3Packet? {
        val headerValue = dataByteArray.copyOfRange(0, 2).value
        if (!headerValue.contentEquals(byteArrayOf(0x74, 0x6b))) {
            Log.d(TAG, "解析失败，header不是W3的header $headerValue")
            return null
        }
        val cmdValue = dataByteArray.getByte(2)
        val w3Cmd = findCmd(cmdValue)
        if (w3Cmd == null) {
            Log.d(TAG, "解析失败，cmd不是W3的cmd")
            return null
        }
        val type = dataByteArray.getByte(3)!!
        val length = dataByteArray.getByte(4)!!
        val data = dataByteArray.copyOfRange(5, 5 + length.toInt()).value
        Log.d(TAG, "解析成功，w3Cmd: $w3Cmd")
        return W3Packet(0x746b, w3Cmd, type, length, data)
    }


    // 遍历W3CMD，如果byte一样就返回，参考协议文档
    fun findCmd(byte: Byte?): W3CMD? {
        if (byte == null) return null
        Log.d(TAG, "findCmd byte: $byte")
        for (cmd in W3CMD.entries) {
            Log.d(TAG, "findCmd cmd byte: ${cmd.byte}")
            if (cmd.byte == byte) {
                return cmd
            }
        }
        Log.d(TAG, "没有找到对应的cmd")
        return null
    }

}