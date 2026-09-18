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

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

private val DarkColorScheme = darkColorScheme(
    primary = GeoPrimaryDark,
    onPrimary = GeoOnPrimaryDark,
    primaryContainer = GeoPrimaryContainerDark,
    onPrimaryContainer = GeoOnPrimaryContainerDark,
    secondary = GeoSecondaryDark,
    onSecondary = GeoOnSecondaryDark,
    secondaryContainer = GeoSecondaryContainerDark,
    onSecondaryContainer = GeoOnSecondaryContainerDark,
    tertiary = GeoTertiaryDark,
    onTertiary = GeoOnTertiaryDark,
    tertiaryContainer = GeoTertiaryContainerDark,
    onTertiaryContainer = GeoOnTertiaryContainerDark,
    background = GeoBackgroundDark,
    onBackground = GeoOnBackgroundDark,
    surface = GeoSurfaceDark,
    onSurface = GeoOnSurfaceDark,
    surfaceVariant = GeoSurfaceVariantDark,
    onSurfaceVariant = GeoOnSurfaceVariantDark,
    outline = CarbonBorderDark,
    outlineVariant = CarbonBorderDark,
    error = Color(0xFFFF5449),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val LightColorScheme = lightColorScheme(
    primary = GeoPrimaryLight,
    onPrimary = GeoOnPrimaryLight,
    primaryContainer = GeoPrimaryContainerLight,
    onPrimaryContainer = GeoOnPrimaryContainerLight,
    secondary = GeoSecondaryLight,
    onSecondary = GeoOnSecondaryLight,
    secondaryContainer = GeoSecondaryContainerLight,
    onSecondaryContainer = GeoOnSecondaryContainerLight,
    tertiary = GeoTertiaryLight,
    onTertiary = GeoOnTertiaryLight,
    tertiaryContainer = GeoTertiaryContainerLight,
    onTertiaryContainer = GeoOnTertiaryContainerLight,
    background = GeoBackgroundLight,
    onBackground = GeoOnBackgroundLight,
    surface = GeoSurfaceLight,
    onSurface = GeoOnSurfaceLight,
    surfaceVariant = GeoSurfaceVariantLight,
    onSurfaceVariant = GeoOnSurfaceVariantLight,
    outline = FogEdge,
    outlineVariant = GeoOutlineLight,
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun VitalStrengthTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    },
    dynamicColor: Boolean = false,
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

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    },
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    VitalStrengthTheme(themeMode = themeMode, darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}


