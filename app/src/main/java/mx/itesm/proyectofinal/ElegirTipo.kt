package mx.itesm.proyectofinal

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_elegir_tipo.*

class ElegirTipo : AppCompatActivity() {

    lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elegir_tipo)

        val extras = intent.extras?: return

        this.profile = extras.getParcelable(PatientList.ACCOUNT)!!

        button_paciente.setOnClickListener { irPaciente() }
        button_clinica.setOnClickListener { irClinica() }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tipo, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, ConfigurationActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                false
            }
        }
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
        finish()
    }
}
