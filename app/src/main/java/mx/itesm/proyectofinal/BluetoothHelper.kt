/*
    Copyright (C) 2018 - ITESM

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mx.itesm.proyectofinal

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast


/*
 * Bluetooth Helper. Handles connecting and disconnecting bluetooth communication to and from
 * external device. Declares sockets and selects device to connect to.
 * @params. activity. The activity that handles the bluetooth communication. Preferably an activity
 * that executes at the start of the application.
 */
class BluetoothHelper(activity: Activity) {

    val context = activity.applicationContext

    // The thread that searches and extracts the socket through which communication with the
    // bluetooth device is initiated.
//    private var mConnectThread: ConnectThread?

    // Thread that handles receiving information from external device.
//    var mConnectedThread: ConnectedThread?

    var mBluetoothAdapter: BluetoothAdapter? = null
    var mBluetoothDevice: BluetoothDevice? = null
    var dataList: MutableList<Data> = mutableListOf()
    var started = false

    companion object {
        const val REQUEST_ENABLE_BT: Int = 10
        const val REQUEST_COARSE_LOCATION_PERMISSION: Int = 11
    }

    /**
     * Initializes the connection threads and checks that bluetooth is enabled in the device.
     * Proceeds to call the bluetooth enabling intent or start the connection.
     */
    init {
//        mConnectThread = null
//        mConnectedThread = null

        checkBluetoothPermission(activity)

        checkLocationPermission(activity)
    }

    /**
     * Check the Bluetooth Permission before scaning with BLE API's
     */
    private fun checkBluetoothPermission(activity: Activity) {
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!activity.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "Dispositivo no soporta LE", Toast.LENGTH_SHORT).show()
//            finish()
        }

//        mBluetoothAdapterLE = lazy(LazyThreadSafetyMode.NONE) {
//            val bluetoothManager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//            bluetoothManager.adapter
//        }

        val mBluetoothManager: BluetoothManager =
                activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "BT no soport", Toast.LENGTH_SHORT).show()
//            finish()
//            return
        }

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    /**
     * Check the Location Permission before calling the BLE API's
     */
    private fun checkLocationPermission(activity: Activity) {
        if(!isLocationPermissionEnabled(activity)){
            when {
                ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) -> displayRationale(activity)
                else -> requestLocationPermission(activity)
            }
        }
    }

    /**
     * If the user decline the Permission request and tick the never ask again message
     * Then the application can't proceed further steps
     * In such situation- App need to prompt the user to do the change form Settings Manually
     */
    private fun displayRationale(activity: Activity) {
        AlertDialog.Builder(activity)
                .setTitle(R.string.location_permission_not_granted)
                .setMessage(R.string.location_permission_disabled)
                .setPositiveButton(R.string.ok
                ) { _, _ -> requestLocationPermission(activity) }
                .setNegativeButton(R.string.cancel
                ) { _, _ -> }
                .show()
    }

    /**
     * Request Location API
     * If the request go to Android system and the System will throw a dialog message
     * user can accept or decline the permission from there
     */
    private fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_COARSE_LOCATION_PERMISSION)
    }

    /**
     * Check with the system- If the permission already enabled or not
     */
    private fun isLocationPermissionEnabled(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Returns if the bluetooth adapter is enabled
     */
    fun isEnabled(): Boolean {
        return mBluetoothAdapter?.isEnabled ?: false
    }

    /**
     * Returns if the bluetooth device is connected and ready to communicate
     */
    fun isDeviceConnected(): Boolean {
        // TODO mBluetoothDevice va a decidir si esta conectado o no, debe estar asginado a uno
        return false
//        return mBluetoothAdapter.bondedDevices.size > 0
    }

    /**
     * Handles starting the connection to the external device depending on the paired devices with
     * the smart phone and runs both connect and connected threads.
     */
    fun startConnection() {
        dataList.clear()
//        var mDevice: BluetoothDevice? = null
//        val pairedDevices = mBluetoothAdapter.bondedDevices
//        if (pairedDevices.size > 0) {
//            for (device in pairedDevices) {
//                if (device.address == "20:15:05:27:15:42")
//                    mDevice = device
//            }
//        }

//        mConnectThread = ConnectThread(mDevice!!)
//        mConnectThread!!.start()
//
//        mConnectedThread = ConnectedThread(mConnectThread!!.mmSocket!!)
//        mConnectedThread!!.start()
    }
}

