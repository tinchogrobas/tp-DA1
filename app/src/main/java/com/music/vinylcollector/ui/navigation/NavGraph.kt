package com.music.vinylcollector.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.music.vinylcollector.di.AppContainer
import com.music.vinylcollector.ui.screens.about.AboutScreen
import com.music.vinylcollector.ui.screens.collection.CollectionScreen

/**
 * Grafo de navegación — define las rutas y transiciones entre pantallas.
 */
object Routes {
    const val COLLECTION = "collection"
    const val ABOUT = "about"
}

@Composable
fun VinylNavGraph(container: AppContainer) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.COLLECTION,
        // Transiciones suaves entre pantallas
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 } },
        exitTransition = { fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 4 } },
        popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 4 } },
        popExitTransition = { fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it / 4 } }
    ) {
        composable(Routes.COLLECTION) {
            CollectionScreen(
                container = container,
                onNavigateToAbout = { navController.navigate(Routes.ABOUT) }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
