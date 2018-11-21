package mx.itesm.proyectofinal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.android.synthetic.main.activity_configuration.view.*
import kotlinx.android.synthetic.main.activity_configuration.*

class ConfigurationActivity : AppCompatActivity() {

    companion object {
        var isNurseMode = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        this.title = "Configuración"

        switch1.setOnCheckedChangeListener { buttonView, nurseChecked ->
            isNurseMode = nurseChecked
        }

        var bluetoothHelper = BluetoothHelper(this)

        if (bluetoothHelper.isEnabled() && bluetoothHelper.isDeviceConnected()) {
            textView4.text = "El dispositivo de onda de pulso se encuentra conectado."
        } else {
            textView4.text = "El dispositivo de onda de pulso no se encuentra conectado."
        }
    }
}
