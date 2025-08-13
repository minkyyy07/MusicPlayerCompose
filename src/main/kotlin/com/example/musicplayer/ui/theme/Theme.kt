package com.example.musicplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Современная палитра для музыкального плеера
object MusicPlayerColors {
    // Основные цвета
    val Purple = Color(0xFF6C63FF)
    val PurpleLight = Color(0xFF9B93FF)
    val PurpleDark = Color(0xFF4A42C7)

    // Акцентные цвета
    val Pink = Color(0xFFFF6B9D)
    val Orange = Color(0xFFFF8A65)
    val Blue = Color(0xFF42A5F5)
    val Green = Color(0xFF66BB6A)

    // Нейтральные цвета
    val DarkBackground = Color(0xFF0D0D0D)
    val DarkSurface = Color(0xFF1A1A1A)
    val DarkCard = Color(0xFF2A2A2A)
    val LightGray = Color(0xFFE0E0E0)
    val MediumGray = Color(0xFF757575)
    val DarkGray = Color(0xFF424242)

    // Градиентные цвета
    val GradientStart = Color(0xFF667eea)
    val GradientEnd = Color(0xFFf093fb)
}

private val DarkColorPalette = darkColors(
    primary = MusicPlayerColors.Purple,
    primaryVariant = MusicPlayerColors.PurpleDark,
    secondary = MusicPlayerColors.Pink,
    background = MusicPlayerColors.DarkBackground,
    surface = MusicPlayerColors.DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LightColorPalette = lightColors(
    primary = MusicPlayerColors.Purple,
    primaryVariant = MusicPlayerColors.PurpleDark,
    secondary = MusicPlayerColors.Pink,
    background = Color.White,
    surface = Color(0xFFFAFAFA),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
