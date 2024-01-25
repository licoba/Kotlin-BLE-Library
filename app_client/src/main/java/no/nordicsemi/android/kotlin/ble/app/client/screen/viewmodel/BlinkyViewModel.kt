package no.nordicsemi.android.kotlin.ble.app.client.screen.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.core.DataByteArray
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import no.nordicsemi.android.kotlin.ble.app.client.BlinkyDestinationId
import no.nordicsemi.android.kotlin.ble.app.client.screen.repository.BlinkyButtonParser
import no.nordicsemi.android.kotlin.ble.app.client.screen.repository.BlinkyLedParser
import no.nordicsemi.android.kotlin.ble.app.client.screen.repository.W3Parser
import no.nordicsemi.android.kotlin.ble.app.client.screen.view.BlinkyViewState
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattServices
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import java.util.*
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class BlinkyViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val navigator: Navigator,
    private val savedStateHandle: SavedStateHandle
) : SimpleNavigationViewModel(navigator, savedStateHandle) {

    private val _device = MutableStateFlow<ServerDevice?>(null)
    val device = _device.asStateFlow()

    private val _state = MutableStateFlow(BlinkyViewState())
    val state = _state.asStateFlow()

    private var client: ClientBleGatt? = null

    init {
        val blinkyDevice = parameterOf(BlinkyDestinationId)
        _device.value = blinkyDevice
        startGattClient(blinkyDevice)
    }

    private lateinit var commandWriteCharacteristic: ClientBleGattCharacteristic
    private lateinit var commandNotifyCharacteristic: ClientBleGattCharacteristic

    private fun startGattClient(blinkyDevice: ServerDevice) = viewModelScope.launch {
        //Connect a Bluetooth LE device.
        val client = ClientBleGatt.connect(context, blinkyDevice, viewModelScope).also {
            this@BlinkyViewModel.client = it
        }

        if (!client.isConnected) {
            return@launch
        }

        //Discover services on the Bluetooth LE Device.
        val services = client.discoverServices()
        configureGatt(services)
    }

    private suspend fun configureGatt(services: ClientBleGattServices) {
        //Remember needed service and characteristics which are used to communicate with the DK.
        val service = services.findService(WSeriesServiceUUID.COMMAND_DATA)!!
        Log.d("TAG", "找到了指定的服务：$service")

        commandWriteCharacteristic =
            service.findCharacteristic(WSeriesServiceUUID.COMMAND_DATA_WRITE)!!
        commandNotifyCharacteristic =
            service.findCharacteristic(WSeriesServiceUUID.COMMAND_DATA_NOTIFY)!!

        commandNotifyCharacteristic.getNotifications()
        //Observe button characteristic which detects when a button is pressed
        commandNotifyCharacteristic.getNotifications().onEach {
            Log.d("TAG", "收到了通知1  $it")
            W3Parser.parseW3Packet(it)?.let {
                Log.d("TAG", "解析成功  $it")
            }
//            _state.value = _state.value.copy(isButtonPressed = BlinkyButtonParser.isButtonPressed(it))
        }.launchIn(viewModelScope)

        // 读取信息

        val service1 = services.findService(StandServiceUUID.S_DEVICE_INFO)!!
        Log.d("TAG", "找到了设备信息服务：$service")
        val cha1 = service1.findCharacteristic(StandServiceUUID.C_Serial_Number)!!
        val cha2 = service1.findCharacteristic(StandServiceUUID.C_Manufacturer_Name)!!




        val service2 = services.findService(WSeriesServiceUUID.AUDIO_DATA)!!
        Log.d("TAG", "找到了设备信息服务：$service")
        val cha11 = service2.findCharacteristic(WSeriesServiceUUID.AUDIO_DATA_WRITE)!!
        val cha21 = service2.findCharacteristic(WSeriesServiceUUID.AUDIO_DATA_NOTIFY)!!

        cha21.getNotifications().onEach {
            Log.d("TAG", "收到了通知2 $it")
        }.launchIn(viewModelScope)

//        val isLedOn = BlinkyLedParser.isLedOn(commandNotifyCharacteristic.read())
//        _state.value = _state.value.copy(isLedOn = isLedOn)
    }

    @SuppressLint("NewApi")
    fun turnLed() {
        viewModelScope.launch {
            if (state.value.isLedOn) {
                _state.value = _state.value.copy(isLedOn = false)
                commandWriteCharacteristic.write(DataByteArray.from(0x00))
            } else {
                _state.value = _state.value.copy(isLedOn = true)
                commandWriteCharacteristic.write(DataByteArray.from(0x01))
            }
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            client?.disconnect()
            navigator.navigateUp()
        }
    }

    fun DataByteArray.toHexStr(): String {
        return String(this.value, Charsets.UTF_8)


    }

}
