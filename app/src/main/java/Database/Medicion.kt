package Database

import android.arch.persistence.room.*
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "Medicion")
data class Medicion(@ColumnInfo(name = "appSistolica") var appSistolica: String?,
                    @ColumnInfo(name = "appDiastolica") var appDiastolica: String?,
                    @ColumnInfo(name = "manSistolica") var manSistolica: String?,
                    @ColumnInfo(name = "manDiastolica") var manDiastolica: String?,
                    @ColumnInfo(name = "pulso") var pulso: String?,
                    @ColumnInfo(name = "fecha") var fecha: String?,
                    @ColumnInfo(name = "verificado") var verificado: Boolean?,
                    @ColumnInfo(name ="brazo") var brazo: String?,
                    @ColumnInfo(name = "grafica") var grafica: ByteArray?): Parcelable {
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
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.createByteArray()) {
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