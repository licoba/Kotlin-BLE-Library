package no.nordicsemi.android.kotlin.ble.ui.scanner.parser

import android.util.Log
import androidx.core.util.getOrDefault
import androidx.core.util.valueIterator
import no.nordicsemi.android.common.core.DataByteArray
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanRecord
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanResultData


/**
 * T：要解析出来的对象类
 * 这是一个标准解析器，可以继承它，实现自己的解析器
 */

open class StandParser<T : RawBlePeripheral> {


    /**
     * BleScanResultData(
     *     rssi = -42,
     *     timestampNanos = 72427236526452,
     *     scanRecord = BleScanRecord(
     *         advertiseFlag = 5,
     *         serviceUuids = [
     *             592e6f28-432f-4669-84b1-ddc45cc40dd9
     *         ],
     *         serviceData = {},
     *         serviceSolicitationUuids = [],
     *         deviceName = "",
     *         txPowerLevel = -2147483648,
     *         bytes = (0x) 02:01:05:11:07:D9:0D:C4:5C:C4:DD:B1:84:69:46:2F:43:28:6F:2E:59:1C:FF:FF:FF:F7:4B:43:51:53:1A:04:25:10:1E:64:03:00:AE:00:57:54:58:2D:4C:20:20:20:00:06,
     *         manufacturerSpecificData = {
     *             65535 = (0x) F7:4B:43:51:53:1A:04:25:10:1E:64:03:00:AE:00:57:54:58:2D:4C:20:20:20:00:06
     *         }
     *     ),
     *     advertisingSid = null,
     *     primaryPhy = PHY_LE_1M,
     *     secondaryPhy = null,
     *     txPower = null,
     *     periodicAdvertisingInterval = null,
     *     isLegacy = true,
     *     isConnectable = true,
     *     dataStatus = DATA_COMPLETE
     * )
     */

    @Suppress("UNCHECKED_CAST")
    open fun parseBleData(bleScanResultData: BleScanResultData): T {
        Log.d("TAG", "开始解析最新的bleScanResultData:$bleScanResultData")
        val rawBlePeripheral = bleScanResultData.toRawBlePeripheral()
        parseWManufacturerSpecificData(bleScanResultData.scanRecord, rawBlePeripheral)
        return rawBlePeripheral as T
    }


    // 映射
    private fun BleScanResultData.toRawBlePeripheral(): RawBlePeripheral {
        val resultData = this
        return RawBlePeripheral().apply {
            rssi = resultData.rssi
            timestampNanos = resultData.timestampNanos
            isConnectable = resultData.isConnectable ?: false
        }
    }


    private fun parseWManufacturerSpecificData(
        record: BleScanRecord?,
        peripheral: RawBlePeripheral
    ) {
        if (record == null) return
        val specificData = record.manufacturerSpecificData.valueIterator().next()
        if (specificData.size == 0) {
            Log.d("TAG", "解析失败，specificData为空")
            return
        }

        var index = 0
        // MAC地址（6字节）
        val macArray = specificData.copyOfRange(index, index + 6).also { index += 6 }
        peripheral.macAddress = macArray.toString().replace("(0x) ", "")
    }


}