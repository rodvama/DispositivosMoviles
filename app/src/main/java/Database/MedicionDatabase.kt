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

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/*
 * Declares the entity of the Measurement database based on a Singleton design pattern.
 */
@Database(entities = [Medicion::class , Patient::class], version = 1, exportSchema = false)
abstract class MedicionDatabase : RoomDatabase() {

    abstract fun medicionDao(): MedicionDao
    abstract fun pacienteDao(): PacienteDao

    companion object {

        private const val DATABASE_NAME = "MedicionDB.db"
        private var dbInstance: MedicionDatabase? = null

        @Synchronized
        fun getInstance(context: Context): MedicionDatabase {
            if (dbInstance == null) {
                dbInstance = buildDatabase(context)
            }
            return dbInstance!!
        }

        private fun buildDatabase(context: Context): MedicionDatabase {
            return Room.databaseBuilder(context,
                    MedicionDatabase::class.java,
                    DATABASE_NAME)
                    .build()
        }
    }
}