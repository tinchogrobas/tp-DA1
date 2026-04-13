package com.music.vinylcollector

import android.app.Application
import com.music.vinylcollector.di.AppContainer

/**
 * Application class — inicializa el contenedor de DI al arrancar la app.
 */
class VinylCollectorApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
