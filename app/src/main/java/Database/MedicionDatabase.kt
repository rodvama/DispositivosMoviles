package Database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Medicion::class], version = 1, exportSchema = false)
abstract class MedicionDatabase : RoomDatabase() {

    abstract fun medicionDao(): MedicionDao

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