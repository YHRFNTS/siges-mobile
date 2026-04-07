package dev.spiffocode.sigesmobile.ui.screens.applicant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.ui.components.detail.InfoRow
import dev.spiffocode.sigesmobile.ui.components.detail.ObservationBox
import dev.spiffocode.sigesmobile.ui.components.detail.SectionTitle
import dev.spiffocode.sigesmobile.ui.components.detail.StatusHeaderCard
import dev.spiffocode.sigesmobile.ui.helpers.toHumanString
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.ReservationDetailUiState
import dev.spiffocode.sigesmobile.viewmodel.ReservationDetailViewModel
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReservationDetailScreen(
    windowSizeClass: WindowSizeClass,
    reservationId: Long,
    viewModel: ReservationDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(reservationId) {
        viewModel.loadReservation(reservationId)
    }

    ReservationDetailScreenContent(
        windowSizeClass = windowSizeClass,
        state = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToEdit = { onNavigateToEdit(reservationId) },
        onCancelReservation = { reason -> viewModel.cancel(reservationId, reason) },
        onClearMessages = viewModel::clearMessages
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreenContent(
    windowSizeClass: WindowSizeClass? = null,
    state: ReservationDetailUiState,
    onNavigateBack: () -> Unit = {},
    onNavigateToEdit: () -> Unit = {},
    onCancelReservation: (String) -> Unit = {},
    onClearMessages: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            val scrollState = rememberScrollState()

            if (state.isLoading && state.reservation == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null && state.reservation == null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
            } else if (state.reservation != null) {
                val res = state.reservation
                val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded

                if (isExpanded) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                            ReservationDetailLeftSection(res)
                        }
                        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                            ReservationDetailRightSection(
                                res = res,
                                onNavigateToEdit = onNavigateToEdit,
                                onCancelReservation = onCancelReservation
                            )
                        }
                    }
                } else {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(24.dp)
                    ) {
                        ReservationDetailLeftSection(res)
                        ReservationDetailRightSection(
                            res = res,
                            onNavigateToEdit = onNavigateToEdit,
                            onCancelReservation = onCancelReservation
                        )
                    }
                }
            }

            if (state.error != null || state.actionSuccess != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = onClearMessages) { Text("OK", color = MaterialTheme.colorScheme.inversePrimary) }
                    }
                ) {
                    Text(state.actionSuccess ?: state.error ?: "")
                }
            }
        }
    }
}



@Composable
fun ReservationDetailLeftSection(res: dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse) {
    StatusHeaderCard(
        status = res.status,
        title = res.reservable?.name ?: "Recurso Desconocido",
        subtitle = res.reservable?.building?.name ?: "",
        modifier = Modifier.padding(bottom = 24.dp)
    )

    SectionTitle("INFORMACIÓN DE LA SOLICITUD")
    
    val resourceTypeLabel = when(res.reservable?.reservableType) {
        ReservableType.SPACE -> "Espacio"
        ReservableType.EQUIPMENT -> "Equipo"
        else -> "--"
    }
    InfoRow("Tipo de recurso", resourceTypeLabel)
    
    val dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", Locale("es", "ES"))
    InfoRow("Fecha", res.date.format(dateFormatter))
    
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    InfoRow("Hora inicio", res.startTime.format(timeFormatter))
    InfoRow("Hora fin", res.endTime.format(timeFormatter))
    
    val durationMins = java.time.Duration.between(res.startTime, res.endTime).toMinutes()
    val durationText = if (durationMins >= 60) "${(durationMins/60.0).toString().removeSuffix(".0")} horas" else "$durationMins minutos"
    InfoRow("Duración", durationText)
    
    if (res.companions != null && res.companions > 1) {
        InfoRow("Asistentes", "${res.companions} personas")
    } else {
        InfoRow("Asistentes", "1 persona")
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun ReservationDetailRightSection(
    res: dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse,
    onNavigateToEdit: () -> Unit,
    onCancelReservation: (String) -> Unit
) {
    // ── Request Reason ──────────────────────────────────
    if (!res.requestReason.isNullOrBlank()) {
        SectionTitle("Propósito")
        Text(
            text = res.requestReason,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    if (!res.rejectionReason.isNullOrBlank()) {
        SectionTitle("Motivo de Rechazo")
        ObservationBox(
            observation = res.rejectionReason,
            authorAndDate = "Administración - - ${
                res.rejectedAt?.toKotlinLocalDateTime()?.toHumanString()
            }",
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    if (!res.approvalReason.isNullOrBlank()) {
        SectionTitle("Observaciones de Aprobación")
        ObservationBox(
            observation = res.approvalReason,
            authorAndDate = "Administración - ${res.approvedAt?.toKotlinLocalDateTime()?.toHumanString()}",
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    val otherNotes = res.notes ?: emptyList()
    if (otherNotes.isNotEmpty()) {
        SectionTitle("NOTAS ADICIONALES")
        otherNotes.forEach { note ->
            ObservationBox(
                observation = note.comment,
                authorAndDate = "${note.createdBy?.firstName} ${note.createdBy?.lastName} - - ${
                    note.createdAt?.toKotlinLocalDateTime()?.toHumanString()
                }",
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(48.dp))

    if (res.status == ReservationStatus.PENDING) {
        var showCancelDialog by remember { mutableStateOf(false) }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onNavigateToEdit,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Filled.Update,
                    contentDescription = "Reagendar",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reagendar", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showCancelDialog = true },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text("Cancelar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        }

        if (showCancelDialog) {
            var cancelReason by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                confirmButton = {
                    TextButton(onClick = { 
                        onCancelReservation(cancelReason)
                        showCancelDialog = false 
                    }) {
                        Text("Confirmar Cancelación", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) { Text("Volver") }
                },
                title = { Text("Cancelar Solicitud") },
                text = {
                    Column {
                        Text("¿Estás seguro de que quieres cancelar esta solicitud? Por favor, indica el motivo.")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = cancelReason,
                            onValueChange = { cancelReason = it },
                            placeholder = { Text("Motivo de la cancelación") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Preview(showBackground = true)
@Composable
fun ReservationDetailScreenPreview() {
    SigesmobileTheme {
        ReservationDetailScreenContent(
            windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
            state = ReservationDetailUiState(
                reservation = dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse(
                    id = 1,
                    status = ReservationStatus.APPROVED,
                    date = LocalDate.of(2026, 1, 28),
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(12, 0),
                    type = dev.spiffocode.sigesmobile.data.remote.dto.ReservationType.GROUP,
                    companions = 15,
                    reservable = dev.spiffocode.sigesmobile.data.remote.dto.ReservableDto(
                        id = 1, name = "Sala de Juntas A", reservableType = ReservableType.SPACE, status = dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus.AVAILABLE, availableForStudents = true
                    ),
                    approvedAt = java.time.LocalDateTime.now().minusDays(1),
                    approvalReason = "Cirren las puertas al irse",
                    createdAt = java.time.LocalDateTime.now().minusDays(2),
                    requestReason = "Reunión de seguimiento del proyecto de desarrollo de software para el semestre actual",
                    notes = listOf(
                        dev.spiffocode.sigesmobile.data.remote.dto.NoteItem(
                            id = 1, comment = "Reunión de seguimiento del proyecto de desarrollo de software para el semestre actual.", createdAt = null, updatedAt = null, createdBy = null
                        ),
                        dev.spiffocode.sigesmobile.data.remote.dto.NoteItem(
                            id = 2, comment = "La pantalla interactiva de la sala estará disponible durante su reserva.", createdAt = null, updatedAt = null, createdBy = null
                        )
                    )
                )
            )
        )
    }
}

