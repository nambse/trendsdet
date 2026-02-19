package com.example.trend_sdet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TrendyolOrange,
    onPrimary = Color.White,
    primaryContainer = TrendyolOrangeLight,
    onPrimaryContainer = TrendyolOrangeDark,
    secondary = WarmSecondary,
    onSecondary = OnWarmSecondary,
    secondaryContainer = WarmSecondaryContainer,
    onSecondaryContainer = OnWarmSecondaryContainer,
    tertiary = WarmTertiary,
    onTertiary = OnWarmTertiary,
    tertiaryContainer = WarmTertiaryContainer,
    onTertiaryContainer = OnWarmTertiaryContainer,
    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = ErrorRedContainer,
    onErrorContainer = OnErrorRedContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    surfaceDim = LightSurfaceDim,
    surfaceBright = LightSurfaceBright,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary,
    scrim = LightScrim,
    surfaceTint = LightSurfaceTint,
)

private val DarkColorScheme = darkColorScheme(
    primary = TrendyolOrange,
    onPrimary = Color.White,
    primaryContainer = TrendyolOrangeDark,
    onPrimaryContainer = TrendyolOrangeLight,
    secondary = WarmSecondary,
    onSecondary = OnWarmSecondary,
    secondaryContainer = WarmSecondaryContainer,
    onSecondaryContainer = OnWarmSecondaryContainer,
    tertiary = WarmTertiary,
    onTertiary = OnWarmTertiary,
    tertiaryContainer = WarmTertiaryContainer,
    onTertiaryContainer = OnWarmTertiaryContainer,
    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = ErrorRedContainer,
    onErrorContainer = OnErrorRedContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    surfaceDim = DarkSurfaceDim,
    surfaceBright = DarkSurfaceBright,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary,
    scrim = DarkScrim,
    surfaceTint = DarkSurfaceTint,
)

@Composable
fun TrendSdetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
