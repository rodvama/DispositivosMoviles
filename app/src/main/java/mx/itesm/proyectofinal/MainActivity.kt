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
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_patient_list.*
import kotlinx.coroutines.experimental.launch
import mx.itesm.proyectofinal.BLE.BLEConnectionManager
import mx.itesm.proyectofinal.BLE.BLEConstants
import mx.itesm.proyectofinal.BLE.BleDeviceData

/*
 * MainActivity class declares the activity and inflates the view.
 */
class MainActivity : AppCompatActivity() {

    private val TAG: String = "uuidMainActivity"
    private var mSeries: LineGraphSeries<DataPoint?> = LineGraphSeries()

    // TODO Delete this
    var mBluetoothHelper: BluetoothHelper? = PatientList.bluetoothHelper

    private var mDevice: BleDeviceData = BleDeviceData("","")

    private var dataString: String = ""
    private var valid: Boolean = false
    //var dataList: MutableList<Data> = mutableListOf<Data>()
    //var started = false

    companion object {
        const val LIST_ID = "DataList"
    }

    init {

    }

    // Creates the view and initialized the run method for the connected thread.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSeries.color = Color.parseColor("#E84A48")
        graph.addSeries(mSeries)
        graph.title = "Fotopletismografía"
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.toDouble())
        graph.viewport.setMaxX(40.toDouble())


        val extras = intent.extras?:return
        mDevice = extras.getParcelable(PatientList.BLUETOOTH_ADDRESS)!!

        registerServiceReceiver()
        BLEConnectionManager.initBLEService(this@MainActivity)
        connectDevice(mDevice.mDeviceAddress)

//        launchRefreshUiCheck()

        val buttonScan: View = findViewById(R.id.button)
        buttonScan.setOnClickListener { onClick() }
    }

    fun onClick(){
        writeMissedConnection()
    }

    override fun onDestroy() {
        super.onDestroy()
        BLEConnectionManager.disconnect()
        unRegisterServiceReceiver()
    }

    // Handles redirecting and passing information to the ResultsActivity
    fun goToDetail() {
        //mBluetoothHelper?.closeConnection()
        val intent = Intent(this, ResultsActivity::class.java)
        var max = 0
        val actualData = ArrayList<Data>()
        val holder = mBluetoothHelper?.dataList!!
        for (i in 0 until holder.size-1) {
            if (holder[max].mmHg < holder[i].mmHg)
                max = i
        }
        val firstTime = holder[max].timer
        for (i in max until holder.size-1) {
            holder[i].timer -= firstTime
            actualData.add(holder[i])
        }
        if(actualData.size > 20) {
            mBluetoothHelper?.dataList!!.clear()
            mSeries.resetData(arrayOfNulls(0))
            intent.putExtra(LIST_ID, actualData)
            startActivityForResult(intent, 2)
        }
    }

    fun launchRefreshUiCheck() {


//        launch {
//            var dataSize = 0
//            var counter = 0.0
//            while (mBluetoothHelper?.started!!) {
//                val list = mBluetoothHelper?.dataList!!
//                if (dataSize != list.size) {
//                    dataSize = list.size
//                    runOnUiThread {
//                        if (list.size > 0) {
//                            speedMeter.setSpeed(list[dataSize-1].mmHg.toFloat())
//                            mSeries.appendData(DataPoint(counter, list[dataSize-1].pulse), false, 1000)
//                            counter++
//                        }
//                    }
//                    if (list != null && list.size > 0 && list.size > 250 && list.last().mmHg >= 0 && list.last().mmHg <= 25)
//                        mBluetoothHelper?.started = false
//                }
//            }
//            goToDetail()
//        }
    }

    // Handles receiving information from the ResultsActivity and either restarting measurement or
    // redirecting to PatientsList
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            mBluetoothHelper?.started = true
            launchRefreshUiCheck()
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        } else if (requestCode == 2 && resultCode == Activity.RESULT_CANCELED) {
            mBluetoothHelper?.started = true
            launchRefreshUiCheck()
        }
    }

     /**
     * Connect the application with BLE device with selected device address.
     */
    private fun connectDevice(mDeviceAddress : String) {
        Handler().postDelayed({
//            BLEConnectionManager.initBLEService(this@MainActivity)
            if (BLEConnectionManager.connect(mDeviceAddress)) {
                Toast.makeText(this@MainActivity, "DEVICE CONNECTED", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@MainActivity, "DEVICE CONNECTION FAILED", Toast.LENGTH_LONG).show()
            }
        }, 500)
//         100
    }

    /**
     * Register GATT update receiver
     */
    private fun registerServiceReceiver() {
        this.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
    }

    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when {
                BLEConstants.ACTION_GATT_CONNECTED.equals(action) -> {
                    Log.i(TAG, "ACTION_GATT_CONNECTED ")
                    BLEConnectionManager.findBLEGattService(this@MainActivity)
                }
                BLEConstants.ACTION_GATT_DISCONNECTED.equals(action) -> {
                    Log.i(TAG, "ACTION_GATT_DISCONNECTED ")
                }
                BLEConstants.ACTION_GATT_SERVICES_DISCOVERED.equals(action) -> {
                    Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED ")
                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    BLEConnectionManager.findBLEGattService(this@MainActivity)
                }
                BLEConstants.ACTION_DATA_AVAILABLE.equals(action) -> {
                    val data = intent.getStringExtra(BLEConstants.EXTRA_DATA)
                    val uuId = intent.getStringExtra(BLEConstants.EXTRA_UUID)

                    if(data != null ){
                        if(valid){
                            if(data.contains("\n")){
                                var pos = data.indexOf("\n")
                                var add = data.substring(0,pos-1)
                                dataString += add
                                Log.i("NEWDATA", dataString)
                                var values: List<String> = dataString.split(';')
                                if (pos < data.length){
                                    dataString = data.substring(pos+1)
                                }
                            }
                            else{
                                dataString += data
                            }
                        }
                        else{
                            if(data.contains("\n")){
                                var pos = data.indexOf("\n")
                                if (pos < data.length){
                                    dataString += data.substring(pos+1)
                                    Log.i("****NEWDATA_START***", dataString)
                                    valid = true
                                }
                            }
                            else{
                                dataString += data
                            }
                        }
                    }
                }
                BLEConstants.ACTION_DATA_WRITTEN.equals(action) -> {
                    val data = intent.getStringExtra(BLEConstants.EXTRA_DATA)
                    Log.i(TAG, "ACTION_DATA_WRITTEN ")
                }
            }
        }
    }

    /**
     * Intent filter for Handling BLEService broadcast.
     */
    private fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BLEConstants.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BLEConstants.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BLEConstants.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BLEConstants.ACTION_DATA_AVAILABLE)
        intentFilter.addAction(BLEConstants.ACTION_DATA_WRITTEN)

        return intentFilter
    }

    /**
     * Unregister GATT update receiver
     */
    private fun unRegisterServiceReceiver() {
        try {
            this.unregisterReceiver(mGattUpdateReceiver)
        } catch (e: Exception) {
            //May get an exception while user denies the permission and user exists the app
            Log.e(TAG, e.message)
        }

    }

    private fun writeMissedConnection() {
        BLEConnectionManager.writeMissedConnection("A")
    }
}

// Data class. An ArrayList of this type is sent to ResultsActivity
@Parcelize
data class Data(var timer: Double, var mmHg: Double, var pulse: Double) : Parcelable


