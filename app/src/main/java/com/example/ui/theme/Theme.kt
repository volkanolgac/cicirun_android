package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameColorScheme = lightColorScheme(
    primary = CiciYellow,
    onPrimary = CiciDark,
    primaryContainer = CiciOrange,
    onPrimaryContainer = Color.White,
    secondary = CiciSkyDark,
    onSecondary = Color.White,
    secondaryContainer = CiciSkyLight,
    onSecondaryContainer = CiciDark,
    tertiary = CiciCoinGold,
    background = CiciDark,
    onBackground = CiciTextLight,
    surface = CiciCardBg,
    onSurface = CiciTextLight,
    error = CiciRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GameColorScheme,
        typography = Typography,
        content = content
    )
}
