package dev.spiffocode.sigesmobile.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.ui.theme.SigesTheme
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme


@Composable
public fun ProfileMenu(
    isLoading: Boolean,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        ProfileMenuItem(
            title         = "Mi Perfil",
            subtitle      = "Ver y editar datos personales",
            icon          = Icons.Default.Person,
            iconBgColor   = SigesTheme.extendedColors.statusFinished,
            iconTintColor = SigesTheme.extendedColors.onStatusFinished,
            onClick       = onNavigateToEditProfile
        )

        ProfileMenuItem(
            title         = "Notificaciones",
            subtitle      = "Configurar alertas y push",
            icon          = Icons.Outlined.Notifications,
            iconBgColor   = SigesTheme.extendedColors.statusApproved,
            iconTintColor = SigesTheme.extendedColors.onStatusApproved,
            onClick       = onNavigateToNotifications
        )

        ProfileMenuItem(
            title         = "Cambiar Contraseña",
            subtitle      = "Actualizar credenciales",
            icon          = Icons.Default.Lock,
            iconBgColor   = SigesTheme.extendedColors.statusFinished,
            iconTintColor = SigesTheme.extendedColors.onStatusFinished,
            onClick       = onNavigateToChangePassword
        )

        ProfileMenuItem(
            title         = "Cerrar Sesión",
            subtitle      = "Salir de la aplicación",
            icon          = Icons.AutoMirrored.Filled.ExitToApp,
            iconBgColor   = SigesTheme.extendedColors.statusDenied,
            iconTintColor = SigesTheme.extendedColors.onStatusDenied,
            onClick       = onLogoutClick
        )
    }
}

@Preview
@Composable
fun ProfileMenuPreview(){
    SigesmobileTheme {
        ProfileMenu(
            isLoading = false
        )
    }
}


@Preview
@Composable
fun ProfileMenuLoadingPreview(){
    SigesmobileTheme {
        ProfileMenu(
            isLoading = true
        )
    }
}