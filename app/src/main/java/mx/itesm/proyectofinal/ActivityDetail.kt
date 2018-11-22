package mx.itesm.proyectofinal

import Database.Medicion
import Database.MedicionDatabase
import Database.ioThread
import android.arch.lifecycle.Observer
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detail.*

class ActivityDetail : AppCompatActivity() {

    lateinit var instanceDatabase: MedicionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        instanceDatabase = MedicionDatabase.getInstance(this)

        val extras = intent.extras?:return
        val idExtra = extras.getInt(PatientList.PATIENT_KEY)

        ioThread {
            val measurementObj = instanceDatabase.medicionDao().cargarMedicionId(idExtra)

            measurementObj.observe(this, object: Observer<Medicion>{
                override fun onChanged(measurementObj: Medicion?) {
                    title = measurementObj?.iniciales

                    checkbox_verified.isChecked = measurementObj?.verificado!!

                    val deviceResults = measurementObj.appSistolica + " / " + measurementObj.appDiastolica
                    tv_device_results.text = deviceResults

                    val manualResults = measurementObj.manSistolica + " / " + measurementObj.manDiastolica
                    tv_manual_results.text = manualResults

                    if(measurementObj.brazo == "I"){
                        tv_arm_results.text = "Izquierdo"
                    }
                    else if(measurementObj.brazo == "D") {
                        tv_arm_results.text = "Derecho"
                    }

                    if(measurementObj.grafica != null) {
                        val image = BitmapFactory.decodeByteArray(measurementObj.grafica, 0, measurementObj.grafica!!.size)
                        image_graph.setImageBitmap(image)
                    }
                }
            })
        }

        checkbox_verified.setOnCheckedChangeListener { buttonView, isChecked ->
            ioThread {
                instanceDatabase.medicionDao().updateMedicion(idExtra, isChecked)
            }
        }

    }
}
