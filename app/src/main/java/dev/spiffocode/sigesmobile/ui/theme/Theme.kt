package dev.spiffocode.sigesmobile.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary        = Teal,
    onPrimary      = Color.White,
    primaryContainer   = Mint,
    onPrimaryContainer = TealLight,
    secondary        = Plum,
    secondaryContainer = Lav,
    onSecondaryContainer = Sky,
    tertiary         = Color(0xFFB85C38),
    tertiaryContainer = Peach,
    surfaceVariant   = Lemon,
    error          = Coral,
    surface        = SurfaceLight,
    background     = Color.White,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

private val DarkColorScheme = darkColorScheme(
    primary        = TealDark,
    onPrimary      = Color.Black,
    primaryContainer   = Color(0xFF0F6E56),
    onPrimaryContainer = Mint,
    secondary        = Color(0xFF987AF5),
    secondaryContainer = LavDark,
    tertiary         = Color(0xFFE88966),
    tertiaryContainer = PeachDark,
    surfaceVariant   = LemonDark,
    error          = Color(0xFFFFB4AB),
    surface        = SurfaceDark,
    background     = Color(0xFF121212),
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark
)

@Composable
fun SigesmobileTheme(
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
        shapes = shapes,
        typography = Typography,
        content = content
    )
}