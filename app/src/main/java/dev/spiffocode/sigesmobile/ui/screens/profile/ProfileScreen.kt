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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import dev.spiffocode.sigesmobile.ui.components.profile.ProfileMenuItem
import dev.spiffocode.sigesmobile.ui.theme.Background
import dev.spiffocode.sigesmobile.ui.theme.SigesTheme
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
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

    ProfileScreen(
        isLoggedOut = state.isLoggedOut,
        isLoading = state.isLoading,
        profilePictureUrl = viewModel.profilePictureUrl,
        fullName = viewModel.fullName,
        initials = viewModel.initials,
        roleLabel = viewModel.roleLabel,
        identifier = viewModel.identifier,
        error = state.error,
        onNavigateToEditProfile = onNavigateToEditProfile,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToChangePassword = onNavigateToChangePassword,
        onLogoutSuccess = onLogoutSuccess,
        logout = viewModel::logout

    )
}


@Composable
fun ProfileScreen(
    isLoggedOut: Boolean,
    isLoading: Boolean,
    profilePictureUrl: String,
    logout: () -> Unit = {},
    fullName: String,
    initials: String,
    roleLabel: String,
    identifier: String,
    error: String?,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) onLogoutSuccess()
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
                    logout()
                }) {
                    Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
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
            fullName = fullName,
            initials = initials,
            roleLabel = roleLabel,
            identifier = identifier,
            profilePictureUrl = profilePictureUrl
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProfileMenu(
            isLoading                  = isLoading,
            onNavigateToEditProfile    = onNavigateToEditProfile,
            onNavigateToNotifications  = onNavigateToNotifications,
            onNavigateToChangePassword = onNavigateToChangePassword,
            onLogoutClick              = { showLogoutDialog = true }
        )

        error?.let {
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
    identifier: String,
    profilePictureUrl: String? = null
) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!profilePictureUrl.isNullOrBlank()) {
            AsyncImage(
                model = profilePictureUrl,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier         = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.onPrimary,
                        MaterialTheme.colorScheme.onSecondary))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = initials.ifBlank { "?" },
                    fontSize   = 32.sp,
                    color      = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text       = fullName.ifBlank { "—" },
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text     = if (identifier.isNotBlank()) "$roleLabel · $identifier" else roleLabel,
            fontSize = 13.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant
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

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SigesmobileTheme {
        ProfileScreen(
            identifier = "20243ds158",
            profilePictureUrl = "https://mockimage.tw/photo/720x640/6ef1ea/ff8800",
            isLoggedOut = false,
            isLoading = false,
            fullName = "Ana Martínez López",
            initials = "AM",
            roleLabel = "Estudiante",
            error = null
        )
    }
}