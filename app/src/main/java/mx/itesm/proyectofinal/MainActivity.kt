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
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var mSeries: LineGraphSeries<DataPoint?> = LineGraphSeries()
    var mBluetoothHelper: BluetoothHelper? = null

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

        mSeries.color = Color.parseColor("#E84A48")
        graph.addSeries(mSeries)
        graph.title = "BPM"
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.toDouble())
        graph.viewport.setMaxX(40.toDouble())

        measure_btn.setOnClickListener{ onClick() }
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
}


