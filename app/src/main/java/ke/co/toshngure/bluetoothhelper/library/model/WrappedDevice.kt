package ke.co.toshngure.bluetoothhelper.library.model

import android.bluetooth.BluetoothDevice
import android.text.Spannable
import android.text.TextUtils
import ke.co.toshngure.bluetoothhelper.Spanny

data class WrappedDevice(val device: BluetoothDevice, val connected: Boolean, val paired: Boolean) {

    fun resolveDetails(): String {
        return device.name?.let { "${device.name} - ${device.address}" } ?: device.address
    }
}
