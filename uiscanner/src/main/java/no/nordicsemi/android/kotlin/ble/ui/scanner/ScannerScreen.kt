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
package no.nordicsemi.android.kotlin.ble.ui.scanner

import android.os.ParcelUuid
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.kotlin.ble.ui.scanner.main.DeviceListItem
import no.nordicsemi.android.kotlin.ble.ui.scanner.view.ScannerAppBar
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanResults
import no.nordicsemi.android.kotlin.ble.ui.scanner.parser.RawBlePeripheral
import no.nordicsemi.android.kotlin.ble.ui.scanner.parser.StandParser

/**
 * 扫描页面
 */
@Composable
fun ScannerScreen(
    title: String = stringResource(id = R.string.scanner_screen),
    uuid: ParcelUuid?,
    cancellable: Boolean = true,
    onResult: (ScannerScreenResult) -> Unit,
    deviceItem: @Composable (BleScanResults) -> Unit = {
        val rawBlePeripheral = StandParser<RawBlePeripheral>().parseBleData(it.lastScanResult!!)
        Log.d("TAG", "解析结果: $rawBlePeripheral")
        DeviceListItem(it.advertisedName ?: it.device.name, it.device.address)
    }
) {
    var isScanning by rememberSaveable { mutableStateOf(false) }

    Column {
        if (cancellable) {
            ScannerAppBar(title, isScanning) { onResult(ScanningCancelled) }
        } else {
            ScannerAppBar(title, isScanning)
        }
        ScannerView(
            uuid = uuid,
            onScanningStateChanged = { isScanning = it },
            onResult = { onResult(DeviceSelected(it)) },
            deviceItem = deviceItem,
        )
    }
}
