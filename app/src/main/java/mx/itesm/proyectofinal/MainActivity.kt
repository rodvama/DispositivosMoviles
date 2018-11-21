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
import kotlinx.android.parcel.Parcelize
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mSeries: LineGraphSeries<DataPoint?> = LineGraphSeries()
    var mBluetoothHelper: BluetoothHelper? = null
    var dataList: MutableList<Data> = Arrays.asList()
    var started = false

    companion object {
        const val LIST_ID = "DataList"
    }

    init {

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
                            if (pressure.toFloat() > 20f) {
                                if (!started) {
                                    started = true
                                }
                                dataList.add(Data(System.currentTimeMillis().toDouble(), pressure.toDouble(), pulse.toDouble()))
                                runOnUiThread {
                                    speedMeter.setSpeed(pressure.toFloat())
                                    mSeries.appendData(DataPoint(counter, pulse.toDouble()), false, 1000)
                                    counter++
                                }
                            } else {
                                if (started) {
                                    goToDetail()
                                }
                            }
                        }
                    } catch (e: IOException) {
                        break
                    }
                }
            }
        }

        mSeries.color = Color.parseColor("#E84A48")
        graph.addSeries(mSeries)
        graph.title = "Fotopletismografía"
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.toDouble())
        graph.viewport.setMaxX(40.toDouble())
    }

    private fun goToDetail() {
        val intent = Intent(this, ActivityDetail::class.java)
        for (data in dataList) {
            data.timer = data.timer - dataList.first().timer
        }
        intent.putExtra("DataList", dataList.toTypedArray())
        startActivityForResult(intent, 2)
    }

    fun setTestData() {

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
                                if (pressure.toFloat() > 20f) {
                                    if (!started) {
                                        started = true
                                    }
                                    runOnUiThread {
                                        speedMeter.setSpeed(pressure.toFloat())
                                        mSeries.appendData(DataPoint(counter, pulse.toDouble()), false, 1000)
                                        counter++
                                    }
                                } else {
                                    if (started) {
                                        goToDetail()
                                    }
                                }
                            }
                        } catch (e: IOException) {
                            break
                        }
                    }
                }
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        } else if (requestCode == 2 && resultCode == Activity.RESULT_CANCELED) {
            started = false

            launch {
                mBluetoothHelper?.mConnectedThread.run {
                    var counter = 0.0
                    while (true) {
                        try {
                            val data = this?.mmInStream?.bufferedReader()?.readLine()
                            if (!data.isNullOrEmpty() && data?.filter { s -> s == ';' }?.count() == 2) {
                                val pressure = data.substringBeforeLast(';').substringAfterLast(';')
                                val pulse = data.substringAfterLast(';')
                                if (pressure.toFloat() > 20f) {
                                    if (!started) {
                                        started = true
                                    }
                                    runOnUiThread {
                                        speedMeter.setSpeed(pressure.toFloat())
                                        mSeries.appendData(DataPoint(counter, pulse.toDouble()), false, 1000)
                                        counter++
                                    }
                                } else {
                                    if (started) {
                                        goToDetail()
                                    }
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
}

@Parcelize
data class Data(var timer: Double, var mmHg: Double, var pulse: Double) : Parcelable


