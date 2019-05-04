package mx.itesm.proyectofinal

import Database.Medicion
import Database.MedicionDatabase
import Database.Patient
import Database.ioThread
import android.arch.lifecycle.Observer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_clinic_list.*
import org.jetbrains.anko.doAsync

class Clinic_list : AppCompatActivity(),CustomItemClickListener2 {

    companion object {
        val ACCOUNT_MAIL:String = "account_mail"
        val ACCOUNT_NAME:String = "account_name"
        val ACCOUNT_IMG:String = "account_img"
    }

    lateinit var mail : String
    // Database variable initialization
    lateinit var instanceDatabase: MedicionDatabase

    // The RecyclerView adapter declaration
    val adapter = PatientAdapter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clinic_list)
        val extras = intent.extras?: return

        val nombre = extras.getString(PatientList.ACCOUNT_NAME)
        mail = extras.getString(PatientList.ACCOUNT_MAIL)
        val photo  = extras.getString(PatientList.ACCOUNT_IMG)

        textView_nombre.text = "Clinica/Doctor: "+nombre

        val layoutManager = LinearLayoutManager(this)
        lista_clinica.layoutManager = layoutManager

        instanceDatabase = MedicionDatabase.getInstance(this)

        lista_clinica.adapter = adapter

        ioThread {
            val pacienteNum = instanceDatabase.pacienteDao().getAnyPaciente(mail)

            if(pacienteNum == 0){
                insertPacientes(this)
            } else{
                loadPacientes ()
            }
        }
        val broadcast_reciever = object : BroadcastReceiver() {

            override fun onReceive(arg0: Context, intent: Intent) {
                val action = intent.action
                if (action == "sign_out") {
                    finish()
                    // DO WHATEVER YOU WANT.
                }
            }
        }
        registerReceiver(broadcast_reciever, IntentFilter("sign_out"))
    }

    // Loads measurements from database
    private fun loadPacientes() {
        val pacientes = instanceDatabase.pacienteDao().cargarPacientes(mail)

        pacientes.observe(this, object: Observer<List<Patient>> {
            override fun onChanged(t: List<Patient>?) {
                adapter.setPatient(t!!)
                lista_clinica.adapter = adapter
                lista_clinica.adapter?.notifyDataSetChanged()

            }
        })

    }

    // Inserts a new measurements to the list in DB
    fun insertPacientes(context: Context){
        var patients:List<Patient>
        doAsync {
            patients = Medicion.populatePatients(applicationContext, mail)
            instanceDatabase.pacienteDao().insertartListaPacientes(patients)
            loadPacientes()
        }
        //ioThread {
        /*
        * Llenar lista con las mediciones del servicio web
        * */
        ///instanceDatabase.medicionDao().insertartListaMediciones(measurements)
        ///loadMediciones()
        //}
    }

    // Custom item click listener for each measurement
    override fun onCustomItemClick(patient: Patient) {
        //val intent = Intent(this, ::class.java)
        //intent.putExtra(PatientList.PATIENT_KEY, patient._idP)
        //startActivityForResult(intent, 3)
        val StartAppIntent = Intent(this,PatientList::class.java)
        StartAppIntent.putExtra(PatientList.ACCOUNT_MAIL,patient.mailC)
        StartAppIntent.putExtra(PatientList.ACCOUNT_NAME,patient.FNameP+" "+patient.LNameP)
        startActivity(StartAppIntent)
    }
}
