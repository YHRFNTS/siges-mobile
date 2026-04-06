package dev.spiffocode.sigesmobile.ui.screens.passwordRecovery

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.spiffocode.sigesmobile.ui.components.passwordRecovery.ErrorBaseScreen
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
fun ExpiredLinkScreen(
    onNavigateToForgotPassword: () -> Unit
) {
    ErrorBaseScreen(
        icon = Icons.Outlined.Close,
        title = "Enlace caducado",
        description = "Por motivos de seguridad, los enlaces de recuperación expiran después de 15 minutos o al solicitar uno nuevo.",
        buttonText = "Solicitar nuevo enlace",
        onButtonClick = onNavigateToForgotPassword
    )
}

@Preview(showBackground = true)
@Composable
fun ExpiredLinkScreenPreview() {
    SigesmobileTheme {
        ExpiredLinkScreen {  }
    }
 }