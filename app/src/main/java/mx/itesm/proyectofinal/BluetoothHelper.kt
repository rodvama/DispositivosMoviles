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

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.util.*

/*
 * Bluetooth Helper. Handles connecting and disconnecting bluetooth communication to and from
 * external device. Declares sockets and selects device to connect to.
 * @params. activity. The activity that handles the bluetooth communication. Preferably an activity
 * that executes at the start of the application.
 */
class BluetoothHelper(activity: Activity) {
    // The thread that searches and extracts the socket through which communication with the
    // bluetooth device is initiated.
    private var mConnectThread: ConnectThread?

    // Thread that handles receiving information from external device.
    var mConnectedThread: ConnectedThread?

    // The default bluetooth adapter from the device.
    var mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var dataList: MutableList<Data> = mutableListOf()
    var started = false
    //var counter = 0.0

    /*
     * Initializes the connection threads and checks that bluetooth is enabled in the device.
     * Proceeds to call the bluetooth enabling intent or start the connection.
     */
    init {
        mConnectThread = null
        mConnectedThread = null

        if (!mBluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, 1)
        } else {
            startConnection()
        }
    }

    /*
     * Returns if the bluetooth device is connected and ready to communicate
     */
    fun isDeviceConnected(): Boolean {
        return mBluetoothAdapter.bondedDevices.size > 0
    }

    /*
     * Returns if the bluetooth adapter is enabled
     */
    fun isEnabled(): Boolean {
        return mBluetoothAdapter.isEnabled
    }

    /*
     * Handles starting the connection to the external device depending on the paired devices with
     * the smart phone and runs both connect and connected threads.
     */
    fun startConnection() {
        dataList.clear()
        var mDevice: BluetoothDevice? = null
        val pairedDevices = mBluetoothAdapter.bondedDevices
        if (pairedDevices.size > 0) {
            for (device in pairedDevices) {
                if (device.address == "20:15:05:27:15:42")
                    mDevice = device
            }
        }

        mConnectThread = ConnectThread(mDevice!!)
        mConnectThread!!.start()

        mConnectedThread = ConnectedThread(mConnectThread!!.mmSocket!!)
        mConnectedThread!!.start()
    }

    /*
     * Closes both connect and connected threads.
     */
    fun closeConnection() {
        mConnectThread?.cancel()
        mConnectedThread?.cancel()
    }

    /*
     * Declaration of the Connect Thread. Inherits behaviour from the Thread class.
     */
    inner class ConnectThread(device: BluetoothDevice) : Thread() {
        var mmSocket: BluetoothSocket?
        var mmDevice: BluetoothDevice?
        private val GENERIC_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")!!

        /*
         * Initializes the bluetooth socket and starts communication
         */
        init {
            var tmp: BluetoothSocket? = null
            mmDevice = device
            try {
                tmp = device.createRfcommSocketToServiceRecord(GENERIC_UUID)
            } catch (e: IOException) {

            }
            mmSocket = tmp!!
            mBluetoothAdapter.cancelDiscovery()
            try {
                mmSocket?.connect()
            } catch( e: IOException) {

            }
        }

        /*
         * Code that is ran when the thread is started. Cancels discovery and starts connection
         * with socket.
         */
//        override fun run() {
//            super.run()
//            mBluetoothAdapter.cancelDiscovery()
//            try {
//                mmSocket?.connect()
//            } catch (connectException: IOException) {
//                try {
//                    mmSocket?.close()
//                } catch (closeException: IOException) {
//                }
//
//                return
//            }
//
//
//        }

        /*
         * Cancels connection to socket
         */
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
            }

        }
    }
     /*
      * Declaration of the Connected Thread. Inherits behaviour from the Thread class.
      */
    inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        val mmInStream: InputStream?

         /*
         * Initializes the bluetooth input stream and starts communication
         */
        init {
            var tmpIn: InputStream? = null
            try {
                tmpIn = mmSocket.inputStream
            } catch (e: IOException) {
            }

            mmInStream = tmpIn
        }

        override fun run() {
            super.run()
            //if (mmInStream != null) {
            //val reader = mmInStream!!.bufferedReader()
            while(!started && mmInStream != null) {
                var data = mmInStream!!.bufferedReader().readLine()
                while (started) {
                    data = mmInStream.bufferedReader().readLine()
                    try {
                        if (!data.isNullOrEmpty() && data?.filter { s -> s == ';' }?.count() == 2) {
                            val pressure = data.substringBeforeLast(';').substringAfterLast(';')
                            val pulse = data.substringAfterLast(';')
                            if (pressure.toFloat() > 20f) {
//                                if (!started) {
//                                    started = true
//                                }

                                dataList.add(Data(System.currentTimeMillis().toDouble(), pressure.toDouble(), pulse.toDouble()))
                                //counter++
//                            runOnUiThread {
//                                speedMeter.setSpeed(pressure.toFloat())
//                                mSeries.appendData(DataPoint(counter, pulse.toDouble()), false, 1000)
//                                counter++
//                            }
                            }
                        }
                    } catch (e: IOException) {
                        break
                    }
                }
            }
            //}
        }

         /*
          * Closes connection to socket
          */
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
            }

        }
    }
}