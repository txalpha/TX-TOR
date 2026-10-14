package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TxTorDarkColorScheme = darkColorScheme(
    primary = TxTorPrimary,
    onPrimary = TxTorBackground,
    primaryContainer = TxTorPrimaryVariant,
    onPrimaryContainer = TxTorTextPrimary,
    secondary = TxTorSecondary,
    onSecondary = TxTorBackground,
    secondaryContainer = TxTorSurfaceElevated,
    onSecondaryContainer = TxTorTextPrimary,
    tertiary = TxTorCyan,
    background = TxTorBackground,
    onBackground = TxTorTextPrimary,
    surface = TxTorSurface,
    onSurface = TxTorTextPrimary,
    surfaceVariant = TxTorSurfaceElevated,
    onSurfaceVariant = TxTorTextSecondary,
    outline = TxTorGlassBorder,
    error = TxTorRed,
    onError = TxTorTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = TxTorDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = TxTorBackground.toArgb()
                window.navigationBarColor = TxTorBackground.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
