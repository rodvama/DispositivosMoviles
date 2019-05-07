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
import de.nitri.gauge.Gauge
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_patient_list.*
import kotlinx.coroutines.experimental.launch
import mx.itesm.proyectofinal.BLE.BLEConnectionManager
import mx.itesm.proyectofinal.BLE.BLEConstants
import mx.itesm.proyectofinal.BLE.BleDeviceData
import org.jetbrains.anko.doAsync
import java.util.*

/*
 * MainActivity class declares the activity and inflates the view.
 */
class MainActivity : AppCompatActivity() {

    private val TAG: String = "uuidMainActivity"
    private var mSeries: LineGraphSeries<DataPoint?> = LineGraphSeries()

    // TODO Delete this
    var mBluetoothHelper: BluetoothHelper? = PatientList.bluetoothHelper

    private var mDevice: BleDeviceData = BleDeviceData("","")

    var dataList: MutableList<Data> = mutableListOf()
    //var started = false
    var counter: Double = 0.0

    private var gauge: Gauge? = null
    private var queue: Queue<List<String>> = LinkedList()

    companion object {
        const val LIST_ID = "DataList"
    }

    init {

    }

    // Creates the view and initialized the run method for the connected thread.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val extras = intent.extras?:return
        mDevice = extras.getParcelable(PatientList.BLUETOOTH_ADDRESS)!!

        registerServiceReceiver()
        BLEConnectionManager.initBLEService(this@MainActivity)
        connectDevice(mDevice.mDeviceAddress)

//        launchRefreshUiCheck()

        val buttonScan: View = findViewById(R.id.button)
        buttonScan.setOnClickListener { onClick() }
        gauge = findViewById(R.id.gauge)

    }

    fun onClick(){
        unRegisterServiceReceiver()
        BLEConnectionManager.disconnect()
        goToDetail()
//        writeMissedConnection()
    }

    override fun onDestroy() {
        unRegisterServiceReceiver()
        BLEConnectionManager.disconnect()
        super.onDestroy()
    }

    // Handles redirecting and passing information to the ResultsActivity
    fun goToDetail() {
        val intent = Intent(this, ResultsActivity::class.java)
        var max = 0
        val actualData = ArrayList<Data>()
        var holder = dataList
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
//            mBluetoothHelper?.dataList!!.clear()
            mSeries.resetData(arrayOfNulls(0))
            intent.putExtra(LIST_ID, actualData)
            startActivityForResult(intent, 2)
        }
//        //mBluetoothHelper?.closeConnection()
//        val intent = Intent(this, ResultsActivity::class.java)
//        var max = 0
//        val actualData = ArrayList<Data>()
//        val holder = mBluetoothHelper?.dataList!!
//        for (i in 0 until holder.size-1) {
//            if (holder[max].mmHg < holder[i].mmHg)
//                max = i
//        }
//        val firstTime = holder[max].timer
//        for (i in max until holder.size-1) {
//            holder[i].timer -= firstTime
//            actualData.add(holder[i])
//        }
//        if(actualData.size > 20) {
//            mBluetoothHelper?.dataList!!.clear()
//            mSeries.resetData(arrayOfNulls(0))
//            intent.putExtra(LIST_ID, actualData)
//            startActivityForResult(intent, 2)
//        }
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
        private var started: Boolean = false
        private var stringData: String = ""
        private var time: Double = 0.0

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
                    /**
                     * It's a notification from the external BLE device.
                     * From an intent we get the data and UUID of the service that send it.
                     */
                    val data = intent.getStringExtra(BLEConstants.EXTRA_DATA)
                    val uuId = intent.getStringExtra(BLEConstants.EXTRA_UUID)

                    if(data != null ){
                        val endDataSet = data.indexOf("\r\n") // Find for end of dataset
                        // If it already found an end of a dataset to begin recording datasets
                        if(started){
//                            mSeries.appendData(DataPoint(counter, values[2].toDouble()), false, 1000)
                            time += 6
                            if(endDataSet > 1){
                                stringData += data.substring(0,endDataSet) // Add data before "\r\n". Case: ";200\r\n"
                                // Split values and convert to Float

                                val values: List<String> = stringData.split(';')
                                Log.d("DATA", values.toString())

                                val space =  values[1].indexOf("\n")
//                                    val space =  values[2].indexOf("\n")
                                if(space > 1){
                                    val pop = values[1].substring(0,space-1)
//                                    values[0].toDouble()
                                    dataList.add(Data(time, pop.toDouble(), 0.0))
                                    if(pop.toFloat() < 20){
                                        gauge?.moveToValue(20F)
                                    }
                                    else{
                                        gauge?.moveToValue(pop.toFloat())
                                    }
//                                        val pop = values[2].substring(0,space-1)
//                                        gauge?.moveToValue(pop.toFloat())
//                                        dataString = values[2].substring(space)
                                }
                                else{
//                                    values[0].toDouble()
                                    dataList.add(Data(time, values[1].toDouble(), 0.0))
                                    if(values[1].toDouble() < 20){
                                        gauge?.moveToValue(20F)
                                    }
                                    else{
                                        gauge?.moveToValue(values[1].toFloat())
                                    }
                                }
                            }
                            stringData = data.substring(endDataSet+2) // Add remaining values. Case: "44\r\n18905;2" Case:"\r\n132455"
                            // With this ^^^^. If "\n" is at the end, dataString ends up being ""
                        }
                        // Haven't found the end of a dataset, keep looking for it
                        else{
                            if(endDataSet != -1){
                                val endChar = data.indexOf("\n") // Find for end of dataset
                                stringData += data.substring(endChar+1)
                                Log.d("****NEWDATA_START***", stringData)
                                started = true
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

    override fun onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}

// Data class. An ArrayList of this type is sent to ResultsActivity
@Parcelize
data class Data(var timer: Double, var mmHg: Double, var pulse: Double) : Parcelable


