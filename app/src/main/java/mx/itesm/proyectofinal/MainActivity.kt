package mx.itesm.proyectofinal

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.*


class MainActivity : AppCompatActivity() {
    var mSeries: LineGraphSeries<DataPoint?>

    init {
        mSeries = LineGraphSeries(generateData())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        pressure_txt.text = "140"
        mSeries.color = Color.parseColor("#E84A48")
        graph.addSeries(mSeries)
        graph.title = "Pulso"
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.toDouble())
        graph.viewport.setMaxX(40.toDouble())

        measure_btn.setOnClickListener{ v -> onClick() }
    }

    fun onClick() {
        speedMeter.setSpeed(240f)
        initializeDataPlot()
    }

    fun generateData(): Array<DataPoint?> {
        val count = 30
        val values = arrayOfNulls<DataPoint>(count)
        for (i in 0 until count) {
            val x = i.toDouble()
            val f = Random().nextDouble() * 0.15 + 0.3
            val y = Math.sin(i * f + 2) + Random().nextDouble() * 0.3
            val v = DataPoint(x, y)
            values[i] = v
        }
        return values
    }

    fun initializeDataPlot() {
        launch {
            for (i in 0..500) {
                mSeries.appendData(DataPoint((31+i).toDouble(), Random().nextDouble()), true, 40)
                delay(500)
            }
        }
    }
}
