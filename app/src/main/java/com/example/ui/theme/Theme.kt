package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = IslamicGreenDark,
    onPrimary = IslamicGreenOnDark,
    primaryContainer = IslamicContainerDark,
    onPrimaryContainer = IslamicOnContainerDark,
    secondary = GoldAccentDark,
    onSecondary = IslamicGreenOnDark,
    secondaryContainer = GoldContainerDark,
    onSecondaryContainer = GoldOnContainerDark,
    tertiary = EarthGreenDarkTertiary,
    onTertiary = IslamicGreenOnDark,
    background = IslamicBackgroundDark,
    surface = IslamicSurfaceDark,
    surfaceVariant = IslamicSurfaceVariantDark,
    onBackground = IslamicOnSurfaceDark,
    onSurface = IslamicOnSurfaceDark,
    onSurfaceVariant = IslamicOnSurfaceVariantDark,
    outline = IslamicOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = IslamicGreenLight,
    onPrimary = IslamicGreenOnLight,
    primaryContainer = IslamicContainerLight,
    onPrimaryContainer = IslamicOnContainerLight,
    secondary = GoldAccentLight,
    onSecondary = IslamicGreenOnLight,
    secondaryContainer = GoldContainerLight,
    onSecondaryContainer = GoldOnContainerLight,
    tertiary = EarthGreenTertiary,
    onTertiary = IslamicGreenOnLight,
    background = IslamicBackgroundLight,
    surface = IslamicSurfaceLight,
    surfaceVariant = IslamicSurfaceVariantLight,
    onBackground = IslamicOnSurfaceLight,
    onSurface = IslamicOnSurfaceLight,
    onSurfaceVariant = IslamicOnSurfaceVariantLight,
    outline = IslamicOutlineLight
)

@Composable
fun DeenMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for backwards compatibility if needed
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    DeenMateTheme(darkTheme = darkTheme, content = content)
}

