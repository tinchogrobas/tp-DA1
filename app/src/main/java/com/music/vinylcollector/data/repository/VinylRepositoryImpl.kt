package com.music.vinylcollector.data.repository

import com.music.vinylcollector.data.local.VinylDao
import com.music.vinylcollector.data.local.VinylEntity
import com.music.vinylcollector.data.remote.NetworkMonitor
import com.music.vinylcollector.data.remote.VinylApiService
import com.music.vinylcollector.data.remote.VinylDto
import com.music.vinylcollector.domain.model.Vinyl
import com.music.vinylcollector.domain.repository.VinylRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación offline-first del repositorio:
 * - Lee siempre de Room (fuente de verdad local).
 * - Escribe en Room y, si hay conexión, sincroniza con el backend.
 * - Al sincronizar, trae datos remotos y actualiza Room.
 */
class VinylRepositoryImpl(
    private val dao: VinylDao,
    private val api: VinylApiService,
    private val networkMonitor: NetworkMonitor
) : VinylRepository {

    override fun getAllVinyls(): Flow<List<Vinyl>> {
        return dao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getVinylById(id: Long): Vinyl? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun insertVinyl(vinyl: Vinyl): Long {
        val localId = dao.insert(VinylEntity.fromDomain(vinyl))

        // Intenta crear en el backend si hay conexión
        if (networkMonitor.isCurrentlyOnline()) {
            runCatching {
                api.createVinyl(VinylDto.fromDomain(vinyl.copy(id = localId)))
            }
        }

        return localId
    }

    override suspend fun updateVinyl(vinyl: Vinyl) {
        dao.update(VinylEntity.fromDomain(vinyl))

        if (networkMonitor.isCurrentlyOnline()) {
            runCatching {
                api.updateVinyl(vinyl.id, VinylDto.fromDomain(vinyl))
            }
        }
    }

    override suspend fun deleteVinyl(vinyl: Vinyl) {
        dao.delete(VinylEntity.fromDomain(vinyl))

        if (networkMonitor.isCurrentlyOnline()) {
            runCatching {
                api.deleteVinyl(vinyl.id)
            }
        }
    }

    /**
     * Sincroniza con el backend: trae todos los discos remotos
     * y reemplaza el contenido local.
     */
    override suspend fun syncWithRemote() {
        if (!networkMonitor.isCurrentlyOnline()) return

        runCatching {
            val response = api.getAllVinyls()
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    val entities = dtos.map { dto ->
                        VinylEntity.fromDomain(dto.toDomain())
                    }
                    dao.deleteAll()
                    dao.insertAll(entities)
                }
            }
        }
    }
}
