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
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import android.content.Intent
import android.os.Parcelable
import android.widget.Toast
import kotlinx.android.parcel.Parcelize
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var mSeries: LineGraphSeries<DataPoint?> = LineGraphSeries()
    var mBluetoothHelper: BluetoothHelper? = PatientList.bluetoothHelper
    //var dataList: MutableList<Data> = mutableListOf<Data>()
    //var started = false

    companion object {
        const val LIST_ID = "DataList"
    }

    init {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        launch {
//            mBluetoothHelper?.mConnectedThread.run {
//                var counter = 0.0
//                while (true) {
//                    try {
//                        val data = this?.mmInStream?.bufferedReader()?.readLine()
//                        if (!data.isNullOrEmpty() && data?.filter { s -> s == ';' }?.count() == 2) {
//                            val pressure = data.substringBeforeLast(';').substringAfterLast(';')
//                            val pulse = data.substringAfterLast(';')
//                            if (pressure.toFloat() > 20f) {
//                                if (!started) {
//                                    started = true
//                                }
//                                dataList.add(Data(System.currentTimeMillis().toDouble(), pressure.toDouble(), pulse.toDouble()))
//                                runOnUiThread {
//                                    speedMeter.setSpeed(pressure.toFloat())
//                                    mSeries.appendData(DataPoint(counter, pulse.toDouble()), false, 1000)
//                                    counter++
//                                }
//                            } else if (started) {
//                                goToDetail()
//                                started = false
//                            }
//                        }
//                    } catch (e: IOException) {
//                        break
//                    }
//                }
//            }
//        }

        mSeries.color = Color.parseColor("#E84A48")
        graph.addSeries(mSeries)
        graph.title = "Fotopletismografía"
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.toDouble())
        graph.viewport.setMaxX(40.toDouble())
    }

    fun goToDetail() {
        mBluetoothHelper?.closeConnection()
        val intent = Intent(this, ResultsActivity::class.java)
        var max = 0
        var actualData = ArrayList<Data>()
        for (i in 0 until mBluetoothHelper?.dataList!!.size-1) {
            if (mBluetoothHelper?.dataList!![max].mmHg < mBluetoothHelper?.dataList!![i].mmHg)
                max = i
        }
        var firstTime = mBluetoothHelper?.dataList!![max].timer
        for (i in max until mBluetoothHelper?.dataList!!.size-1) {
            mBluetoothHelper?.dataList!![i].timer -= firstTime
            actualData.add(mBluetoothHelper?.dataList!![i])
        }
        if(actualData.size > 20) {
            intent.putExtra(LIST_ID, actualData)
            startActivityForResult(intent, 2)
        }
    }

    fun launchRefreshUiCheck() {
        launch {
            var dataSize = 0
            var counter = 0.0
            while (mBluetoothHelper?.started!!) {
                if (dataSize != mBluetoothHelper?.dataList!!.size) {
                    dataSize = mBluetoothHelper?.dataList!!.size
                    runOnUiThread {
                        speedMeter.setSpeed(mBluetoothHelper?.dataList!![dataSize-1].mmHg.toFloat())
                        mSeries.appendData(DataPoint(counter, mBluetoothHelper?.dataList!![dataSize-1].pulse), false, 1000)
                        counter++
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            mBluetoothHelper?.startConnection()
//            launch {
//                mBluetoothHelper?.mConnectedThread.run {
//                    var counter = 0.0
//                    while (true) {
//                        try {
//                            val data = this?.mmInStream?.bufferedReader()?.readLine()
//                            if (!data.isNullOrEmpty() && data?.filter { s -> s == ';' }?.count() == 2) {
//                                val pressure = data.substringBeforeLast(';').substringAfterLast(';')
//                                val pulse = data.substringAfterLast(';')
//                                if (pressure.toFloat() > 20f) {
//                                    if (!started) {
//                                        started = true
//                                    }
//                                    runOnUiThread {
//                                        speedMeter.setSpeed(pressure.toFloat())
//                                        mSeries.appendData(DataPoint(counter, pulse.toDouble()), false, 1000)
//                                        counter++
//                                    }
//                                } else {
//                                    if (started) {
//                                        goToDetail()
//                                    }
//                                }
//                            }
//                        } catch (e: IOException) {
//                            break
//                        }
//                    }
//                }
//            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        } else if (requestCode == 2 && resultCode == Activity.RESULT_CANCELED) {
            mBluetoothHelper?.started = false
            mBluetoothHelper?.startConnection()

//            launch {
//                mBluetoothHelper?.mConnectedThread.run {
//                    var counter = 0.0
//                    while (true) {
//                        try {
//                            val data = this?.mmInStream?.bufferedReader()?.readLine()
//                            if (!data.isNullOrEmpty() && data?.filter { s -> s == ';' }?.count() == 2) {
//                                val pressure = data.substringBeforeLast(';').substringAfterLast(';')
//                                val pulse = data.substringAfterLast(';')
//                                if (pressure.toFloat() > 20f) {
//                                    if (!started) {
//                                        started = true
//                                    }
//                                    runOnUiThread {
//                                        speedMeter.setSpeed(pressure.toFloat())
//                                        mSeries.appendData(DataPoint(counter, pulse.toDouble()), false, 1000)
//                                        counter++
//                                    }
//                                } else {
//                                    if (started) {
//                                        goToDetail()
//                                    }
//                                }
//                            }
//                        } catch (e: IOException) {
//                            break
//                        }
//                    }
//                }
//            }
        }
    }
}

@Parcelize
data class Data(var timer: Double, var mmHg: Double, var pulse: Double) : Parcelable


