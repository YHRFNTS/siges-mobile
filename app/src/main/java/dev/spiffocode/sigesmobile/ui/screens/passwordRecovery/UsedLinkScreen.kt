package dev.spiffocode.sigesmobile.ui.screens.passwordRecovery

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.spiffocode.sigesmobile.ui.components.passwordRecovery.ErrorBaseScreen
import dev.spiffocode.sigesmobile.ui.theme.SigesTheme
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Composable
fun UsedLinkScreen(
    windowSizeClass: WindowSizeClass,
    onNavigateToLogin: () -> Unit
) {
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    UsedLinkContent(isCompact = isCompact, onNavigateToLogin = onNavigateToLogin)
}

@Composable
fun UsedLinkContent(
    isCompact: Boolean,
    onNavigateToLogin: () -> Unit
) {
    ErrorBaseScreen(
        isCompact = isCompact,
        icon = Icons.Outlined.Info,
        iconBgColor = SigesTheme.extendedColors.statusPending,
        iconTintColor = SigesTheme.extendedColors.onStatusPending,
        title = "Enlace ya utilizado",
        description = "Este enlace de recuperación ya fue procesado anteriormente y no puede volver a usarse. Si no fuiste tú, por favor cambia tu contraseña inmediatamente.",
        buttonText = "Iniciar Sesión",
        onButtonClick = onNavigateToLogin
    )
}

@Preview(showBackground = true)
@Composable
fun UsedLinkScreenPreview() {
    SigesmobileTheme {
        UsedLinkContent(isCompact = true) {  }
    }
}