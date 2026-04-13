package com.music.vinylcollector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.music.vinylcollector.ui.navigation.VinylNavGraph
import com.music.vinylcollector.ui.theme.VinylCollectorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash screen con ícono de vinilo
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val container = (application as VinylCollectorApp).container

        setContent {
            VinylCollectorTheme {
                VinylNavGraph(container = container)
            }
        }
    }
}
