package ke.co.toshngure.bluetoothhelper

import android.widget.Toast
import ke.co.toshngure.bluetoothhelper.library.BluetoothFragment

class SampleFragment : BluetoothFragment() {


    override fun onBluetoothReady() {
        super.onBluetoothReady()
        Toast.makeText(activity, "onBluetoothReady", Toast.LENGTH_SHORT).show()
    }

}