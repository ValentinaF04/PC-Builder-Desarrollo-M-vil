package com.example.pcbuilder.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Base Colors
val NeonPurple = Color(0xFFBC13FE) // Morado Neón
val NeonBlue = Color(0xFF00E5FF)   // Tu azul cyan original, conservado para contraste
val DeepViolet = Color(0xFF120024) // Fondo principal (muy oscuro)
val SurfaceViolet = Color(0xFF2A1535) // Superficies de tarjetas

// Text Colors
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFFB0B0B0)

// Gradient (Para fondos y botones)
val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(DeepViolet, Color(0xFF000000))
)

val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(NeonPurple, Color(0xFF6200EA))
)