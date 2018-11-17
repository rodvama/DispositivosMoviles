package mx.itesm.proyectofinal

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.*
import android.content.Intent
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.CountDownTimer
import android.os.Handler
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {
    private var mSeries: LineGraphSeries<DataPoint?> = LineGraphSeries()
    //var mBluetoothAdapter: BluetoothAdapter
    var mBluetoothHelper: BluetoothHelper? = null

    //private var timer: CountDownTimer = CountDownTimer(0f, 25f)


    init {
        //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBluetoothHelper = BluetoothHelper(this)
        launch {
            mBluetoothHelper?.mConnectedThread.run {
                var counter = 0.0
                while (true) {
                    try {
                        val data = this?.mmInStream?.bufferedReader()?.readLine()
                        if (!data.isNullOrEmpty() && data?.filter { s -> s == ';' }?.count() == 2) {
                            val pressure = data.substringBeforeLast(';').substringAfterLast(';')
                            val pulse = data.substringAfterLast(';')
                            if (pressure.toFloat() > 20f)
                                runOnUiThread {
                                    speedMeter.setSpeed(pressure.toFloat())
                                    mSeries.appendData(DataPoint(counter, pulse.toDouble()), true, 1000)
                                    counter++
                                }
                        }
                    } catch (e: IOException) {
                        break
                    }
                }
            }
        }

//        if (!mBluetoothAdapter.isEnabled) {
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(enableBtIntent, 1)
//        }
//
//        var mDevice: BluetoothDevice? = null
//        val pairedDevices = mBluetoothAdapter.bondedDevices
//        if (pairedDevices.size > 0) {
//            for (device in pairedDevices) {
//                mDevice = device
//            }
//        }

        mSeries.color = Color.parseColor("#E84A48")
        graph.addSeries(mSeries)
        graph.title = "BPM"
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.toDouble())
        graph.viewport.setMaxX(40.toDouble())

        measure_btn.setOnClickListener{ onClick() }

//        var mConnectThread = ConnectThread(mDevice!!)
//        mConnectThread.start()
//        var mConnectedThread = ConnectedThread(mConnectThread.mmSocket!!)
//        mConnectedThread.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            mBluetoothHelper?.startConnection()
            launch {
                mBluetoothHelper?.mConnectedThread.run {
                    var counter = 0.0
                    while (true) {
                        try {
                            val data = this?.mmInStream?.bufferedReader()?.readLine()
                            if (!data.isNullOrEmpty() && data?.filter { s -> s == ';' }?.count() == 2) {
                                val pressure = data.substringBeforeLast(';').substringAfterLast(';')
                                val pulse = data.substringAfterLast(';')
                                if (pressure.toFloat() > 20f)
                                    runOnUiThread {
                                        speedMeter.setSpeed(pressure.toFloat())
                                        mSeries.appendData(DataPoint(counter, pulse.toDouble()), true, 1000)
                                        counter++
                                    }
                            }
                        } catch (e: IOException) {
                            break
                        }
                    }
                }
            }
        }
    }

    fun onClick() {
    }

//    inner class ConnectThread(device: BluetoothDevice) : Thread() {
//        var mmSocket: BluetoothSocket?
//        var mmDevice: BluetoothDevice?
//        val GENERIC_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
//
//        init {
//            var tmp: BluetoothSocket? = null
//            mmDevice = device
//            try {
//                tmp = device.createRfcommSocketToServiceRecord(GENERIC_UUID)
//            } catch (e: IOException) { }
//            mmSocket = tmp!!
//        }
//
//        override fun run() {
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
//        }
//
//        fun cancel() {
//            try {
//                mmSocket?.close()
//            } catch (e: IOException) {
//            }
//
//        }
//    }
//
//    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
//        private val mmInStream: InputStream?
//        private val mmOutStream: OutputStream?
//
//        init {
//            var tmpIn: InputStream? = null
//            var tmpOut: OutputStream? = null
//            try {
//                tmpIn = mmSocket.inputStream
//                tmpOut = mmSocket.outputStream
//            } catch (e: IOException) {
//            }
//
//            mmInStream = tmpIn
//            mmOutStream = tmpOut
//        }
//
//        override fun run() {
//            var data: String
//            var counter = 0.0
//            while (true) {
//                try {
//                    data = mmInStream!!.bufferedReader().readLine()
//                    if (data.filter { s -> s == ';' }.count() == 2) {
//                        var pressure = data.substringBeforeLast(';').substringAfterLast(';')
//                        var pulse = data.substringAfterLast(';')
//                        if (pressure.toFloat() > 20f)
//                            runOnUiThread {
//                                speedMeter.setSpeed(pressure.toFloat())
//                                mSeries.appendData(DataPoint(counter, pulse.toDouble()), true, 1000)
//                                counter++
//                            }
//                    }
//                } catch (e: IOException) {
//                    break
//                }
//            }
//        }
//
//        fun cancel() {
//            try {
//                mmSocket.close()
//            } catch (e: IOException) {
//            }
//
//        }
//    }
}


