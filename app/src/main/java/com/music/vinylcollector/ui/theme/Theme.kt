package com.music.vinylcollector.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema oscuro — modo principal de la app, inspirado en disquería nocturna
private val DarkColorScheme = darkColorScheme(
    primary = Amber,
    onPrimary = Charcoal,
    primaryContainer = AmberDark,
    onPrimaryContainer = Cream,
    secondary = WarmGray,
    onSecondary = Charcoal,
    background = Charcoal,
    onBackground = Cream,
    surface = CharcoalSurface,
    onSurface = Cream,
    surfaceVariant = CharcoalLight,
    onSurfaceVariant = CreamDark,
    error = StatusLent,
    outline = WarmGray
)

// Esquema claro — opción para quienes prefieran claridad
private val LightColorScheme = lightColorScheme(
    primary = AmberDark,
    onPrimary = LightSurface,
    primaryContainer = AmberLight,
    onPrimaryContainer = Charcoal,
    secondary = WarmGray,
    onSecondary = LightSurface,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = CreamDark,
    onSurfaceVariant = CharcoalLight,
    error = StatusLent,
    outline = WarmGray
)

@Composable
fun VinylCollectorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Ajusta la barra de estado al tema
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VinylTypography,
        content = content
    )
}
