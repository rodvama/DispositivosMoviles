package mx.itesm.proyectofinal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_results.*
import java.lang.Math.abs

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        this.title = "Resultados"

        val extras = intent.extras?:return

        val dataList: List<Measurement> = extras.getParcelableArrayList(intentString)

        calculateResults(dataList)
    }

    private fun calculateResults(data: List<Measurement>){

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
                startTime[i] = data[i].time
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
                startTime[i] = data[i].time
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
                endTime[i] = data[i].time
            }else{
                endMem[i] = endMem[i+1]
                endTime[i] = endTime[i+1]
            }

            //Cálculo de Peak cuff y Peak Amplitude
            if(peaks[i]!=null){
                cuff[i]	= ((endMem[i]!! - startMem[i]!!)/(endTime[i]!! - startTime[i]!!))*(data[i].time - startTime[i]) + startMem[i]
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

        val results = systolic[0].toString() + " / " + diastolic[0].toString()

        tv_device_results.text = results
    }
}
