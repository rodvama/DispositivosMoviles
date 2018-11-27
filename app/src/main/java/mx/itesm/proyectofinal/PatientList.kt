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
import android.os.Bundle
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_patient_list.*
import java.util.*

/*
 * Declares the patient measurements list. This is the first and main page of the application
 */
class PatientList : AppCompatActivity(), CustomItemClickListener {

    /*
     * Companion objects. Since this is the first activity to execute, this one declares the
     * bluetooth helper and initializes it.
     */
    companion object {
        var PATIENT_KEY:String = "Medicion"
        var bluetoothHelper: BluetoothHelper? = null
    }

    // Database variable initialization
    lateinit var instanceDatabase: MedicionDatabase

    // The RecyclerView adapter declaration
    val adapter = MeditionAdapter(this, this)

    /*
     * Creates the Patient List activity and inflates the view. Also initializes database calls.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)
        //setSupportActionBar(findViewById(R.id.my_toolbar))

        bluetoothHelper = BluetoothHelper(this)

        val layoutManager = LinearLayoutManager(this)
        lista_pacientes.layoutManager = layoutManager

        instanceDatabase = MedicionDatabase.getInstance(this)

        lista_pacientes.adapter = adapter

        ioThread {
            val measureNum = instanceDatabase.medicionDao().getAnyMedicion()

            if(measureNum == 0){
                insertMeasurements(this)
            } else{
                loadMediciones()
            }
        }

        floatingActionButton.setOnClickListener { v -> onMeasure() }
    }

    /*
     * Inflates FAB button
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Starts the MainActivity, which starts measuring data from the bluetooth device.
    private fun onMeasure() {
        var intent = Intent(this, MainActivity::class.java)
        startActivityForResult(intent, 2)
    }

    // When receiving information from the measurement class
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            //loadMediciones()
        }
    }

    // Custom item click listener for each measurement
    override fun onCustomItemClick(medicion: Medicion) {
        val intent = Intent(this, ActivityDetail::class.java)

        intent.putExtra(PATIENT_KEY, medicion._id)
        startActivity(intent)
    }

    // Loads measurements from database
    private fun loadMediciones() {
        val measurements = instanceDatabase.medicionDao().cargarMeciciones()

        measurements.observe(this, object: Observer<List<Medicion>> {
            override fun onChanged(t: List<Medicion>?) {
                adapter.setMedicion(t!!)
                lista_pacientes.adapter = adapter
                lista_pacientes.adapter?.notifyDataSetChanged()

            }
        })

    }

    // Inserts a new measurements to the list in DB
    fun insertMeasurements(context: Context){
        val measurements:List<Medicion> = MedicionData(context).listaMedicion
        ioThread {
            instanceDatabase.medicionDao().insertartListaMediciones(measurements)
            loadMediciones()
        }
    }

    // Handles clicking options item
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.action_settings ->{
                val intent = Intent(this, ConfigurationActivity::class.java)

                startActivity(intent)
                return true
            }
            else -> {
                return false
            }
        }
    }

}

