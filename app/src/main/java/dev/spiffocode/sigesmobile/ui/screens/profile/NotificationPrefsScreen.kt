package dev.spiffocode.sigesmobile.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationPreferenceResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationType
import dev.spiffocode.sigesmobile.ui.components.profile.NotificationPreferenceItem
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.NotificationPrefsViewModel

fun NotificationType.toDisplayInfo(): Pair<String, String> = when (this) {
    NotificationType.COMMENT_ON_RESERVATION -> "Nuevos comentarios" to "Cuando alguien comente en tu reservación"
    NotificationType.RESERVATION_RESCHEDULE -> "Cambios de horario" to "Cuando modifiquen la fecha u hora de tu reserva"
    NotificationType.RESERVATION_REMINDER   -> "Recordatorios" to "Avisos antes de que inicie tu reserva"
    NotificationType.RESERVATION_CREATED    -> "Confirmación de solicitud" to "Cuando tu solicitud es recibida exitosamente"
    NotificationType.RESERVATION_APPROVED    -> "Reservaciones aprobadas" to "Cuando aceptan tus espacios o equipos"
    NotificationType.RESERVATION_REJECTED   -> "Reservaciones rechazadas" to "Cuando rechazan tu solicitud"
    NotificationType.RESERVATION_CANCELLED  -> "Cancelaciones" to "Si tu reservación es cancelada"
    NotificationType.PASSWORD_CHANGED       -> "Cambios de contraseña" to "Aviso de seguridad a tu cuenta"
    NotificationType.LOGIN_NEW_DEVICE       -> "Nuevos inicios de sesión" to "Para mejorar la seguridad de tu cuenta"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPrefsScreen(
    viewModel: NotificationPrefsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    NotificationPrefsScreen(
        isSaved = state.isSaved,
        isSaving = state.isSaving,
        isLoading = state.isLoading,
        preferences = state.preferences,
        error = state.error,
        toggleInApp = viewModel::toggleInApp,
        toggleEmail = viewModel::toggleEmail,
        savePreferences = viewModel::savePreferences,
        onNavigateBack = onNavigateBack
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPrefsScreen(
    isSaved: Boolean,
    isSaving: Boolean,
    isLoading: Boolean,
    toggleInApp: (NotificationType, Boolean) -> Unit = { _, _ -> },
    toggleEmail: (NotificationType, Boolean) -> Unit = { _, _ -> },
    savePreferences: () -> Unit = {},
    preferences: List<NotificationPreferenceResponse>,
    error: String?,
    onNavigateBack: () -> Unit = {}
) {

    LaunchedEffect(isSaved) {
        if (isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(preferences, key = { it.type.name }) { pref ->
                        NotificationPreferenceItem(
                            preference = pref,
                            onToggleInApp = { enabled -> toggleInApp(pref.type, enabled) },
                            onToggleEmail = {enabled -> toggleEmail(pref.type, enabled)}
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = savePreferences,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.background)
                    } else {
                        Text("Guardar Cambios", style = MaterialTheme.typography.titleMedium)
                    }
                }

                error?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


@Composable
@Preview
fun NotificationPrefsScreenPreview() {
    SigesmobileTheme{
        NotificationPrefsScreen(
            isSaved = false,
            isSaving = false,
            isLoading = false,
            preferences = listOf(
                NotificationPreferenceResponse(
                    type = NotificationType.COMMENT_ON_RESERVATION,
                    inAppEnabled = true,
                    emailEnabled = false,
                ),
                NotificationPreferenceResponse(
                    type = NotificationType.RESERVATION_RESCHEDULE,
                    inAppEnabled = true,
                    emailEnabled = false
                )
            ),
            error = null
        )
    }
}