package mx.itesm.proyectofinal

import Database.MedicionDatabase
import Database.ioThread
import android.arch.persistence.room.Database
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_detail.*

class ActivityDetail : AppCompatActivity() {

    lateinit var instanceDatabase: MedicionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        instanceDatabase = MedicionDatabase.getInstance(this)

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

        button_enviarCorreo.setOnClickListener { v ->  sendMail(id) }
    }

    fun sendMail (id:Int) {
        ioThread {
            val measure = instanceDatabase.medicionDao().cargarMedicionId(id)
            val i = Intent(Intent.ACTION_SEND)
            i.type = "message/rfc822"
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf("recipient@example.com"))
            i.putExtra(Intent.EXTRA_SUBJECT, "subject of email")

            var text = "Fecha: " + measure.fecha +
                    "\nIniciales: " + measure.iniciales +
                    "\nBrazo: " + measure.brazo +
                    "\n\nApp: \n" + "    Diastolica: " + measure.appDiastolica + "\n   Sistolica: " + measure.appSistolica +
                    "\n\nManual: \n" + "    Diastolica: " + measure.manDiastolica + "\n   Sistolica: " + measure.manDiastolica

            i.putExtra(Intent.EXTRA_TEXT, text)
            try {
                startActivity(Intent.createChooser(i, "Send mail..."))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(applicationContext, "There are no email clients installed.", Toast.LENGTH_SHORT)
                        .show()
            }

        }
    }
}
