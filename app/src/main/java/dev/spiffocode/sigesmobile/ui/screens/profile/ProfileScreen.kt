package dev.spiffocode.sigesmobile.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import dev.spiffocode.sigesmobile.ui.components.profile.ProfileMenuItem
import dev.spiffocode.sigesmobile.ui.theme.*

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        ProfileHeader()

        Spacer(modifier = Modifier.height(16.dp))

        ProfileMenu(onLogoutClick = onLogoutClick,
                    onNavigateToChangePassword = onNavigateToChangePassword)
    }
}

@Composable
private fun ProfileHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(colors = listOf(Plum, Lav))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AM",
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Ana Martínez López",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Personal Institucional · EMP-8870",
            fontSize = 13.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun ProfileMenu(onLogoutClick: () -> Unit,
                        onNavigateToChangePassword: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        ProfileMenuItem(
            title = "Mi Perfil",
            subtitle = "Ver y editar datos personales",
            icon = Icons.Default.Person,
            iconBgColor = Lav,
            iconTintColor = Plum,
            onClick = { /* Navigate to Edit Profile */ }
        )

        ProfileMenuItem(
            title = "Notificaciones",
            subtitle = "Configurar alertas y push",
            icon = Icons.Outlined.Notifications,
            iconBgColor = Mint,
            iconTintColor = Teal,
            onClick = { /* Navigate to Notifications */ }
        )


        ProfileMenuItem(
            title = "Cambiar Contraseña",
            subtitle = "Actualizar credenciales",
            icon = Icons.Default.Lock,
            iconBgColor = Lav,
            iconTintColor = Plum,
            onClick = { onNavigateToChangePassword() }
        )

        ProfileMenuItem(
            title = "Cerrar Sesión",
            subtitle = "Salir de la aplicación",
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            iconBgColor = Rose,
            iconTintColor = Coral,
            onClick = onLogoutClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SigesmobileTheme {
        ProfileScreen (
            onLogoutClick = {},
            onNavigateToChangePassword = {}
        )
    }
}