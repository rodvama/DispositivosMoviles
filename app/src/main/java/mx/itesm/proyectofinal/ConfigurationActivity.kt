package mx.itesm.proyectofinal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ConfigurationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        this.title = "Configuración"
    }
}
