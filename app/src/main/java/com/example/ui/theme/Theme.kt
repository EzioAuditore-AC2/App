package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val NeuroTalksLightColorScheme = lightColorScheme(
    primary = ForestPrimary,
    onPrimary = CreamSurface,
    primaryContainer = SageContainer,
    onPrimaryContainer = ForestDeep,
    secondary = MossAccent,
    onSecondary = CreamSurface,
    secondaryContainer = SageSoft,
    onSecondaryContainer = ForestDeep,
    tertiary = ClayWarm,
    onTertiary = CreamSurface,
    tertiaryContainer = ClayContainer,
    onTertiaryContainer = InkPrimary,
    background = CreamBackground,
    onBackground = InkPrimary,
    surface = CreamSurface,
    onSurface = InkPrimary,
    surfaceVariant = OatSurface,
    onSurfaceVariant = InkSecondary,
    surfaceContainerLowest = CreamSurface,
    surfaceContainerLow = CreamBackground,
    surfaceContainer = OatSurface,
    surfaceContainerHigh = OatElevated,
    outline = SageBorder,
    outlineVariant = OatElevated,
    error = CrisisCrimson,
    onError = Color.White,
    errorContainer = CrisisContainer,
    onErrorContainer = CrisisOnContainer
)

private val NeuroTalksDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = SageMuted,
    onSecondary = DarkOnPrimary,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = DarkOnSurface,
    tertiary = AmberSoft,
    onTertiary = DarkBackground,
    tertiaryContainer = Color(0xFF4A3525),
    onTertiaryContainer = CreamBackground,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurfaceVariant,
    surfaceContainerHigh = Color(0xFF2A4A38),
    outline = Color(0xFF476956),
    outlineVariant = Color(0xFF2E4C3B),
    error = Color(0xFFF28B82),
    onError = Color(0xFF5D1D16),
    errorContainer = Color(0xFF53231E),
    onErrorContainer = Color(0xFFFCE8E5)
)

val NeuroTalksShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun NeuroTalksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Always preserve NeuroTalks' distinctive Dark Forest Green + Warm Cream brand identity
    val colorScheme = if (darkTheme) NeuroTalksDarkColorScheme else NeuroTalksLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = NeuroTalksShapes,
        content = content
    )
}
