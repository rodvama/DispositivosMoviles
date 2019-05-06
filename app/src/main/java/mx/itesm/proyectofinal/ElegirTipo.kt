package mx.itesm.proyectofinal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_elegir_tipo.*

class ElegirTipo : AppCompatActivity() {

    lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elegir_tipo)

        val extras = intent.extras?: return

        profile = extras.getParcelable(PatientList.ACCOUNT)!!

        button_paciente.setOnClickListener { irPaciente() }
        button_clinica.setOnClickListener { irClinica() }
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

    fun irClinica(){
        val StartAppIntent = Intent(this,Clinic_list::class.java)
        StartAppIntent.putExtra(PatientList.ACCOUNT,profile)
        StartAppIntent.putExtra(PatientList.ACCOUNT_TYPE,0)
        startActivity(StartAppIntent)
    }

    fun irPaciente(){
        val StartAppIntent = Intent(this,PatientList::class.java)
        StartAppIntent.putExtra(PatientList.ACCOUNT,profile)
        StartAppIntent.putExtra(PatientList.ACCOUNT_TYPE,1)
        startActivity(StartAppIntent)
    }

    override fun onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}
