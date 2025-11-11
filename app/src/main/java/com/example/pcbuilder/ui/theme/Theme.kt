package com.example.pcbuilder.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PCBuilderAzul,
    onPrimary = Color.White,
    background = PCBuilderNegro,
    onBackground = PCBuilderBlanco,
    surface = PCBuilderNegro,  //Fondo de las tarjetas
    onSurface = PCBuilderBlanco,    //Texto sobre las tarjetas 
    error = PCBuilderRojo,
    onError = Color.White,
    outline = PCBuilderGris,    //Borde gris 
    secondary = PCBuilderGris,
    onSecondary = PCBuilderBlanco
)

private val LightColorScheme = lightColorScheme(
    primary = PCBuilderAzul, //Para botones 
    onPrimary = Color.White,  //Texto sobre el color primario
    background = PCBuilderBlanco, //Color de fondo 
    onBackground = PCBuilderNegro, //Texto sobre el fondo
    surface = PCBuilderBlanco, //Color para tarjetas y menus
    onSurface = PCBuilderNegro, //Texto sobre superficies
    error = PCBuilderRojo, //Color para errores
    onError = Color.White, //Texto sobre el color de error
    outline = PCBuilderGris,  //Borde para Textfields
    secondary = PCBuilderGris,  //Color secundario
    onSecondary = PCBuilderNegro  //Texto sobre el color secundario
)

@Composable
fun PCBuilderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}