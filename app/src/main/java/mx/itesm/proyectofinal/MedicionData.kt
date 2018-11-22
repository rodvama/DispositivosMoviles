package mx.itesm.proyectofinal

import Database.Medicion
import android.content.Context

class MedicionData(val context: Context) {

    var listaMedicion: ArrayList<Medicion> = ArrayList()

    init {
        dataList()
    }

    fun dataList() {
        listaMedicion.add(Medicion("121",
                "81",
                "116",
                "64",
                "08/12/2018",
                true,
                "I",
                null,
                "JMP"))
        listaMedicion.add(Medicion("122",
                "82",
                "117",
                "65",
                "12/03/2018",
                false,
                "I",
                null,
                "GV"))
        listaMedicion.add(Medicion("123",
                "82",
                "118",
                "66",
                "04/06/2018",
                true,
                "D",
                null,
                "RV"))
    }
}