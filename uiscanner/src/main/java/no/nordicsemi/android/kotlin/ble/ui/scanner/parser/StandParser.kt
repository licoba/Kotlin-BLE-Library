package no.nordicsemi.android.kotlin.ble.ui.scanner.parser

import no.nordicsemi.android.common.core.DataByteArray
import no.nordicsemi.android.kotlin.ble.ui.scanner.repository.RawBlePeripheral


/**
 * T：要解析出来的对象类
 * 这是一个标准解析器，可以继承它，实现自己的解析器
 */
open class StandParser<T : RawBlePeripheral> {
    open fun parseManufacturerSpecificData(rawData: DataByteArray): T? {
        return null
    }

}