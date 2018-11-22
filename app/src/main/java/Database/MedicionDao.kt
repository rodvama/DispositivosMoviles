package Database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.Dao

@Dao
interface MedicionDao {
    @Query("SELECT * FROM Medicion")
    fun cargarMeciciones(): LiveData<List<Medicion>>

    @Query("SELECT COUNT (*) FROM Medicion")
    fun getAnyMedicion():Int

    @Insert
    fun insertarMedicion(medicion: Medicion)

    @Insert
    fun insertartListaMediciones(mediciones: List<Medicion>)

    @Delete
    fun borrarMedicion(meciones: Medicion)

    @Query("SELECT * FROM Medicion WHERE _id = :id")
    fun cargarMedicionId(id: Int): LiveData<Medicion>

    @Query ("UPDATE Medicion SET verificado = :verification WHERE _id = :id")
    fun updateMedicion(id: Int, verification: Boolean)
}
