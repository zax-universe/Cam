package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryBlue,
    primaryContainer = PrimaryContainerBlue,
    secondary = TextMuted,
    surface = SurfaceDark,
    onSurface = TextLight,
    surfaceContainer = SurfaceContainerDark,
    background = SurfaceDark,
    onBackground = TextLight
)

@Composable
fun MotionCamTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

