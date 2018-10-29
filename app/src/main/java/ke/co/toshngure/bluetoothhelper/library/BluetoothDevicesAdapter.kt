package ke.co.toshngure.bluetoothhelper.library

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ke.co.toshngure.bluetoothhelper.R
import ke.co.toshngure.bluetoothhelper.Spanny
import ke.co.toshngure.bluetoothhelper.library.model.WrappedDevice

class BluetoothDevicesAdapter : RecyclerView.Adapter<BluetoothDevicesAdapter.ViewHolder>() {

    private val wrappedDevices = ArrayList<WrappedDevice>()
    private lateinit var onDeviceClickedListener: OnDeviceClickedListener

    interface OnDeviceClickedListener {
        fun onBluetoothDeviceClicked(wrappedDevice: WrappedDevice)
    }

    fun setOnDeviceClickedListener(onDeviceClickedListener: OnDeviceClickedListener) {
        this.onDeviceClickedListener = onDeviceClickedListener
    }

    fun setDevices(newWrappedDevices: ArrayList<WrappedDevice>) {
        val wrappedDevicesDiffUtil = WrappedDevicesDiffUtil(this.wrappedDevices, newWrappedDevices)
        val diffUtilResult = DiffUtil.calculateDiff(wrappedDevicesDiffUtil)
        this.wrappedDevices.clear()
        this.wrappedDevices.addAll(newWrappedDevices)
        diffUtilResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = wrappedDevices[position]
        holder.bind(device)
        holder.itemView.setOnClickListener {
            onDeviceClickedListener.onBluetoothDeviceClicked(device)
        }
    }

    override fun getItemCount(): Int {
        return this.wrappedDevices.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val detailsTV: TextView = itemView.findViewById(R.id.detailsTV)
        private val statusTV: TextView = itemView.findViewById(R.id.statusTV)

        @SuppressLint("SetTextI18n")
        fun bind(wrappedDevice: WrappedDevice) {
            detailsTV.text = wrappedDevice.resolveDetails()
            if (wrappedDevice.paired && wrappedDevice.connected){
                statusTV.text = "Connected"
            } else if (wrappedDevice.paired && !wrappedDevice.connected) {
                statusTV.text = "Paired, tap to connect"
            } else if (!wrappedDevice.paired) {
                statusTV.text = "Unpaired, tap to pair"
            } else {
                statusTV.text = ""
            }
        }
    }

    inner class WrappedDevicesDiffUtil(
        private val oldDevices: ArrayList<WrappedDevice>,
        private val newDevices: ArrayList<WrappedDevice>
    ) : DiffUtil.Callback() {


        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldDevices[oldItemPosition].device.address == newDevices[newItemPosition].device.address
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldDevice = oldDevices[oldItemPosition]
            val newDevice = newDevices[newItemPosition]
            return (oldDevice.connected && newDevice.connected) && (oldDevice.paired && newDevice.paired)
        }

        override fun getOldListSize(): Int = oldDevices.size

        override fun getNewListSize(): Int = newDevices.size

    }
}
