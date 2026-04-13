package com.music.vinylcollector.domain.repository

import com.music.vinylcollector.domain.model.Vinyl
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio — la capa de UI solo conoce esta interfaz.
 * La implementación concreta decide si los datos vienen de Room o Retrofit.
 */
interface VinylRepository {
    fun getAllVinyls(): Flow<List<Vinyl>>
    suspend fun getVinylById(id: Long): Vinyl?
    suspend fun insertVinyl(vinyl: Vinyl): Long
    suspend fun updateVinyl(vinyl: Vinyl)
    suspend fun deleteVinyl(vinyl: Vinyl)
    suspend fun syncWithRemote()
}
