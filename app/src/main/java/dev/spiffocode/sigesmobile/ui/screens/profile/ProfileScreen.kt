package dev.spiffocode.sigesmobile.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.spiffocode.sigesmobile.ui.components.profile.ProfileMenuItem
import dev.spiffocode.sigesmobile.ui.theme.Background
import dev.spiffocode.sigesmobile.ui.theme.Coral
import dev.spiffocode.sigesmobile.ui.theme.Lav
import dev.spiffocode.sigesmobile.ui.theme.Mint
import dev.spiffocode.sigesmobile.ui.theme.Plum
import dev.spiffocode.sigesmobile.ui.theme.Rose
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.ui.theme.Teal
import dev.spiffocode.sigesmobile.ui.theme.TextPrimary
import dev.spiffocode.sigesmobile.ui.theme.TextSecondary
import dev.spiffocode.sigesmobile.viewmodel.ProfileMenuViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileMenuViewModel = hiltViewModel(),
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) onLogoutSuccess()
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title            = { Text("Cerrar sesión") },
            text             = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton    = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout()
                }) {
                    Text("Cerrar sesión", color = Coral)
                }
            },
            dismissButton    = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        ProfileHeader(
            fullName = viewModel.fullName,
            initials = viewModel.initials,
            roleLabel = viewModel.roleLabel,
            identifier = viewModel.identifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProfileMenu(
            isLoading                  = state.isLoading,
            onNavigateToEditProfile    = onNavigateToEditProfile,
            onNavigateToNotifications  = onNavigateToNotifications,
            onNavigateToChangePassword = onNavigateToChangePassword,
            onLogoutClick              = { showLogoutDialog = true }
        )

        state.error?.let {
            Text(
                text     = it,
                color    = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    fullName: String,
    initials: String,
    roleLabel: String,
    identifier: String
) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier         = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(colors = listOf(Plum, Lav))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = initials.ifBlank { "?" },
                fontSize   = 32.sp,
                color      = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text       = fullName.ifBlank { "—" },
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
            color      = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text     = if (identifier.isNotBlank()) "$roleLabel · $identifier" else roleLabel,
            fontSize = 13.sp,
            color    = TextSecondary
        )
    }
}

@Composable
private fun ProfileMenu(
    isLoading: Boolean,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        ProfileMenuItem(
            title         = "Mi Perfil",
            subtitle      = "Ver y editar datos personales",
            icon          = Icons.Default.Person,
            iconBgColor   = Lav,
            iconTintColor = Plum,
            onClick       = onNavigateToEditProfile
        )

        ProfileMenuItem(
            title         = "Notificaciones",
            subtitle      = "Configurar alertas y push",
            icon          = Icons.Outlined.Notifications,
            iconBgColor   = Mint,
            iconTintColor = Teal,
            onClick       = onNavigateToNotifications
        )

        ProfileMenuItem(
            title         = "Cambiar Contraseña",
            subtitle      = "Actualizar credenciales",
            icon          = Icons.Default.Lock,
            iconBgColor   = Lav,
            iconTintColor = Plum,
            onClick       = onNavigateToChangePassword
        )

        ProfileMenuItem(
            title         = "Cerrar Sesión",
            subtitle      = "Salir de la aplicación",
            icon          = Icons.AutoMirrored.Filled.ExitToApp,
            iconBgColor   = Rose,
            iconTintColor = Coral,
            onClick       = onLogoutClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SigesmobileTheme {
        ProfileScreen(
            onLogoutSuccess            = {},
            onNavigateToChangePassword = {}
        )
    }
}