package mx.itesm.proyectofinal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_configuration.*

class ConfigurationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        this.title = "Configuración"

        var bluetoothHelper = BluetoothHelper(this)

        if (bluetoothHelper.isEnabled() && bluetoothHelper.isDeviceConnected()) {
            textView4.text = "El dispositivo de onda de pulso se encuentra conectado."
        } else {
            textView4.text = "El dispositivo de onda de pulso no se encuentra conectado."
        }
    }
}
