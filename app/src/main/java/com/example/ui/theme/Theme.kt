package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueDarkTheme,
    primaryContainer = PrimaryBlueDarkContainer,
    onPrimaryContainer = PrimaryBlueDarkOnContainer,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = CardBackgroundDark,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF94A3B8), // Slate 400
    tertiaryContainer = GreenBackgroundDark,
    onTertiaryContainer = GreenTextDark,
    errorContainer = FlameBackgroundDark,
    onErrorContainer = FlameTextDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = PrimaryBlueDark,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = CardBackground,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF64748B), // Slate 500
    tertiaryContainer = GreenBackground,
    onTertiaryContainer = GreenText,
    errorContainer = FlameBackground,
    onErrorContainer = FlameText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
