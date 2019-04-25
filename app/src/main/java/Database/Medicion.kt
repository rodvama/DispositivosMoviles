/*
    Copyright (C) 2018 - ITESM

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package Database

import android.arch.persistence.room.*
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Date

/*
 * Declares the measurement data that is stored in the database
 */
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