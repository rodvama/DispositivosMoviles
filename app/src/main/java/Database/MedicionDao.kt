package Database

import android.arch.persistence.room.*
import android.arch.persistence.room.Dao

@Dao
interface MedicionDao {
    @Query("SELECT * FROM Medicion")
    fun cargarMeciciones(): List<Medicion>

    @Insert
    fun insertarMedicion(medicion: Medicion)

    @Delete
    fun borrarMedicion(meciones: Medicion)

    @Query("SELECT * FROM Medicion WHERE _id = :id")
    fun cargarMedicionId(id: Int): Medicion
}
