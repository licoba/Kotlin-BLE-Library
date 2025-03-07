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

package no.nordicsemi.android.kotlin.ble.ui.scanner.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.nordicsemi.android.common.theme.NordicTheme
import no.nordicsemi.android.common.theme.view.CircularIcon
import no.nordicsemi.android.common.theme.view.RssiIcon
import no.nordicsemi.android.kotlin.ble.ui.scanner.R

// 这个才是真正的显示的Item
@Composable
fun DeviceListItem(
    name: String?,
    address: String,
    modifier: Modifier = Modifier,
    firmwareType: Int = 1, // 0:普通固件  1:用户固件  2:工厂固件
    extras: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    )
    {

        Box {
            CircularIcon(Icons.Default.Bluetooth)
            if (firmwareType != 0) {
                Text(
                    text = if (firmwareType == 1) "用户" else if(firmwareType == 2)"工厂" else "正常",
                    fontSize = 8.sp,
                    lineHeight = 11.sp,
                    color = if (firmwareType == 1) Color.Black else Color.White,
                    textAlign = TextAlign.Center, // 设置文字居中对齐
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(if (firmwareType == 1) Color.Green else Color.Blue, shape = RoundedCornerShape(2.dp))
                        .width(22.dp)
                        .height(11.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            name?.takeIf { it.isNotEmpty() }?.let { name ->
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )
            } ?: Text(
                text = stringResource(id = R.string.device_no_name),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.alpha(0.7f)
            )
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        extras()
    }
}

@Preview
@Composable
private fun DeviceListItemPreview() {
    NordicTheme {
        DeviceListItem(
            name = "Device name",
            address = "AA:BB:CC:DD:EE:FF",
            extras = {
                RssiIcon(rssi = -45)
            }
        )
    }
}