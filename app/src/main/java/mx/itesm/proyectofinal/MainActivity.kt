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
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch

/*
 * MainActivity class declares the activity and inflates the view.
 */
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

        mBluetoothHelper?.started = true

        launchRefreshUiCheck()
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
        launch {
            var dataSize = 0
            var counter = 0.0
            while (mBluetoothHelper?.started!!) {
                val list = mBluetoothHelper?.dataList!!
                if (dataSize != list.size) {
                    dataSize = list.size
                    runOnUiThread {
                        if (list.size > 0) {
                            speedMeter.setSpeed(list[dataSize-1].mmHg.toFloat())
                            mSeries.appendData(DataPoint(counter, list[dataSize-1].pulse), false, 1000)
                            counter++
                        }
                    }
                    if (list != null && list.size > 0 && list.size > 250 && list.last().mmHg >= 0 && list.last().mmHg <= 25)
                        mBluetoothHelper?.started = false
                }
            }
            goToDetail()
        }
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
}

// Data class. An ArrayList of this type is sent to ResultsActivity
@Parcelize
data class Data(var timer: Double, var mmHg: Double, var pulse: Double) : Parcelable


