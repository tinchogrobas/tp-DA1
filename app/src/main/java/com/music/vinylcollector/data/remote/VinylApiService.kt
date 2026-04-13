package com.music.vinylcollector.data.remote

import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz Retrofit — define los endpoints REST del backend.
 * Usa Response<> para manejar errores HTTP sin excepciones.
 */
interface VinylApiService {

    @GET("discos")
    suspend fun getAllVinyls(): Response<List<VinylDto>>

    @GET("discos/{id}")
    suspend fun getVinylById(@Path("id") id: Long): Response<VinylDto>

    @POST("discos")
    suspend fun createVinyl(@Body vinyl: VinylDto): Response<VinylDto>

    @PUT("discos/{id}")
    suspend fun updateVinyl(@Path("id") id: Long, @Body vinyl: VinylDto): Response<VinylDto>

    @DELETE("discos/{id}")
    suspend fun deleteVinyl(@Path("id") id: Long): Response<Unit>
}
