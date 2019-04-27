package mx.itesm.proyectofinal

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_elegir_tipo.*

class ElegirTipo : AppCompatActivity() {

    var nombre : String = ""
    var mail : String = ""
    var photo : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elegir_tipo)

        val extras = intent.extras?: return

        nombre = extras.getString(PatientList.ACCOUNT_NAME)
        mail   = extras.getString(PatientList.ACCOUNT_MAIL)
        photo  = extras.getString(PatientList.ACCOUNT_IMG)

        button_paciente.setOnClickListener { irPaciente() }
        button_clinica.setOnClickListener { irClinica() }
    }

    fun irClinica(){
        val StartAppIntent = Intent(this,Clinic_list::class.java)
        StartAppIntent.putExtra(PatientList.ACCOUNT_MAIL,mail)
        StartAppIntent.putExtra(PatientList.ACCOUNT_NAME,nombre)
        StartAppIntent.putExtra(PatientList.ACCOUNT_IMG,photo)
        startActivity(StartAppIntent)
    }

    fun irPaciente(){
        val StartAppIntent = Intent(this,PatientList::class.java)
        StartAppIntent.putExtra(PatientList.ACCOUNT_MAIL,mail)
        StartAppIntent.putExtra(PatientList.ACCOUNT_NAME,nombre)
        StartAppIntent.putExtra(PatientList.ACCOUNT_IMG,photo)
        startActivity(StartAppIntent)
    }

    override fun onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}
