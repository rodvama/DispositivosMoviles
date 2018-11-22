package mx.itesm.proyectofinal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_configuration.*

class ConfigurationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        this.title = "Configuración"

        var examplePrefs = getSharedPreferences("Shared", 0)
        var editor = examplePrefs.edit()
        switch1.isChecked = examplePrefs.getBoolean("Shared", false)

        switch1.setOnCheckedChangeListener { buttonView, nurseChecked ->
            editor.putBoolean("Shared", nurseChecked)
            editor.commit()
        }

        var bluetoothHelper = BluetoothHelper(this)

        if (bluetoothHelper.isEnabled() && bluetoothHelper.isDeviceConnected()) {
            textView4.text = "El dispositivo de onda de pulso se encuentra conectado."
        } else {
            textView4.text = "El dispositivo de onda de pulso no se encuentra conectado."
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                this.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
