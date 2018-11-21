package mx.itesm.proyectofinal

import Database.Medicion
import Database.MedicionDatabase
import Database.ioThread
import android.os.Bundle
import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_patient_list.*

class PatientList : Activity() {

    lateinit var instanceDatabase: MedicionDatabase
    var listener: CustomItemClickListener? = null
    private var mediciones: List<Medicion>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)

        val layoutManager = LinearLayoutManager(this)
        lista_pacientes.layoutManager = layoutManager
        lista_pacientes.adapter = MeditionAdapter(mediciones!!, listener!!)
        lista_pacientes.adapter?.notifyDataSetChanged()

        instanceDatabase = MedicionDatabase.getInstance(this)

        ioThread { loadMediciones() }
    }

    private fun loadMediciones() {
        ioThread {
            mediciones = instanceDatabase.medicionDao().cargarMeciciones()
            lista_pacientes.adapter?.notifyDataSetChanged()
        }
    }

}

