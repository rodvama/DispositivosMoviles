package mx.itesm.proyectofinal

import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_device_scan.*

class DeviceScanActivity : AppCompatActivity() {

    private lateinit var mBluetoothHelper: BluetoothHelper
    private lateinit var mBluetoothScanner: BluetoothLeScanner
    private lateinit var adapter: DeviceListAdapter

    private var mScanning: Boolean = false
    private var mHandler: Handler? = null

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_scan)

        mBluetoothHelper = PatientList.bluetoothHelper!!
        mHandler = Handler()
        mBluetoothScanner = mBluetoothHelper.mBluetoothAdapter!!.bluetoothLeScanner

        // Create Adapter
        adapter = DeviceListAdapter(this)
        adapter.notifyDataSetChanged() // Prepares list to notify if it suffers any change
        list_devices.adapter = adapter

        val buttonScan: View = findViewById(R.id.button_scan)
        buttonScan.setOnClickListener { scanLeDevice(true) }

    }

    private fun scanLeDevice(enable: Boolean) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler?.postDelayed(Runnable {
                mScanning = false
//                AsyncTask.execute { mBluetoothScanner?.startScan(mLeScanCallback) }
                mBluetoothScanner.stopScan(mLeScanCallback)
//                invalidateOptionsMenu()
            }, SCAN_PERIOD)

            mScanning = true
            mBluetoothScanner.startScan(mLeScanCallback)
        } else {
            mScanning = false
            mBluetoothScanner.stopScan(mLeScanCallback)
        }
//        invalidateOptionsMenu()
    }

    // Device scan callback.
    private val mLeScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.i("Device", "${result.device.name}    add: ${result.device.address}")
            adapter.addDevice(result.device)
            adapter.notifyDataSetChanged()
//            // auto scroll for text view
//            val scrollAmount = peripheralTextView.getLayout().getLineTop(peripheralTextView.getLineCount()) - peripheralTextView.getHeight()
//            // if there is no need to scroll, scrollAmount will be <=0
//            if (scrollAmount > 0)
//                peripheralTextView.scrollTo(0, scrollAmount)
        }
    }

//    private val mLeScanCallback = object : ScanCallback() {
//        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            super.onScanResult(callbackType, result)
//        }
//
//        override fun onBatchScanResults(results: List<ScanResult>) {
//            super.onBatchScanResults(results)
//        }
//
//        override fun onScanFailed(errorCode: Int) {
//            super.onScanFailed(errorCode)
//        }
//    }
}
