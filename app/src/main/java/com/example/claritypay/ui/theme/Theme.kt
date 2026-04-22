package com.example.claritypay.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NightAccent,
    onPrimary = Night,
    secondary = Coral,
    background = Night,
    surface = NightSurface,
    onSurface = Cloud,
    onBackground = Cloud,
    outline = Slate
)

private val LightColorScheme = lightColorScheme(
    primary = Teal,
    onPrimary = SurfaceWhite,
    secondary = Coral,
    background = Cloud,
    surface = SurfaceWhite,
    onSurface = Ink,
    onBackground = Ink,
    outline = Mist
)

@Composable
fun ClarityPayTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
