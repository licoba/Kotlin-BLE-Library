package no.nordicsemi.android.kotlin.ble.profile.csc

import no.nordicsemi.android.kotlin.ble.profile.common.Data
import no.nordicsemi.android.kotlin.ble.profile.error.InvalidDataReceived
import kotlin.experimental.and

class CSCDataParser {

    private var previousData: CSCDataSnapshot = CSCDataSnapshot()

    private var wheelRevolutions: Long = -1
    private var wheelEventTime: Int = -1
    private var crankRevolutions: Long = -1
    private var crankEventTime: Int = -1

    fun parse(byteArray: ByteArray, wheelSize: WheelSize = WheelSizes.default): CSCData {
        val data = Data(byteArray)

        if (data.size() < 1) {
            throw InvalidDataReceived()
        }

        // Decode the new data
        var offset = 0
        val flags: Byte = data.getByte(offset)!!
        offset += 1

        val wheelRevPresent = (flags and 0x01).toInt() != 0
        val crankRevPreset = (flags and 0x02).toInt() != 0

        if (data.size() < 1 + (if (wheelRevPresent) 6 else 0) + (if (crankRevPreset) 4 else 0)) {
            throw InvalidDataReceived()
        }

        if (wheelRevPresent) {
            wheelRevolutions = data.getIntValue(Data.FORMAT_UINT32_LE, offset)!!.toLong() and 0xFFFFFFFFL
            offset += 4
            wheelEventTime = data.getIntValue(Data.FORMAT_UINT16_LE, offset)!! // 1/1024 s
            offset += 2
        }

        if (crankRevPreset) {
            crankRevolutions = data.getIntValue(Data.FORMAT_UINT16_LE, offset)!!.toLong()
            offset += 2
            crankEventTime = data.getIntValue(Data.FORMAT_UINT16_LE, offset)!!
            // offset += 2;
        }

        val wheelCircumference = wheelSize.value.toFloat()

        return CSCData(
            totalDistance = getTotalDistance(wheelSize.value.toFloat()),
            distance = getDistance(wheelCircumference, previousData),
            speed = getSpeed(wheelCircumference, previousData),
            wheelSize = wheelSize,
            cadence = getCrankCadence(previousData),
            gearRatio = getGearRatio(previousData),
        ).also {
            previousData = CSCDataSnapshot(
                wheelRevolutions,
                wheelEventTime,
                crankRevolutions,
                crankEventTime
            )
        }
    }

    private fun getTotalDistance(wheelCircumference: Float): Float {
        return wheelRevolutions.toFloat() * wheelCircumference / 1000.0f // [m]
    }

    /**
     * Returns the distance traveled since the given response was received.
     *
     * @param wheelCircumference the wheel circumference in millimeters.
     * @param previous a previous response.
     * @return distance traveled since the previous response, in meters.
     */
    private fun getDistance(
        wheelCircumference: Float,
        previous: CSCDataSnapshot
    ): Float {
        return (wheelRevolutions - previous.wheelRevolutions).toFloat() * wheelCircumference / 1000.0f // [m]
    }

    /**
     * Returns the average speed since the previous response was received.
     *
     * @param wheelCircumference the wheel circumference in millimeters.
     * @param previous a previous response.
     * @return speed in meters per second.
     */
    private fun getSpeed(
        wheelCircumference: Float,
        previous: CSCDataSnapshot
    ): Float {
        val timeDifference: Float = if (wheelEventTime < previous.wheelEventTime) {
            (65535 + wheelEventTime - previous.wheelEventTime) / 1024.0f
        } else {
            (wheelEventTime - previous.wheelEventTime) / 1024.0f
        } // [s]
        return getDistance(wheelCircumference, previous) / timeDifference // [m/s]
    }

    /**
     * Returns average wheel cadence since the previous message was received.
     *
     * @param previous a previous response.
     * @return wheel cadence in revolutions per minute.
     */
    private fun getWheelCadence(previous: CSCDataSnapshot): Float {
        val timeDifference: Float = if (wheelEventTime < previous.wheelEventTime)  {
            (65535 + wheelEventTime - previous.wheelEventTime) / 1024.0f
        } else {
            (wheelEventTime - previous.wheelEventTime) / 1024.0f
        } // [s]
        return if (timeDifference == 0f) {
            0.0f
        } else {
            (wheelRevolutions - previous.wheelRevolutions) * 60.0f / timeDifference
        }
        // [revolutions/minute];
    }

    /**
     * Returns average crank cadence since the previous message was received.
     *
     * @param previous a previous response.
     * @return crank cadence in revolutions per minute.
     */
    private fun getCrankCadence(previous: CSCDataSnapshot): Float {
        val timeDifference: Float = if (crankEventTime < previous.crankEventTime) {
            (65535 + crankEventTime - previous.crankEventTime) / 1024.0f // [s]
        } else {
            (crankEventTime - previous.crankEventTime) / 1024.0f
        } // [s]
        return if (timeDifference == 0f) {
            0.0f
        } else {
            (crankRevolutions - previous.crankRevolutions) * 60.0f / timeDifference
        }
        // [revolutions/minute];
    }

    /**
     * Returns the gear ratio (equal to wheel cadence / crank cadence).
     * @param previous a previous response.
     * @return gear ratio.
     */
    private fun getGearRatio(previous: CSCDataSnapshot): Float {
        val crankCadence = getCrankCadence(previous)
        return if (crankCadence > 0) {
            getWheelCadence(previous) / crankCadence
        } else {
            0.0f
        }
    }
}
