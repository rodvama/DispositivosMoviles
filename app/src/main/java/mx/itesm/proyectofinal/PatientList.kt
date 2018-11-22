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

class PatientList : AppCompatActivity(), CustomItemClickListener {

    companion object {
        var PATIENT_KEY:String = "Medicion"
        var bluetoothHelper: BluetoothHelper? = null
    }

    lateinit var instanceDatabase: MedicionDatabase
    val adapter = MeditionAdapter(this, this)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun onMeasure() {
        var intent = Intent(this, MainActivity::class.java)
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            //loadMediciones()
        }
    }

    override fun onCustomItemClick(medicion: Medicion) {
        val intent = Intent(this, ActivityDetail::class.java)

        intent.putExtra(PATIENT_KEY, medicion._id)
        startActivity(intent)
    }

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

    fun insertMeasurements(context: Context){
        val measurements:List<Medicion> = MedicionData(context).listaMedicion
        ioThread {
            instanceDatabase.medicionDao().insertartListaMediciones(measurements)
            loadMediciones()
        }
    }

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

