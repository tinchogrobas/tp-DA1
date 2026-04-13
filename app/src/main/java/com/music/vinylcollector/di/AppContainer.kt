package com.music.vinylcollector.di

import android.content.Context
import com.music.vinylcollector.BuildConfig
import com.music.vinylcollector.data.local.VinylDatabase
import com.music.vinylcollector.data.remote.NetworkMonitor
import com.music.vinylcollector.data.remote.VinylApiService
import com.music.vinylcollector.data.repository.VinylRepositoryImpl
import com.music.vinylcollector.domain.repository.VinylRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Contenedor de dependencias manual — sin Hilt/Dagger para simplicidad.
 * Construye e inyecta todas las dependencias en orden.
 */
class AppContainer(context: Context) {

    // Red
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: VinylApiService = retrofit.create(VinylApiService::class.java)

    // Base de datos local
    private val database = VinylDatabase.getInstance(context)
    val vinylDao = database.vinylDao()

    // Monitor de red
    val networkMonitor = NetworkMonitor(context)

    // Repositorio — punto de acceso único a los datos
    val vinylRepository: VinylRepository = VinylRepositoryImpl(
        dao = vinylDao,
        api = apiService,
        networkMonitor = networkMonitor
    )
}
