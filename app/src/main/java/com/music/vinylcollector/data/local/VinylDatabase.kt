package com.music.vinylcollector.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos Room — singleton para evitar múltiples instancias de conexión.
 */
@Database(entities = [VinylEntity::class], version = 1, exportSchema = false)
abstract class VinylDatabase : RoomDatabase() {

    abstract fun vinylDao(): VinylDao

    companion object {
        @Volatile
        private var INSTANCE: VinylDatabase? = null

        fun getInstance(context: Context): VinylDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    VinylDatabase::class.java,
                    "vinyl_collector.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
