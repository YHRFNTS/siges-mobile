package dev.spiffocode.sigesmobile.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationPreferenceResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationType
import dev.spiffocode.sigesmobile.ui.theme.*
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

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", fontWeight = FontWeight.Bold, color = Plum) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Plum)
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
                    // Solo iteramos los que tienen display info o todos
                    items(state.preferences, key = { it.type.name }) { pref ->
                        NotificationPreferenceItem(
                            preference = pref,
                            onToggle = { enabled -> viewModel.toggleInApp(pref.type, enabled) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = viewModel::savePreferences,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Plum),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("✓ Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                state.error?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = it, color = Color.Red, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun NotificationPreferenceItem(
    preference: NotificationPreferenceResponse,
    onToggle: (Boolean) -> Unit
) {
    val (title, description) = preference.type.toDisplayInfo()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = preference.inAppEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Lav,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
            )
        )
    }
}
