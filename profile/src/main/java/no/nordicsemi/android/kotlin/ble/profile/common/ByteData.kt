/*
 * Copyright (c) 2023, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package no.nordicsemi.android.kotlin.ble.profile.common

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import androidx.annotation.IntRange

open class ByteData(val value: ByteArray) {

    /**
     * Return the stored value of this characteristic.
     *
     * See [.getValue] for details.
     *
     * @param offset Offset at which the string value can be found.
     * @return Cached value of the characteristic
     */
    fun getStringValue(@IntRange(from = 0) offset: Int): String? {
        if (offset > value.size) {
            return null
        }
        val strBytes = ByteArray(value.size - offset)
        for (i in 0 until value.size - offset) {
            strBytes[i] = value[offset + i]
        }
        return String(strBytes)
    }

    /**
     * Returns the size of underlying byte array.
     *
     * @return Length of the data.
     */
    fun size(): Int {
        return value.size
    }

    override fun toString(): String {
        if (size() == 0) {
            return ""
        }
        val out = CharArray(value.size * 3 - 1)
        for (j in value.indices) {
            val v = value[j].toInt() and 0xFF
            out[j * 3] = HEX_ARRAY[v ushr 4]
            out[j * 3 + 1] = HEX_ARRAY[v and 0x0F]
            if (j != value.size - 1) {
                out[j * 3 + 2] = '-'
            }
        }
        return "(0x) $out"
    }

    /**
     * Returns a byte at the given offset from the byte array.
     *
     * @param offset Offset at which the byte value can be found.
     * @return Cached value or null of offset exceeds value size.
     */
    fun getByte(@IntRange(from = 0) offset: Int): Byte? {
        return value.getOrNull(offset)
    }

    /**
     * Returns an integer value from the byte array.
     *
     *
     *
     * The formatType parameter determines how the value
     * is to be interpreted. For example, setting formatType to
     * [.FORMAT_UINT16_LE] specifies that the first two bytes of the
     * value at the given offset are interpreted to generate the
     * return value.
     *
     * @param formatType The format type used to interpret the value.
     * @param offset     Offset at which the integer value can be found.
     * @return Cached value or null of offset exceeds value size.
     */
    fun getIntValue(
        formatType: IntFormat,
        @IntRange(from = 0) offset: Int
    ): Int? {
        if (offset + getTypeLen(formatType) > size()) {
            return null
        }

        when (formatType) {
            IntFormat.FORMAT_UINT8 -> return unsignedByteToInt(value[offset])
            IntFormat.FORMAT_UINT16_LE -> return unsignedBytesToInt(value[offset], value[offset + 1])
            IntFormat.FORMAT_UINT16_BE -> return unsignedBytesToInt(value[offset + 1], value[offset])
            IntFormat.FORMAT_UINT24_LE -> return unsignedBytesToInt(
                value[offset],
                value[offset + 1],
                value[offset + 2],
                0.toByte()
            )
            IntFormat.FORMAT_UINT24_BE -> return unsignedBytesToInt(
                value[offset + 2],
                value[offset + 1],
                value[offset],
                0.toByte()
            )
            IntFormat.FORMAT_UINT32_LE -> return unsignedBytesToInt(
                value[offset],
                value[offset + 1],
                value[offset + 2],
                value[offset + 3]
            )
            IntFormat.FORMAT_UINT32_BE -> return unsignedBytesToInt(
                value[offset + 3],
                value[offset + 2],
                value[offset + 1],
                value[offset]
            )
            IntFormat.FORMAT_SINT8 -> return unsignedToSigned(unsignedByteToInt(value[offset]), 8)
            IntFormat.FORMAT_SINT16_LE -> return unsignedToSigned(unsignedBytesToInt(value[offset], value[offset + 1]), 16)
            IntFormat.FORMAT_SINT16_BE -> return unsignedToSigned(unsignedBytesToInt(value[offset + 1], value[offset]), 16)
            IntFormat.FORMAT_SINT24_LE -> return unsignedToSigned(
                unsignedBytesToInt(
                    value[offset],
                    value[offset + 1],
                    value[offset + 2], 0.toByte()
                ), 24
            )
            IntFormat.FORMAT_SINT24_BE -> return unsignedToSigned(
                unsignedBytesToInt(
                    0.toByte(),
                    value[offset + 2],
                    value[offset + 1],
                    value[offset]
                ), 24
            )
            IntFormat.FORMAT_SINT32_LE -> return unsignedToSigned(
                unsignedBytesToInt(
                    value[offset],
                    value[offset + 1],
                    value[offset + 2],
                    value[offset + 3]
                ), 32
            )
            IntFormat.FORMAT_SINT32_BE -> return unsignedToSigned(
                unsignedBytesToInt(
                    value[offset + 3],
                    value[offset + 2],
                    value[offset + 1],
                    value[offset]
                ), 32
            )
        }
    }

    /**
     * Returns a long value from the byte array.
     *
     * Only [.FORMAT_UINT32_LE] and [.FORMAT_SINT32_LE] are supported.
     *
     * The formatType parameter determines how the value
     * is to be interpreted. For example, setting formatType to
     * [.FORMAT_UINT32_LE] specifies that the first four bytes of the
     * value at the given offset are interpreted to generate the
     * return value.
     *
     * @param formatType The format type used to interpret the value.
     * @param offset     Offset at which the integer value can be found.
     * @return Cached value or null of offset exceeds value size.
     */
    fun getLongValue(
        formatType: LongFormat,
        @IntRange(from = 0) offset: Int
    ): Long? {
        if (offset + getTypeLen(formatType) > size()) return null
        when (formatType) {
            LongFormat.FORMAT_UINT32_LE -> return unsignedBytesToLong(
                value[offset],
                value[offset + 1],
                value[offset + 2],
                value[offset + 3]
            )
            LongFormat.FORMAT_UINT32_BE -> return unsignedBytesToLong(
                value[offset + 3],
                value[offset + 2],
                value[offset + 1],
                value[offset]
            )
            LongFormat.FORMAT_SINT32_LE -> return unsignedToSigned(
                unsignedBytesToLong(
                    value[offset],
                    value[offset + 1],
                    value[offset + 2],
                    value[offset + 3]
                ), 32
            )
            LongFormat.FORMAT_SINT32_BE -> return unsignedToSigned(
                unsignedBytesToLong(
                    value[offset + 3],
                    value[offset + 2],
                    value[offset + 1],
                    value[offset]
                ), 32
            )
        }
    }

    /**
     * Returns an float value from the given byte array.
     *
     * @param formatType The format type used to interpret the value.
     * @param offset     Offset at which the float value can be found.
     * @return Cached value at a given offset or null if the requested offset exceeds the value size.
     */
    fun getFloatValue(
        formatType: FloatFormat,
        @IntRange(from = 0) offset: Int
    ): Float? {
        if (offset + getTypeLen(formatType) > size()) {
            return null
        }
        when (formatType) {
            FloatFormat.FORMAT_SFLOAT -> {
                if (value[offset + 1] == 0x07.toByte() && value[offset] == 0xFE.toByte()) {
                    return Float.POSITIVE_INFINITY
                }
                if (value[offset + 1].toInt() == 0x07 && value[offset] == 0xFF.toByte()
                    || value[offset + 1].toInt() == 0x08 && value[offset].toInt() == 0x00
                    || value[offset + 1].toInt() == 0x08 && value[offset].toInt() == 0x01
                ) {
                    return Float.NaN
                }
                return if (value[offset + 1].toInt() == 0x08 && value[offset].toInt() == 0x02) {
                    Float.NEGATIVE_INFINITY
                } else {
                    bytesToFloat(value[offset], value[offset + 1])
                }
            }
            FloatFormat.FORMAT_FLOAT -> {
                if (value[offset + 3].toInt() == 0x00) {
                    if (value[offset + 2].toInt() == 0x7F && value[offset + 1] == 0xFF.toByte()) {
                        if (value[offset] == 0xFE.toByte()) return Float.POSITIVE_INFINITY
                        if (value[offset] == 0xFF.toByte()) return Float.NaN
                    } else if (value[offset + 2] == 0x80.toByte() && value[offset + 1].toInt() == 0x00) {
                        if (value[offset].toInt() == 0x00 || value[offset].toInt() == 0x01) return Float.NaN
                        if (value[offset].toInt() == 0x02) return Float.NEGATIVE_INFINITY
                    }
                }
                return bytesToFloat(value[offset], value[offset + 1], value[offset + 2], value[offset + 3])
            }
        }
    }

    companion object {
        private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()

        fun from(value: String): ByteData {
            return ByteData(value.toByteArray()) // UTF-8
        }

        fun from(characteristic: BluetoothGattCharacteristic): ByteData {
            return ByteData(characteristic.value)
        }

        fun from(descriptor: BluetoothGattDescriptor): ByteData {
            return ByteData(descriptor.value)
        }

        fun opCode(opCode: Byte): ByteData {
            return ByteData(byteArrayOf(opCode))
        }

        fun opCode(opCode: Byte, parameter: Byte): ByteData {
            return ByteData(byteArrayOf(opCode, parameter))
        }

        /**
         * Convert a signed byte to an unsigned int.
         */
        private fun unsignedByteToInt(b: Byte): Int {
            return b.toInt() and 0xFF
        }

        /**
         * Convert a signed byte to an unsigned int.
         */
        private fun unsignedByteToLong(b: Byte): Long {
            return b.toLong() and 0xFFL
        }

        /**
         * Convert signed bytes to a 16-bit unsigned int.
         */
        private fun unsignedBytesToInt(b0: Byte, b1: Byte): Int {
            return unsignedByteToInt(b0) + (unsignedByteToInt(b1) shl 8)
        }

        /**
         * Convert signed bytes to a 32-bit unsigned int.
         */
        private fun unsignedBytesToInt(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Int {
            return (unsignedByteToInt(b0) + (unsignedByteToInt(b1) shl 8)
                    + (unsignedByteToInt(b2) shl 16) + (unsignedByteToInt(b3) shl 24))
        }

        /**
         * Convert signed bytes to a 32-bit unsigned long.
         */
        private fun unsignedBytesToLong(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Long {
            return (unsignedByteToLong(b0) + (unsignedByteToLong(b1) shl 8)
                    + (unsignedByteToLong(b2) shl 16) + (unsignedByteToLong(b3) shl 24))
        }

        /**
         * Convert signed bytes to a 16-bit short float value.
         */
        private fun bytesToFloat(b0: Byte, b1: Byte): Float {
            val mantissa = unsignedToSigned(
                unsignedByteToInt(b0) + (unsignedByteToInt(b1) and 0x0F shl 8),
                12
            )
            val exponent = unsignedToSigned(unsignedByteToInt(b1) shr 4, 4)
            return (mantissa * Math.pow(10.0, exponent.toDouble())).toFloat()
        }

        /**
         * Convert signed bytes to a 32-bit short float value.
         */
        private fun bytesToFloat(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Float {
            val mantissa = unsignedToSigned(
                unsignedByteToInt(b0)
                        + (unsignedByteToInt(b1) shl 8)
                        + (unsignedByteToInt(b2) shl 16),
                24
            )
            return (mantissa * Math.pow(10.0, b3.toDouble())).toFloat()
        }

        /**
         * Convert an unsigned integer value to a two's-complement encoded
         * signed value.
         */
        private fun unsignedToSigned(unsigned: Int, size: Int): Int {
            return unsigned.takeIf { unsigned and (1 shl size - 1) == 0 }
                ?: (-1 * ((1 shl size - 1) - (unsigned and (1 shl size - 1) - 1)))
        }

        /**
         * Convert an unsigned long value to a two's-complement encoded
         * signed value.
         */
        private fun unsignedToSigned(unsigned: Long, size: Int): Long {
            return unsigned.takeIf { unsigned and (1L shl size - 1) == 0L }
                ?: (-1 * ((1L shl (size - 1)) - (unsigned.toInt() and (1 shl size - 1) - 1)))
        }
    }
}
