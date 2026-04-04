package com.example.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val AATColorScheme = lightColorScheme(
    primary = DarkBlue,
    secondary = LightBlue,
    tertiary = DarkGray,
    background = Color.White,
    surface = Color.White,
    error = Red,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = DarkGray,
    onSurface = LightGray,
    onError = Color.White
)

@Composable
fun AtionetAndroidTerminalTheme(
    colorScheme: ColorScheme = LocalColorScheme.current,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

val LocalColorScheme = staticCompositionLocalOf { AATColorScheme }