package Database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface PacienteDao {

    @Query("SELECT * FROM Patient")
    fun cargarPacientes(): LiveData<List<Patient>>

    @Query("SELECT COUNT (*) FROM Patient")
    fun getAnyPaciente():Int

    @Insert
    fun insertarPaciente(paciente: Patient)

    @Insert
    fun insertartListaPacientes(pacientes: List<Patient>)

    @Query("DELETE FROM Patient WHERE _idP = :id")
    fun borrarPaciente(id: Int)

    @Query("SELECT * FROM Patient WHERE _idP = :id")
    fun cargarPacienteId(id: Int): LiveData<Patient>

    //Cargar medicion por Id devuelve una medicion
    @Query("SELECT * FROM Patient WHERE _idP = :id")
    fun cargarPacientePorId(id: Int): Patient

    @Query("UPDATE Patient SET clinic = :clinica WHERE _idP = :id")
    fun updatePaciente(id: Int, clinica: String)
}