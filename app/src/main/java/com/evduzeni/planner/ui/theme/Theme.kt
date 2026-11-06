package com.evduzeni.planner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = PureWhite,
    primaryContainer = LightMint,
    onPrimaryContainer = ForestInk,
    secondary = ForestInk,
    onSecondary = PureWhite,
    secondaryContainer = LightMint,
    onSecondaryContainer = ForestInk,
    background = WarmCream,
    onBackground = ForestInk,
    surface = PureWhite,
    onSurface = ForestInk,
    surfaceVariant = WarmCream,
    onSurfaceVariant = SlateMuted,
    outline = BorderLine,
    error = TerracottaOrange,
    onError = PureWhite
)

@Composable
fun EvDuzeniTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Elegant warm light aesthetic
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
