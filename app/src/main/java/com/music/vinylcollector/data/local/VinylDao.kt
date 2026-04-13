package com.music.vinylcollector.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO de Room — operaciones CRUD sobre la tabla vinyls.
 * Usa Flow para que la lista se actualice reactivamente.
 */
@Dao
interface VinylDao {

    @Query("SELECT * FROM vinyls ORDER BY id DESC")
    fun getAll(): Flow<List<VinylEntity>>

    @Query("SELECT * FROM vinyls WHERE id = :id")
    suspend fun getById(id: Long): VinylEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vinyl: VinylEntity): Long

    @Update
    suspend fun update(vinyl: VinylEntity)

    @Delete
    suspend fun delete(vinyl: VinylEntity)

    @Query("DELETE FROM vinyls")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vinyls: List<VinylEntity>)
}
