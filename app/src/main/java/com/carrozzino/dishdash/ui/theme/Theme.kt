package com.carrozzino.dishdash.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val DarkColorScheme = darkColorScheme(
    background = Black90,
    surface = DarkBlue,
    surfaceVariant = DarkBlue.copy(alpha = 0.7f),
    onBackground = White70,
    primary = DarkGreen,
    onPrimary = Green,
    error = Red20,
    outline = YellowDark,

    secondary = DarkBlue,
    onSecondary = Red20
)

val LightColorScheme = lightColorScheme(
    background = White90,
    surface = LightBlue,
    surfaceVariant = LightBlue.copy(alpha = 0.7f),
    onBackground = Black90,
    error = Red90,
    outline = Yellow,


    primary = Green,
    onPrimary = DarkGreen,
    secondary = Blue,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun DishDashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}