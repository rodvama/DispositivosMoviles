package Database

import android.arch.persistence.room.*
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Date

@Parcelize
@Entity(tableName = "Medicion")
data class Medicion(@ColumnInfo(name = "appSistolica") var appSistolica: String?,
                    @ColumnInfo(name = "appDiastolica") var appDiastolica: String?,
                    @ColumnInfo(name = "manSistolica") var manSistolica: String?,
                    @ColumnInfo(name = "manDiastolica") var manDiastolica: String?,
                    @ColumnInfo(name = "fecha") var fecha: String?,
                    @ColumnInfo(name = "verificado") var verificado: Boolean?,
                    @ColumnInfo(name ="brazo") var brazo: String?,
                    @ColumnInfo(name = "grafica") var grafica: ByteArray?,
                    @ColumnInfo(name = "iniciales") var iniciales: String?): Parcelable {

    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var _id:Int = 0
}