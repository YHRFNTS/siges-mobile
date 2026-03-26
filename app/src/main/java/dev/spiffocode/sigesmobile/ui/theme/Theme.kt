package dev.spiffocode.sigesmobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary        = Purple40,
    onPrimary      = Color.White,
    primaryContainer   = Lav,
    onPrimaryContainer = deepBlue,
    secondary        = TealLight,
    onSecondary      = Color.White,
    secondaryContainer = Mint,
    onSecondaryContainer = Color(0xFF00201C),
    surfaceVariant   = Lemon,
    error          = Coral,
    errorContainer   = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    surface        = SurfaceLight,
    background       = Color(0xFFFFFFFF),
    onBackground     = Color(0xFF1C1B1F),
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline          = Color(0xFFCAC4D0)
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Color(0xFF381E72),
    primaryContainer = LavDark,
    onPrimaryContainer = Color(0xFFE8DFF5),

    secondary = TealDark,
    onSecondary = Color(0xFF00382F),
    secondaryContainer = Color(0xFF1F4F46),
    onSecondaryContainer = Color(0xFFD4F1E8),

    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = LemonDark,
    onSurfaceVariant = TextSecondaryDark,

    background = Color(0xFF121212),
    onBackground = TextPrimaryDark,

    error = Coral,
    errorContainer = Color(0xFF8C1D18),
    onError = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF938F99)
)

// ui/theme/SigesExtendedColors.kt (mismo archivo)

val LightExtendedColors = SigesExtendedColors(
    // Estados
    statusPending    = Color(0xFFFFF3CD),
    onStatusPending  = Color(0xFFF5A623),
    statusApproved   = Color(0xFFD4EDDA),
    onStatusApproved = Color(0xFF2E7D32),
    statusDenied     = Color(0xFFFFDAD6),
    onStatusDenied   = Color(0xFFE53935),
    statusCancelled  = Color(0xFFEEEEEE),
    onStatusCancelled= Color(0xFF757575),
    statusFinished   = Color(0xFFE8EAF6),
    onStatusFinished = Color(0xFF3949AB),

    // Disponibilidad
    available   = Color(0xFF2E7D32),
    maintenance = Color(0xFFE53935),

    // Quick actions
    quickActionSearch   = Color(0xFFEDE7F6),  // lila claro
    quickActionNew      = Color(0xFFE8F5E9),  // verde menta claro
    quickActionHistory  = Color(0xFFFFEBEE),  // rosa claro
    quickActionCalendar = Color(0xFFFFFDE7),  // amarillo claro
)

val DarkExtendedColors = SigesExtendedColors(
    // Estados → más oscuros y menos saturados para dark
    statusPending    = Color(0xFF3D2E00),
    onStatusPending  = Color(0xFFFFB74D),
    statusApproved   = Color(0xFF1B3A1F),
    onStatusApproved = Color(0xFF81C784),
    statusDenied     = Color(0xFF3E1212),
    onStatusDenied   = Color(0xFFEF9A9A),
    statusCancelled  = Color(0xFF2C2C2C),
    onStatusCancelled= Color(0xFFBDBDBD),
    statusFinished   = Color(0xFF1A1F3A),
    onStatusFinished = Color(0xFF7986CB),

    // Disponibilidad
    available   = Color(0xFF81C784),
    maintenance = Color(0xFFEF9A9A),

    // Quick actions → versiones oscuras
    quickActionSearch   = Color(0xFF2D1F5E),
    quickActionNew      = Color(0xFF1B3A1F),
    quickActionHistory  = Color(0xFF3E1212),
    quickActionCalendar = Color(0xFF3D2E00),
)

val LocalSigesExtendedColors = staticCompositionLocalOf {
    LightExtendedColors
}

object SigesTheme {
    val extendedColors: SigesExtendedColors
        @Composable
        get() = LocalSigesExtendedColors.current
}

@Composable
fun SigesmobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors


    CompositionLocalProvider(
        LocalSigesExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = shapes,
            typography = Typography,
            content = content
        )
    }
}