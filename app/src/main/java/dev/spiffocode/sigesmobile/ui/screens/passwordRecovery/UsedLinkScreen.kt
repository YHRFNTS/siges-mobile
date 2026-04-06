package dev.spiffocode.sigesmobile.ui.screens.passwordRecovery

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.spiffocode.sigesmobile.ui.components.passwordRecovery.ErrorBaseScreen
import dev.spiffocode.sigesmobile.ui.theme.SigesTheme
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
fun UsedLinkScreen(
    onNavigateToLogin: () -> Unit
) {
    ErrorBaseScreen(
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
        UsedLinkScreen {  }
    }
}