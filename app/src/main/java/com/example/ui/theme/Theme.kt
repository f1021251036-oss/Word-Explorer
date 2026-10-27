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

private val DarkColorScheme = darkColorScheme(
    primary = AdventureOrange,
    onPrimary = Color.White,
    primaryContainer = DeepJungleGreen,
    onPrimaryContainer = ExplorerCream,
    secondary = ForestGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF285434),
    onSecondaryContainer = ExplorerCream,
    tertiary = GoldYellow,
    onTertiary = TextDark,
    background = DarkBackground,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E4637),
    onSurfaceVariant = ExplorerCream,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AdventureOrange,
    onPrimary = Color.White,
    primaryContainer = ExplorerCream,
    onPrimaryContainer = DeepJungleGreen,
    secondary = ForestGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD4EBD1),
    onSecondaryContainer = DeepJungleGreen,
    tertiary = DeepJungleGreen,
    onTertiary = Color.White,
    background = ParchmentLight,
    onBackground = TextDark,
    surface = Color.White,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFF3EEDC),
    onSurfaceVariant = TextDark,
    error = ErrorRed,
    onError = Color.White
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
