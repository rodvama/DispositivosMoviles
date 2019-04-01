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
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_patient_list.*
import mx.itesm.proyectofinal.BluetoothHelper.Companion.REQUEST_ENABLE_BT

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
        var DELETE_ID: String = "id"
        var DEL: String = "Borrar ?"
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
        val intent = Intent(this, DeviceScanActivity::class.java)
        startActivityForResult(intent, 100)
//        var intent = Intent(this, MainActivity::class.java)
//        startActivityForResult(intent, 2)
    }

    // When receiving information from the measurement class
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, R.string.bluetooth_permission_not_granted,
                    Toast.LENGTH_LONG).show()
//            finish()
            return
        }
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, R.string.bluetooth_permission_granted,
                    Toast.LENGTH_LONG).show()
//            finish()
            return
        }
//        if(requestCode == 2 && resultCode == Activity.RESULT_OK){
//            //loadMediciones()
//        }
//
//        if (requestCode == 3 && resultCode == Activity.RESULT_OK){
//            if (data?.getBooleanExtra(DEL, false) == true) {
//                ioThread {
//                    instanceDatabase.medicionDao().borrarMedicion(data.getIntExtra(DELETE_ID, 0))
//                }
//            }
//        }
    }

    /**
     * If the user either accept or reject the Permission- The requested App will get a callback
     * Form the call back we can filter the user response with the help of request key
     * If the user accept the same- We can proceed further steps
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            BluetoothHelper.REQUEST_COARSE_LOCATION_PERMISSION -> {
                if (permissions.size != 1 || grantResults.size != 1) {
                    throw RuntimeException("Error on requesting location permission.")
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Toast.makeText(this, R.string.location_permission_granted,
                            Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, R.string.location_permission_not_granted,
                            Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Custom item click listener for each measurement
    override fun onCustomItemClick(medicion: Medicion) {
        val intent = Intent(this, ActivityDetail::class.java)

        intent.putExtra(PATIENT_KEY, medicion._id)
        startActivityForResult(intent, 3)
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
        return when (item?.itemId){
            R.id.action_settings ->{
                val intent = Intent(this, ConfigurationActivity::class.java)

                startActivity(intent)
                true
            }
            else -> {
                false
            }
        }
    }

}

