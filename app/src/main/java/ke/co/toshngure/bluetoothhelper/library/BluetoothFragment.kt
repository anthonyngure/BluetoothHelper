package ke.co.toshngure.bluetoothhelper.library


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ke.co.toshngure.bluetoothhelper.R
import ke.co.toshngure.bluetoothhelper.library.model.WrappedDevice
import kotlinx.android.synthetic.main.fragment_bluetooth.*
import me.aflak.bluetooth.Bluetooth
import me.aflak.bluetooth.BluetoothCallback
import me.aflak.bluetooth.DeviceCallback
import me.aflak.bluetooth.DiscoveryCallback

/**
 * A simple [Fragment] subclass.
 *
 */
@SuppressLint("SetTextI18n")
open class BluetoothFragment : Fragment(), BluetoothCallback, DiscoveryCallback, DeviceCallback,
    BluetoothDevicesAdapter.OnDeviceClickedListener {

    private lateinit var mBluetooth: Bluetooth
    private lateinit var mAdapter: BluetoothDevicesAdapter
    private val mWrappedDevices = ArrayList<WrappedDevice>()


    open fun onBluetoothReady(){

    }

    override fun onBluetoothDeviceClicked(wrappedDevice: WrappedDevice) {
        Log.d(TAG, wrappedDevice.toString())
        if (wrappedDevice.paired) {
            mBluetooth.pair(wrappedDevice.device)
        } else {
            mBluetooth.connectToDevice(wrappedDevice.device)
        }
    }

    private fun isPaired(device: BluetoothDevice): Boolean {
        val pairedDevices = mBluetooth.pairedDevices
        for (d in pairedDevices) {
            if (device.address == d.address) {
                return true
            }
        }
        return false
    }

    //region DiscoveryCallback
    override fun onDevicePaired(device: BluetoothDevice?) {
        device?.let {
            val wrappedDevice = WrappedDevice(it, false, isPaired(it))
            mWrappedDevices.add(wrappedDevice)
            mAdapter.setDevices(mWrappedDevices)
        }
    }


    @SuppressLint("RestrictedApi")
    override fun onDiscoveryStarted() {
        loadingLL.visibility = View.VISIBLE
        fab.visibility = View.GONE
        messageTV.text = "Discovering bluetooth devices"
    }

    override fun onDeviceUnpaired(device: BluetoothDevice?) {
        device?.let {
            val wrappedDevice = WrappedDevice(it, false, isPaired(it))
            mWrappedDevices.add(wrappedDevice)
            mAdapter.setDevices(mWrappedDevices)
        }
    }


    @SuppressLint("RestrictedApi")
    override fun onDiscoveryFinished() {
        loadingLL.visibility = View.GONE
        fab.visibility = View.VISIBLE
        messageTV.text = ""
    }

    override fun onDeviceFound(device: BluetoothDevice?) {
        device?.let {
            val wrappedDevice = WrappedDevice(it, false, isPaired(it))
            mWrappedDevices.add(wrappedDevice)
            mAdapter.setDevices(mWrappedDevices)
        }
    }
    //endregion

    //region DeviceCallback
    override fun onDeviceDisconnected(device: BluetoothDevice?, message: String?) {
        device?.let {
            val wrappedDevice = WrappedDevice(it, false, isPaired(it))
            mWrappedDevices.add(wrappedDevice)
            mAdapter.setDevices(mWrappedDevices)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onDeviceConnected(device: BluetoothDevice?) {
        loadingLL.visibility = View.GONE
        fab.visibility = View.VISIBLE
        device?.let {
            mWrappedDevices.clear()
            val wrappedDevice = WrappedDevice(it, true, isPaired(it))
            mWrappedDevices.add(wrappedDevice)
            mAdapter.setDevices(mWrappedDevices)
            onBluetoothReady()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onConnectError(device: BluetoothDevice?, message: String?) {
        loadingLL.visibility = View.GONE
        fab.visibility = View.VISIBLE
        Snackbar.make(devicesRV, "Unable to connect", Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                device?.let { d ->
                    mBluetooth.connectToDevice(d)
                }
            }
    }

    override fun onMessage(message: String?) {
    }
    //endregion

    //region BluetoothCallback
    @SuppressLint("RestrictedApi")
    override fun onError(message: String?) {
        loadingLL.visibility = View.GONE
        fab.visibility = View.VISIBLE
        messageTV.text = message
    }

    @SuppressLint("RestrictedApi")
    override fun onUserDeniedActivation() {
        loadingLL.visibility = View.VISIBLE
        fab.visibility = View.GONE
        messageTV.text = "You must allow to turn bluetooth on..."
    }

    @SuppressLint("RestrictedApi")
    override fun onBluetoothOff() {
        loadingLL.visibility = View.GONE
        fab.visibility = View.GONE
        messageTV.text = ""
    }

    @SuppressLint("RestrictedApi")
    override fun onBluetoothOn() {
        loadingLL.visibility = View.GONE
        fab.visibility = View.VISIBLE
        messageTV.text = ""
        addPairedDevices()
    }

    private fun addPairedDevices() {
        val pairedDevices = mBluetooth.pairedDevices
        for (device in pairedDevices) {
            val wrappedDevice = WrappedDevice(device, false, true)
            mWrappedDevices.add(wrappedDevice)
            mAdapter.setDevices(mWrappedDevices)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onBluetoothTurningOn() {
        loadingLL.visibility = View.VISIBLE
        fab.visibility = View.GONE
        messageTV.text = "Turning bluetooth on..."
    }

    @SuppressLint("RestrictedApi")
    override fun onBluetoothTurningOff() {
        loadingLL.visibility = View.VISIBLE
        fab.visibility = View.GONE
        messageTV.text = "Turning bluetooth of..."
    }
    //endregion


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBluetooth = Bluetooth(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bluetooth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener {
            mWrappedDevices.clear()
            mBluetooth.startScanning()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBluetooth.setBluetoothCallback(this)
        mBluetooth.setDeviceCallback(this)
        mBluetooth.setDiscoveryCallback(this)
        mBluetooth.setCallbackOnUI(activity)


        mAdapter = BluetoothDevicesAdapter()
        mAdapter.setOnDeviceClickedListener(this)
        devicesRV.adapter = mAdapter
        devicesRV.layoutManager = LinearLayoutManager(activity)
        devicesRV.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    @SuppressLint("RestrictedApi")
    override fun onStart() {
        super.onStart()
        mBluetooth.onStart()
        mBluetooth.enable()

        if (mBluetooth.isEnabled){
            addPairedDevices()
            fab.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        mBluetooth.onStop()
        try {
            mBluetooth.disable()
            mBluetooth.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "BluetoothFragment"
    }


}
