package mx.itesm.proyectofinal

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_results.*
import java.lang.Math.abs

class ResultsActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val SYSTOLIC_DEVICE = "systolic_results"
        const val DIASTOLIC_DEVICE = "diastolic_results"
        const val SYSTOLIC_MANUAL = "systolic_manual_results"
        const val DIASTOLIC_MANUAL = "diastolic_manual_results"
        const val VERIFIED_VALUE = "verified_check"
        const val GRAPH_ID = "generated_graph"
    }

    var systolicRes: Double = 0.0
    var diastolicRes: Double = 0.0
    var validateCheck: Boolean = false

    private var mSeries: LineGraphSeries<DataPoint?> = LineGraphSeries()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        this.title = "Resultados"

        val extras = intent.extras?:return

        val dataList: List<Data> = extras.getParcelableArrayList(MainActivity.LIST_ID)!!

        mSeries.color = Color.parseColor("#FF6860")
        time_graph.addSeries(mSeries)
        time_graph.title = "Datos de presión"


        calculateResults(dataList)

        button_accept.setOnClickListener { v -> onClick(v) }
        button_retry.setOnClickListener { v -> onClick(v) }

        checkBox_verify_results.setOnCheckedChangeListener { _, isChecked ->
            validateCheck = isChecked
        }
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.button_accept -> {
                if(edit_diastolic_manual.text.isEmpty() || edit_systolic_manual.text.isEmpty()) {
                    val resultIntent = Intent()

                    resultIntent.putExtra(SYSTOLIC_DEVICE, systolicRes)
                    resultIntent.putExtra(DIASTOLIC_DEVICE, diastolicRes)
                    resultIntent.putExtra(SYSTOLIC_MANUAL, edit_systolic_manual.text)
                    resultIntent.putExtra(DIASTOLIC_MANUAL, edit_diastolic_manual.text)
                    resultIntent.putExtra(VERIFIED_VALUE, validateCheck)
                    resultIntent.putExtra(GRAPH_ID, time_graph.takeSnapshot())

                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                else {
                    Toast.makeText(this, "Llena los campos manuales", Toast.LENGTH_LONG).show()
                }
            }

            R.id.button_retry -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    private fun calculateResults(data: List<Data>){

        val fixed = arrayOfNulls<Double>(data.size)     //Arreglo para almacenar los valores de Fixed mmHg
        var fixedSum = 0.0                              //Suma de valores de Fixed mmHg, utilizado para cálculos de mmHg mov
        val mov = arrayOfNulls<Double>(data.size)       //Arreglo para los valores de mmHg mov
        val pend = arrayOfNulls<Double>(data.size)      //Arreglo para los valores de Pend
        var pendSum = 0.0                               //Variable para almacenar la suma que se usará para calcular el promedio de Pend
        val pendN = arrayOfNulls<Double>(data.size)     //Arreglo para almacenar los valores de Pend Norm
        val pendNMov = arrayOfNulls<Double>(data.size)  //Arreglo para almacenar los valores de Pend Norm mov
        val peaks = arrayOfNulls<Double>(data.size)     //Arreglo para almacenar la ocurrencia de picos
        val start = arrayOfNulls<Double>(data.size)     //Arreglo para almacenar la ocurrencia de start
        val startMem = arrayOfNulls<Double>(data.size)  //Arreglo para guardar los valores de Start Memory
        val startTime = arrayOfNulls<Int>(data.size)    //Arreglo para guardar los valores de Start Time
        val endMem = arrayOfNulls<Double>(data.size)    //Arreglo para guardar los valores de End Memory
        val endTime = arrayOfNulls<Int>(data.size)      //Arreglo para guardar los valores de End Time
        val cuff = arrayOfNulls<Double>(data.size)      //Arreglo donde se vayan registrando los Peak cuff
        val amp = arrayOfNulls<Double>(data.size)       //Arreglo donde se vayan registrando las amplitudes de los picos
        val systolic = arrayOfNulls<Double>(data.size)  //Arreglo para guardar los cálculos de presión sistólica
        val diastolic = arrayOfNulls<Double>(data.size) //Arreglo para guardar los cálculos de presión diastólica

        //Cálculo de primeros valores filtrados de mmHg
        fixed[0] = data[0].mmHg
        fixed[1] = data[1].mmHg

        mSeries.appendData(DataPoint(data[0].timer, fixed[0]!!), false, data.size)
        mSeries.appendData(DataPoint(data[1].timer, fixed[1]!!), false, data.size)


        //Primer recorrido
        for(i in 2 until data.size){
            //Cálculo de los datos de presión filtrados
            if(abs(data[i].mmHg - data[i-1].mmHg) < 4){
                fixed[i] = data[i].mmHg
            } else if(abs(data[i-1].mmHg - data[i-2].mmHg) >= 4){
                fixed[i] = data[i].mmHg
            } else if(data[i+1].mmHg != null){
                fixed[i] = (data[i-1].mmHg + data[i+1].mmHg) / 2
            }else{
                fixed[i] = data[i-1].mmHg
            }

            mSeries.appendData(DataPoint(data[i].timer, fixed[i]!!), false, data.size)

            //Cálculo de mmHg mov (hasta n-5)
            fixedSum += fixed[i]!!
            if(i>7){
                mov[i-4] = fixedSum/9.0
                fixedSum -= fixed[i-8]!!

                //Cálculo de Pend
                if(i>8){
                    pend[i-4] = mov[i-4]!! - mov[i-5]!!
                    pendSum += pend[i-4]!!
                }
            }
        }

        //Cálculo para las últimas 4 posiciones de mmHg mov
        for(i in data.size-4 until data.size){
            mov[i] = fixedSum/(data.size-i+4)
            pend[i] = mov[i]!! - mov[i-1]!!

            //Cálculo de Pend
            pendSum += pend[i-4]!!
            fixedSum -= fixed[i-4]!!
        }

        val avgPend = pendSum / (data.size-5)   //Promedio de pendiente
        var sumPendN = 0.0                      //Variable que se usará para el cálculo de Pend Norm mov

        //Inicializar valores de Start en 0
        startMem[4] = 0.0
        startTime[4] = 0

        //Cálculo de pendiente normalizada
        for(i in 5 until data.size){
            pendN[i] = pend[i]!! - avgPend
            sumPendN += pendN[i]!!

            //Cálculo de pendiente normal mov
            if(i>12){
                pendNMov[i-4] = sumPendN/9
                sumPendN -= pendN[i-8]!!
            }

            if(i>13){
                //Cálculo de Peaks
                if(pendNMov[i]!! < pendNMov[i-1]!! && pendNMov[i-1]!! * pendNMov[i]!! < 0){
                    peaks[i] = mov[i]
                }

                //Cálculo de start
                if(pendNMov[i]!! > pendNMov[i-1]!! && pendNMov[i-1]!! * pendNMov[i]!! < 0){
                    start[i] = mov[i]
                }
            }

            //Cálculo de start memory y start time
            if(start[i]!=null){
                startMem[i] = start[i]
                startTime[i] = data[i].timer.toInt()
            } else{
                startMem[i] = startMem[i-1]
                startTime[i] = startTime[i-1]
            }
        }

        //Cálculo de últimas 4 posiciones para la pendiente normal mov
        for(i in data.size-4 until data.size){
            pendNMov[i] = sumPendN/(data.size-i+4)

            sumPendN -= pendN[i-4]!!

            //Cálculo de Peaks
            if(pendNMov[i]!! < pendNMov[i-1]!! && pendNMov[i-1]!! * pendNMov[i]!! < 0){
                peaks[i] = mov[i]
            }

            //Cálculo de start
            if(pendNMov[i]!! > pendNMov[i-1]!! && pendNMov[i-1]!! * pendNMov[i]!! < 0){
                start[i] = mov[i]
            }

            //Cálculo de start memory y start time
            if(start[i]!=null){
                startMem[i] = start[i]
                startTime[i] = data[i].timer.toInt()
            } else{
                startMem[i] = startMem[i-1]
                startTime[i] = startTime[i-1]
            }
        }

        //Inicializar valores de las últimas casillas para end memory y end time
        endMem[data.size-1] = 0.0
        endTime[data.size-1] = 0

        //Declarar una variable para calcular el máximo de un arreglo de amplitudes
        var ampMax = 0.0

        //Recorrido a la inversa para End memory y End Time
        for(i in data.size-1 downTo 5){
            if(start[i]!=null){
                endMem[i] = startMem[i]
                endTime[i] = data[i].timer.toInt()
            }else{
                endMem[i] = endMem[i+1]
                endTime[i] = endTime[i+1]
            }

            //Cálculo de Peak cuff y Peak Amplitude
            if(peaks[i]!=null){
                cuff[i]	= ((endMem[i]!! - startMem[i]!!)/(endTime[i]!! - startTime[i]!!))*(data[i].timer.toInt() - startTime[i]!!) + startMem[i]!!
                amp[i] = peaks[i]!! - cuff[i]!!

                //Encontrar la amplitud máxima
                if(amp[i]!! > ampMax){
                    ampMax = amp[i]!!
                }
            }
        }

        //Inicializar las ultimas casillas de systolic y diastolic en 0.0
        systolic[data.size - 1] = 0.0
        diastolic[data.size - 1] = 0.0

        //Recorrido a la inversa para el cálculo de Systolic y Diastolic
        for(i in data.size-1 downTo 0){
            if(amp[i] != null && amp[i]!! >= 0.5*ampMax){
                systolic[i] = peaks[i]
            } else{
                systolic[i] = systolic[i+1]
            }

            if(amp[i] != null && amp[i]!! >= 0.8*ampMax && diastolic[i+1]==0.0){
                diastolic[i] = peaks[i]
            } else{
                diastolic[i] = diastolic[i+1]
            }
        }

        systolicRes = systolic[0]!!
        diastolicRes = diastolic[0]!!

        val results = systolic[0]!!.toInt().toString() + " / " + diastolic[0]!!.toInt().toString()

        tv_device_results.text = results
    }

}
