package mx.itesm.proyectofinal

import Database.Medicion

class MedicionData {
    var listaMedicion: ArrayList<Medicion> = ArrayList()

    init {
        dataList()
    }

    fun dataList() {
        listaMedicion.add(Medicion("120", "80", "115", "64", "29", "18/04/2018", 0, "I", null, "JMP"))
        listaMedicion.add(Medicion("120", "80", "115", "64", "29", "18/04/2018", 1, "I", null, "GV"))
        listaMedicion.add(Medicion("120", "80", "115", "64", "29", "18/04/2018", 0, "D", null, "RV"))
    }
}