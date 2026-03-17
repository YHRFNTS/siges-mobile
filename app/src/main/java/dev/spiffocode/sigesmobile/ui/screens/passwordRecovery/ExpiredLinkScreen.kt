package dev.spiffocode.sigesmobile.ui.screens.passwordRecovery

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable

import dev.spiffocode.sigesmobile.ui.components.passwordRecovery.ErrorBaseScreen
import dev.spiffocode.sigesmobile.ui.theme.*

@Composable
fun ExpiredLinkScreen(
    onNavigateToForgotPassword: () -> Unit
) {
    ErrorBaseScreen(
        icon = Icons.Outlined.Close,
        iconBgColor = Rose,
        iconTintColor = Coral,
        title = "Enlace caducado",
        description = "Por motivos de seguridad, los enlaces de recuperación expiran después de 24 horas o al solicitar uno nuevo.",
        buttonText = "Solicitar nuevo enlace",
        onButtonClick = onNavigateToForgotPassword
    )
}