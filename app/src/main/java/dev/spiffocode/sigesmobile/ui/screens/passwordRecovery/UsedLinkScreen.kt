package dev.spiffocode.sigesmobile.ui.screens.passwordRecovery

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

import dev.spiffocode.sigesmobile.ui.components.passwordRecovery.ErrorBaseScreen
import dev.spiffocode.sigesmobile.ui.theme.*

@Composable
fun UsedLinkScreen(
    onNavigateToLogin: () -> Unit
) {
    ErrorBaseScreen(
        icon = Icons.Outlined.Info,
        iconBgColor = Lemon,
        iconTintColor = Color(0xFFB8860B),
        title = "Enlace ya utilizado",
        description = "Este enlace de recuperación ya fue procesado anteriormente y no puede volver a usarse. Si no fuiste tú, por favor cambia tu contraseña inmediatamente.",
        buttonText = "Iniciar Sesión",
        onButtonClick = onNavigateToLogin
    )
}