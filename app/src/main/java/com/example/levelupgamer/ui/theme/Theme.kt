package com.example.levelupgamer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LevelUpGamerColorScheme = darkColorScheme(
    primary = AzulElectrico,
    onPrimary = Blanco,
    primaryContainer = AzulElectricoOscuro,
    onPrimaryContainer = Blanco,
    secondary = VerdeNeon,
    onSecondary = Negro,
    secondaryContainer = VerdeNeonOscuro,
    onSecondaryContainer = Blanco,
    tertiary = VerdeNeon,
    onTertiary = Negro,
    background = Negro,
    onBackground = Blanco,
    surface = GrisOscuro,
    onSurface = Blanco,
    surfaceVariant = GrisOscuro,
    onSurfaceVariant = GrisClaro,
    error = Rojo,
    onError = Blanco,
    outline = GrisClaro,
    outlineVariant = GrisOscuro
)

@Composable
fun LevelUpGamerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LevelUpGamerColorScheme,
        typography = Typography,
        content = content
    )
}