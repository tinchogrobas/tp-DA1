package com.music.vinylcollector.domain.model

/**
 * Modelo de dominio central — representa un disco de vinilo en la colección.
 * Desacoplado de Room y Retrofit para mantener Clean Architecture.
 */
data class Vinyl(
    val id: Long = 0,
    val title: String,
    val artist: String,
    val year: Int,
    val genre: Genre,
    val status: VinylStatus,
    val rating: Int, // 1–5
    val notes: String = "",
    val coverUrl: String = ""
)

enum class Genre(val displayName: String) {
    ROCK("Rock"),
    JAZZ("Jazz"),
    ELECTRONICA("Electrónica"),
    HIP_HOP("Hip-Hop"),
    POP("Pop"),
    BLUES("Blues"),
    CLASSICAL("Clásica"),
    FOLK("Folk"),
    REGGAE("Reggae"),
    METAL("Metal"),
    PUNK("Punk"),
    SOUL("Soul"),
    OTHER("Otro")
}

enum class VinylStatus(val displayName: String) {
    OWNED("Lo tengo"),
    WANTED("Lo quiero"),
    LENT("Prestado")
}
