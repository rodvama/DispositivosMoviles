package mx.itesm.proyectofinal

import Database.Medicion
import Database.MedicionDatabase
import Database.ioThread
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_patient_list.*

class PatientList : Activity(), CustomItemClickListener {

    companion object {
        var PATIENT_KEY:String = "Medicion"
    }

    lateinit var instanceDatabase: MedicionDatabase

    private var mediciones: ArrayList<Medicion>? = MedicionData().listaMedicion

    init {
        mediciones = MedicionData().listaMedicion
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)

        val layoutManager = LinearLayoutManager(this)
        lista_pacientes.layoutManager = layoutManager
        lista_pacientes.adapter = MeditionAdapter(mediciones!!, this)
        lista_pacientes.adapter?.notifyDataSetChanged()

        instanceDatabase = MedicionDatabase.getInstance(this)

        floatingActionButton.setOnClickListener { v -> onMeasure() }

        ioThread { loadMediciones() }
    }

    private fun onMeasure() {
        var intent = Intent(this, MainActivity::class.java)
        startActivityForResult(intent, 2)
    }

    override fun onCustomItemClick(medicion: Medicion) {
        val intent = Intent(this, ActivityDetail::class.java)

        intent.putExtra(PATIENT_KEY, medicion._id)
        startActivity(intent)
    }

    private fun loadMediciones() {
        ioThread {
            mediciones = ArrayList(instanceDatabase.medicionDao().cargarMeciciones())
            runOnUiThread {
                lista_pacientes.adapter?.notifyDataSetChanged()
            }
        }
    }

}

