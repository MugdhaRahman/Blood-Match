package com.mrapps.bloodmatch.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BloodRedDarkTheme,
    onPrimary = BloodOnPrimary,
    primaryContainer = BloodRedDark,
    secondary = BloodRedLight,
    background = BloodBackgroundDark,
    surface = BloodSurfaceDark,
    onSurfaceVariant = HintDark
)

private val LightColorScheme = lightColorScheme(
    primary = BloodRed,
    onPrimary = BloodOnPrimary,
    primaryContainer = BloodRedDark,
    secondary = BloodRedDark,
    tertiary = BloodRedLight,
    background = BloodBackground,
    surface = BloodSurface,
    onSurfaceVariant = HintLight
)

@Composable
fun BloodMatchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
