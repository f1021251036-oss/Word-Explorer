package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
data class ExpeditionColors(
    val canvasLight: Color = Cream,
    val surfaceElevated: Color = PureWhite,
    val surfaceSoft: Color = CreamSoft,
    val canvasDark: Color = Forest700,
    val surfaceDarkElevated: Color = Forest900,
    val primaryInk: Color = Forest900,
    val secondaryInk: Color = SageDark,
    val darkMutedInk: Color = SageLight,
    val ctaPrimary: Color = Honey,
    val ctaPrimaryHover: Color = HoneyHover,
    val ctaPrimaryPressed: Color = HoneyPressed,
    val ctaPrimaryContent: Color = Forest900,
    val ctaSecondary: Color = Sand,
    val ctaSecondaryHover: Color = SandHover,
    val ctaSecondaryPressed: Color = SandPressed,
    val success: Color = Leaf,
    val successOnDark: Color = LeafBright,
    val successTint: Color = LeafTint,
    val error: Color = Terracotta,
    val errorOnDark: Color = TerracottaBright,
    val errorTint: Color = TerracottaTint,
    val info: Color = Lagoon,
    val infoOnDark: Color = LagoonBright,
    val infoTint: Color = LagoonTint,
    val borderLight: Color = Sage,
    val borderDark: Color = DarkBorderSubtle,
    val disabledBg: Color = DisabledBg,
    val disabledFg: Color = DisabledFg
)

val LocalExpeditionColors = staticCompositionLocalOf { ExpeditionColors() }

val MaterialTheme.expeditionColors: ExpeditionColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExpeditionColors.current

private val LightColorScheme = lightColorScheme(
    primary = Forest700,
    onPrimary = PureWhite,
    primaryContainer = Sand,
    onPrimaryContainer = Forest900,
    secondary = Forest500,
    onSecondary = PureWhite,
    secondaryContainer = CreamSoft,
    onSecondaryContainer = Forest900,
    tertiary = Honey,
    onTertiary = Forest900,
    background = Cream,
    onBackground = Forest900,
    surface = PureWhite,
    onSurface = Forest900,
    surfaceVariant = CreamSoft,
    onSurfaceVariant = SageDark,
    surfaceContainer = PureWhite,
    surfaceContainerHigh = PureWhite,
    outline = Sage,
    outlineVariant = SageDark,
    error = Terracotta,
    onError = PureWhite,
    errorContainer = TerracottaTint,
    onErrorContainer = Terracotta
)

private val DarkColorScheme = darkColorScheme(
    primary = Honey,
    onPrimary = Forest900,
    primaryContainer = Forest500,
    onPrimaryContainer = Cream,
    secondary = Sand,
    onSecondary = Forest900,
    secondaryContainer = Forest900,
    onSecondaryContainer = Cream,
    tertiary = LagoonBright,
    onTertiary = Forest900,
    background = Forest700,
    onBackground = Cream,
    surface = Forest900,
    onSurface = Cream,
    surfaceVariant = Forest500,
    onSurfaceVariant = SageLight,
    surfaceContainer = Forest900,
    surfaceContainerHigh = Forest500,
    outline = DarkBorderSubtle,
    outlineVariant = Sage,
    error = TerracottaBright,
    onError = Forest900,
    errorContainer = Terracotta,
    onErrorContainer = PureWhite
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

    CompositionLocalProvider(LocalExpeditionColors provides ExpeditionColors()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Retain alias for template compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = WordExplorerTheme(darkTheme, dynamicColor, content)
