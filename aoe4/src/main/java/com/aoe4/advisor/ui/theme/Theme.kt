package com.aoe4.advisor.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Aoe4DarkColors = darkColorScheme(
    primary = Gold,
    onPrimary = Color(0xFF1A1206),
    secondary = GoldDim,
    onSecondary = Color(0xFF1A1206),
    background = DarkBg,
    onBackground = OnDark,
    surface = DarkSurface,
    onSurface = OnDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkMuted,
    outline = OutlineBrown,
    outlineVariant = Color(0xFF3A3122),
)

@Composable
fun Aoe4AdvisorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Aoe4DarkColors,
        typography = Typography(),
        content = content
    )
}
