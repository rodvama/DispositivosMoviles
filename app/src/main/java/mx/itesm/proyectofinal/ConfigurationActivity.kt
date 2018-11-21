package mx.itesm.proyectofinal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.android.synthetic.main.activity_configuration.view.*

class ConfigurationActivity : AppCompatActivity() {

    companion object {
        public var isNurse = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        this.title = "Configuración"

        switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            isNurse = isChecked
        }
    }
}
