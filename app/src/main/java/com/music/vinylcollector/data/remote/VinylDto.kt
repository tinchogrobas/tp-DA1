package com.music.vinylcollector.data.remote

import com.google.gson.annotations.SerializedName
import com.music.vinylcollector.domain.model.Genre
import com.music.vinylcollector.domain.model.Vinyl
import com.music.vinylcollector.domain.model.VinylStatus

/**
 * DTO para serialización/deserialización JSON con el backend.
 * Campos con @SerializedName para mapear a snake_case del API.
 */
data class VinylDto(
    val id: Long? = null,
    val title: String,
    val artist: String,
    val year: Int,
    val genre: String,
    val status: String,
    val rating: Int,
    val notes: String = "",
    @SerializedName("cover_url")
    val coverUrl: String = ""
) {
    fun toDomain() = Vinyl(
        id = id ?: 0,
        title = title,
        artist = artist,
        year = year,
        genre = runCatching { Genre.valueOf(genre) }.getOrDefault(Genre.OTHER),
        status = runCatching { VinylStatus.valueOf(status) }.getOrDefault(VinylStatus.OWNED),
        rating = rating.coerceIn(1, 5),
        notes = notes,
        coverUrl = coverUrl
    )

    companion object {
        fun fromDomain(vinyl: Vinyl) = VinylDto(
            id = if (vinyl.id == 0L) null else vinyl.id,
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
