package mx.itesm.proyectofinal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pressure_txt.text = "140"

        measure_btn.setOnClickListener{ v -> onClick() }
    }

    fun onClick() {

    }
}
