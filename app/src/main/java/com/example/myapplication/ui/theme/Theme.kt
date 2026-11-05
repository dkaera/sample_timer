package com.example.myapplication.ui.theme

import android.app.Activity
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
    primary = TomatoPrimaryDark,
    onPrimary = TomatoOnPrimaryDark,
    primaryContainer = TomatoPrimaryContainerDark,
    onPrimaryContainer = TomatoOnPrimaryContainerDark,
    secondary = TomatoSecondaryDark,
    onSecondary = TomatoOnSecondaryDark,
    secondaryContainer = TomatoSecondaryContainerDark,
    onSecondaryContainer = TomatoOnSecondaryContainerDark,
    tertiary = TomatoTertiaryDark,
    onTertiary = TomatoOnTertiaryDark,
    tertiaryContainer = TomatoTertiaryContainerDark,
    onTertiaryContainer = TomatoOnTertiaryContainerDark,
    background = TomatoBackgroundDark,
    onBackground = TomatoOnBackgroundDark,
    surface = TomatoSurfaceDark,
    onSurface = TomatoOnSurfaceDark,
    surfaceVariant = TomatoSurfaceVariantDark,
    onSurfaceVariant = TomatoOnSurfaceVariantDark,
    outline = TomatoOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = TomatoPrimaryLight,
    onPrimary = TomatoOnPrimaryLight,
    primaryContainer = TomatoPrimaryContainerLight,
    onPrimaryContainer = TomatoOnPrimaryContainerLight,
    secondary = TomatoSecondaryLight,
    onSecondary = TomatoOnSecondaryLight,
    secondaryContainer = TomatoSecondaryContainerLight,
    onSecondaryContainer = TomatoOnSecondaryContainerLight,
    tertiary = TomatoTertiaryLight,
    onTertiary = TomatoOnTertiaryLight,
    tertiaryContainer = TomatoTertiaryContainerLight,
    onTertiaryContainer = TomatoOnTertiaryContainerLight,
    background = TomatoBackgroundLight,
    onBackground = TomatoOnBackgroundLight,
    surface = TomatoSurfaceLight,
    onSurface = TomatoOnSurfaceLight,
    surfaceVariant = TomatoSurfaceVariantLight,
    onSurfaceVariant = TomatoOnSurfaceVariantLight,
    outline = TomatoOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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