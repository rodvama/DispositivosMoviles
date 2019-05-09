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

import Database.Medicion
import Database.MedicionDatabase
import Database.ioThread
import android.app.Activity
import android.arch.lifecycle.LiveData
import android.content.Intent
import android.arch.lifecycle.Observer
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_detail.*
import mx.itesm.proyectofinal.PatientList.Companion.DEL
import mx.itesm.proyectofinal.PatientList.Companion.DELETE_ID

class ActivityDetail : AppCompatActivity() {

    lateinit var instanceDatabase: MedicionDatabase
    var idExtra: Int = 0
    lateinit var measurementObj: LiveData<Medicion>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        instanceDatabase = MedicionDatabase.getInstance(this)


        val extras = intent.extras?:return
        this.idExtra = extras.getInt(PatientList.PATIENT_KEY)

        ioThread {
           this.measurementObj = instanceDatabase.medicionDao().cargarMedicionId(idExtra)

            this.measurementObj.observe(this, object: Observer<Medicion>{
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
                this.instanceDatabase.medicionDao().updateMedicion(idExtra, isChecked)
            }
        }

    }

    /*
 * Inflates FAB button
 */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    // Handles clicking options item
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                this.onBackPressed()
                return true
            }
            R.id.action_delete ->{
                val data = Intent()
                data.putExtra(
                        DELETE_ID,
                        this.idExtra
                )
                data.putExtra(DEL, true )
                setResult(Activity.RESULT_OK, data)

                this.measurementObj.removeObservers(this)
                finish()
                true
            }
            R.id.action_sendEmail-> {
                sendMail()
                true
            }
            else -> {
                false
            }
        }
    }

    fun sendMail () {
        ioThread {
            val measure = this.instanceDatabase.medicionDao().cargarMedicionPorId(idExtra)
            val i = Intent(Intent.ACTION_SEND)
            i.type = "message/rfc822"
            //Cliente ingresa correo de remitente
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
            i.putExtra(Intent.EXTRA_SUBJECT, "Medicion: " + measure.fecha + " " + measure.iniciales)

            var text = "Fecha: " + measure.fecha +
                    "\nIniciales: " + measure.iniciales +
                    "\nBrazo: " + measure.brazo +
                    "\n\nApp: \n" + "    Sistolica: " + measure.appSistolica + "\n   Diastolica: " + measure.appDiastolica +
                    "\n\nManual: \n" + "    Sistolica: " + measure.manSistolica + "\n   Diastolica: " + measure.manDiastolica

            i.putExtra(Intent.EXTRA_TEXT, text)
            try {
                startActivity(Intent.createChooser(i, "Send mail..."))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(applicationContext, "There are no email clients installed.", Toast.LENGTH_SHORT)
                        .show()
            }

        }
    }

    // Handles clicking the back button
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