//
//    /*
//     * Returns if the bluetooth adapter is enabled
//     */
//    fun isEnabled(): Boolean {
//        return mBluetoothAdapter.isEnabled
//    }
//
//    /*
//     * Closes both connect and connected threads.
//     */
//    fun closeConnection() {
//        mConnectThread?.cancel()
//        mConnectedThread?.cancel()
//    }
//
//    /*
//     * Declaration of the Connect Thread. Inherits behaviour from the Thread class.
//     */
//    inner class ConnectThread(device: BluetoothDevice) : Thread() {
//        var mmSocket: BluetoothSocket?
//        var mmDevice: BluetoothDevice?
//        private val GENERIC_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")!!
//
//        /*
//         * Initializes the bluetooth socket and starts communication
//         */
//        init {
//            var tmp: BluetoothSocket? = null
//            mmDevice = device
//            try {
//                tmp = device.createRfcommSocketToServiceRecord(GENERIC_UUID)
//            } catch (e: IOException) {
//
//            }
//            mmSocket = tmp!!
//            mBluetoothAdapter.cancelDiscovery()
//            try {
//                mmSocket?.connect()
//            } catch( e: IOException) {
//
//            }
//        }
//
//        /*
//         * Code that is ran when the thread is started. Cancels discovery and starts connection
//         * with socket.
//         */
////        override fun run() {
////            super.run()
////            mBluetoothAdapter.cancelDiscovery()
////            try {
////                mmSocket?.connect()
////            } catch (connectException: IOException) {
////                try {
////                    mmSocket?.close()
////                } catch (closeException: IOException) {
////                }
////
////                return
////            }
////
////
////        }
//
//        /*
//         * Cancels connection to socket
//         */
//        fun cancel() {
//            try {
//                mmSocket?.close()
//            } catch (e: IOException) {
//            }
//
//        }
//    }
//     /*
//      * Declaration of the Connected Thread. Inherits behaviour from the Thread class.
//      */
//    inner class ConnectedThread(private val mmSocket: BluetoothSocket?) : Thread() {
//        val mmInStream: InputStream?
//
//         /*
//         * Initializes the bluetooth input stream and starts communication
//         */
//        init {
//            var tmpIn: InputStream? = null
//            try {
//                tmpIn = mmSocket?.inputStream
//            } catch (e: IOException) {
//            }
//
//            mmInStream = tmpIn
//        }
//
//         /*
//         * Code that is ran when the thread is started. Cancels discovery and starts connection
//         * with socket.
//         */
//        override fun run() {
//            super.run()
//            //if (mmInStream != null) {
//            //val reader = mmInStream!!.bufferedReader()
//            try {
//                while (!started && mmInStream != null) {
//                    var data = mmInStream.bufferedReader().readLine()
//                    while (started) {
//                        data = mmInStream.bufferedReader().readLine()
//                        try {
//                            if (!data.isNullOrEmpty() && data?.filter { s -> s == ';' }?.count() == 2) {
//                                val pressure = data.substringBeforeLast(';').substringAfterLast(';')
//                                val pulse = data.substringAfterLast(';')
//                                if (pressure.toFloat() > 20f) {
////                                if (!started) {
////                                    started = true
////                                }
//
//                                    dataList.add(Data(System.currentTimeMillis().toDouble(), pressure.toDouble(), pulse.toDouble()))
//                                    //counter++
////                            runOnUiThread {
////                                speedMeter.setSpeed(pressure.toFloat())
////                                mSeries.appendData(DataPoint(counter, pulse.toDouble()), false, 1000)
////                                counter++
////                            }
//                                }
//                            }
//                        } catch (e: IOException) {
//                            break
//                        }
//                    }
//                }
//            } catch (e: IOException) {
//                Looper.prepare()
//                Toast.makeText(context, "No se pudo crear comunicación con el dispositivo externo", Toast.LENGTH_LONG).show()
//                Looper.loop()
//                Looper.myLooper()?.quit()
//            }
//            //}
//        }
//
//         /*
//          * Closes connection to socket
//          */
//        fun cancel() {
//            try {
//                mmSocket?.close()
//            } catch (e: IOException) {
//            }
//
//        }
//    }
//
////    private val mLeScanCallback = object : ScanCallback() {
////
////
////        override fun onScanResult(callbackType: Int, result: ScanResult) {
////            //super.onScanResult(callbackType, result);
////            Log.i("BLE", result.getDevice().getName())
////
////        }
////
////        override fun onScanFailed(errorCode: Int) {
////            Log.i("BLE", "error")
////        }
////    }
//
//    // Device scan callback.
//    private val mScanCallbackLE = object : ScanCallback() {
//        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            Log.i("Device Name: ", result.device.name)
//        }
//    }
//
////    fun scanLeDevices() {
////        Log.i("Scanning", "start")
//////        mBluetoothScannerLE!!.startScan(mScanCallbackLE)
//////        AsyncTask.execute { mBluetoothScannerLE!!.startScan(mScanCallbackLE) }
////
////    }
