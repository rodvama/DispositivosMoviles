package mx.itesm.proyectofinal

import Database.MedicionDatabase
import Database.ioThread
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detail.*

class ActivityDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val instanceDatabase = MedicionDatabase.getInstance(this)

        val extras = intent.extras?:return
        val id = extras.getInt(PATIENT_KEY)

        ioThread {
            val measurementObj = instanceDatabase.medicionDao().cargarMedicionId(id)
            runOnUiThread{
                title = "ID " + id.toString()

                checkbox_verified.isChecked = measurementObj.verificado!!

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

                val image = BitmapFactory.decodeByteArray(measurementObj.grafica, 0, measurementObj.grafica!!.size)
                image_graph.setImageBitmap(image)
            }
        }

        checkbox_verified.setOnCheckedChangeListener { buttonView, isChecked ->
            ioThread {
                instanceDatabase.medicionDao().updateMedicion(id, isChecked)
            }
        }

    }
}
