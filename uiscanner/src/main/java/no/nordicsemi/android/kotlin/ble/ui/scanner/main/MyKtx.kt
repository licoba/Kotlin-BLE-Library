package no.nordicsemi.android.kotlin.ble.ui.scanner.main

import no.nordicsemi.android.common.core.DataByteArray

object MyKtx {
}

fun DataByteArray.Companion.from(packet: W3Packet):DataByteArray{
    return DataByteArray.from(*packet.toByteArray())
}
