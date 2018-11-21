package Database

import android.arch.persistence.room.*
import android.os.Parcel
import android.os.Parcelable
import java.sql.Date

@Entity(tableName = "Medicion")
data class Medicion(@ColumnInfo(name = "appSistolica") var appSistolica: String?,
                    @ColumnInfo(name = "appDiastolica") var appDiastolica: String?,
                    @ColumnInfo(name = "manSistolica") var manSistolica: String?,
                    @ColumnInfo(name = "manDiastolica") var manDiastolica: String?,
                    @ColumnInfo(name = "pulso") var pulso: String?,
                    @ColumnInfo(name = "fecha") var fecha: String?,
                    @ColumnInfo(name = "verificado") var verificado: Int?,
                    @ColumnInfo(name ="brazo") var brazo: String?,
                    @ColumnInfo(name = "grafica") var grafica: ByteArray?,
                    @ColumnInfo(name = "iniciales") var iniciales: String?): Parcelable {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var _id: Int = 0

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.createByteArray(),
            parcel.readString()) {
        _id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(appSistolica)
        parcel.writeString(appDiastolica)
        parcel.writeString(manSistolica)
        parcel.writeString(manDiastolica)
        parcel.writeString(pulso)
        parcel.writeString(fecha)
        parcel.writeValue(verificado)
        parcel.writeString(brazo)
        parcel.writeByteArray(grafica)
        parcel.writeString(iniciales)
        parcel.writeInt(_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Medicion> {
        override fun createFromParcel(parcel: Parcel): Medicion {
            return Medicion(parcel)
        }

        override fun newArray(size: Int): Array<Medicion?> {
            return arrayOfNulls(size)
        }
    }


}