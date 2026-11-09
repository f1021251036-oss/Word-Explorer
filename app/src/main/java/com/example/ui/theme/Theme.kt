package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = DeepForestGreen,
    onPrimary = PureWhite,
    primaryContainer = SandGold,
    onPrimaryContainer = DeepForestGreen,
    secondary = SageGreen,
    onSecondary = PureWhite,
    secondaryContainer = Cream,
    onSecondaryContainer = DeepForestGreen,
    tertiary = SandGold,
    onTertiary = DeepForestGreen,
    background = Cream,
    onBackground = DeepForestGreen,
    surface = PureWhite,
    onSurface = DeepForestGreen,
    surfaceVariant = Cream,
    onSurfaceVariant = DeepForestGreen,
    error = SemanticRed,
    onError = PureWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = SandGold,
    onPrimary = DeepForestGreen,
    primaryContainer = DeepForestGreen,
    onPrimaryContainer = Cream,
    secondary = SageGreen,
    onSecondary = PureWhite,
    secondaryContainer = DeepForestGreen,
    onSecondaryContainer = Cream,
    tertiary = Cream,
    onTertiary = DeepForestGreen,
    background = DeepForestGreen,
    onBackground = Cream,
    surface = Color(0xFF0F260A),
    onSurface = Cream,
    surfaceVariant = DeepForestGreen,
    onSurfaceVariant = Cream,
    error = SemanticRed,
    onError = PureWhite
)

@Composable
fun WordExplorerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent adventure theme branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Retain alias for template compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = WordExplorerTheme(darkTheme, dynamicColor, content)
