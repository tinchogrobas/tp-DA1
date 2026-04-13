package com.music.vinylcollector.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.music.vinylcollector.domain.model.Genre
import com.music.vinylcollector.domain.model.Vinyl
import com.music.vinylcollector.domain.model.VinylStatus

/**
 * Entidad Room — tabla "vinyls" en SQLite.
 * Los enums se almacenan como strings via sus nombres.
 */
@Entity(tableName = "vinyls")
data class VinylEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String,
    val year: Int,
    val genre: String,
    val status: String,
    val rating: Int,
    val notes: String,
    val coverUrl: String,
    val remoteId: Long? = null // ID del backend para sincronización
) {
    fun toDomain() = Vinyl(
        id = id,
        title = title,
        artist = artist,
        year = year,
        genre = Genre.valueOf(genre),
        status = VinylStatus.valueOf(status),
        rating = rating,
        notes = notes,
        coverUrl = coverUrl
    )

    companion object {
        fun fromDomain(vinyl: Vinyl) = VinylEntity(
            id = vinyl.id,
            title = vinyl.title,
            artist = vinyl.artist,
            year = vinyl.year,
            genre = vinyl.genre.name,
            status = vinyl.status.name,
            rating = vinyl.rating,
            notes = vinyl.notes,
            coverUrl = vinyl.coverUrl
        )
    }
}
