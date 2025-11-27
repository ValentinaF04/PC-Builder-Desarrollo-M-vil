package com.example.pcbuilder.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CyberColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    secondary = NeonBlue,
    onSecondary = Color.Black,
    background = DeepViolet,
    onBackground = TextWhite,
    surface = SurfaceViolet, // Color base de las tarjetas
    onSurface = TextWhite,
    error = Color(0xFFCF6679),
    outline = NeonBlue
)

@Composable
fun PCBuilderTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Hacemos la barra de estado del mismo color que el fondo profundo
            window.statusBarColor = DeepViolet.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}