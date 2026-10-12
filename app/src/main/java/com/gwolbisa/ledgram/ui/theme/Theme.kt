package com.gwolbisa.ledgram.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LedgramColorScheme = darkColorScheme(
    primary = LedAmber,
    onPrimary = Color(0xFF2A1900),
    primaryContainer = Color(0xFF493000),
    onPrimaryContainer = Color(0xFFFFDDAE),
    background = LedBlack,
    onBackground = LedCream,
    surface = LedSurface,
    onSurface = LedCream,
    surfaceVariant = LedSurfaceRaised,
    onSurfaceVariant = LedMuted,
    outline = Color(0xFF655E53),
)

@Composable
fun LEDgramTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LedgramColorScheme,
        content = content,
    )
}

