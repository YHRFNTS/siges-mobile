package dev.spiffocode.sigesmobile.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import dev.spiffocode.sigesmobile.data.remote.dto.NoteItem
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationType
import dev.spiffocode.sigesmobile.data.remote.dto.UserRole
import dev.spiffocode.sigesmobile.ui.components.detail.InfoRow
import dev.spiffocode.sigesmobile.ui.components.detail.ObservationBox
import dev.spiffocode.sigesmobile.ui.components.detail.SectionTitle
import dev.spiffocode.sigesmobile.ui.components.detail.StatusHeaderCard
import dev.spiffocode.sigesmobile.ui.helpers.toText
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.AdminReviewUiState
import dev.spiffocode.sigesmobile.viewmodel.AdminReviewViewModel
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AdminReviewDetailScreen(
    windowSizeClass: WindowSizeClass,
    reservationId: Long,
    viewModel: AdminReviewViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(reservationId) {
        viewModel.loadReservation(reservationId)
    }

    AdminReviewDetailScreenContent(
        windowSizeClass = windowSizeClass,
        state           = state,
        onNavigateBack  = onNavigateBack,
        onObservationChange = viewModel::setObservation,
        onApprove       = { viewModel.approve(reservationId) },
        onReject        = { viewModel.reject(reservationId) },
        onClearMessages = viewModel::clearMessages
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReviewDetailScreenContent(
    windowSizeClass: WindowSizeClass,
    state: AdminReviewUiState,
    onNavigateBack: () -> Unit = {},
    onObservationChange: (String) -> Unit = {},
    onApprove: () -> Unit = {},
    onReject: () -> Unit = {},
    onClearMessages: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de Solicitud",
                        fontWeight = FontWeight.Bold
                    )
                },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.reservation == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null && state.reservation == null -> {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    )
                }
                state.reservation != null -> {
                    val res = state.reservation
                    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
                    
                    if (isExpanded) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                AdminReviewLeftSection(res)
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                AdminReviewRightSection(
                                    res = res,
                                    state = state,
                                    onObservationChange = onObservationChange,
                                    onApprove = onApprove,
                                    onReject = onReject
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
                            AdminReviewLeftSection(res)
                            AdminReviewRightSection(
                                res = res,
                                state = state,
                                onObservationChange = onObservationChange,
                                onApprove = onApprove,
                                onReject = onReject
                            )
                        }
                    }
                }
            }

            // ── Snackbar ──────────────────────────────────────────────────────
            if (state.error != null || state.actionSuccess != null) {
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = onClearMessages) {
                            Text("OK", color = MaterialTheme.colorScheme.inversePrimary)
                        }
                    }
                ) {
                    Text(state.actionSuccess ?: state.error ?: "")
                }
            }
        }
    }
}

@Composable
fun AdminReviewLeftSection(res: ReservationResponse) {
    // ── Header card ───────────────────────────────────────
    StatusHeaderCard(
        status   = res.status,
        title    = res.reservable?.name ?: "Recurso desconocido",
        subtitle = res.reservable?.building?.name ?: "",
        modifier = Modifier.padding(bottom = 24.dp)
    )

    // ── Petitioner section ────────────────────────────────
    val petitioner = res.petitioner
    if (petitioner != null) {
        SectionTitle("SOLICITANTE")
        InfoRow("Nombre", "${petitioner.firstName} ${petitioner.lastName}")
        InfoRow("Tipo de usuario", petitioner.role.toText())
        val idLabel = when (petitioner.role) {
            UserRole.STUDENT               -> "Número de matrícula"
            UserRole.INSTITUTIONAL_STAFF   -> "ID empleado"
            else                           -> "ID"
        }
        val idValue = petitioner.registrationNumber
            ?: petitioner.employeeNumber
            ?: "--"
        InfoRow(idLabel, idValue)
        Spacer(modifier = Modifier.height(8.dp))
    }

    // ── Reservation info ─────────────────────────────────
    SectionTitle("INFORMACIÓN DE LA SOLICITUD")

    val resourceTypeLabel = when (res.reservable?.reservableType) {
        ReservableType.SPACE     -> "Espacio"
        ReservableType.EQUIPMENT -> "Equipo"
        else                     -> "--"
    }
    InfoRow("Tipo de recurso", resourceTypeLabel)

    val dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", Locale("es", "ES"))
    InfoRow("Fecha", res.date.format(dateFormatter))

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    InfoRow("Hora inicio", res.startTime.format(timeFormatter))
    InfoRow("Hora fin",   res.endTime.format(timeFormatter))

    val durationMins = java.time.Duration.between(res.startTime, res.endTime).toMinutes()
    val durationText = if (durationMins >= 60)
        "${(durationMins / 60.0).toString().removeSuffix(".0")} horas"
    else "$durationMins minutos"
    InfoRow("Duración", durationText)

    val assistants = res.companions?.takeIf { it > 1 } ?: 1
    InfoRow("Asistentes", "$assistants ${if (assistants == 1) "persona" else "personas"}")

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun AdminReviewRightSection(
    res: ReservationResponse,
    state: AdminReviewUiState,
    onObservationChange: (String) -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    // ── Purpose note ──────────────────────────────────────
    val firstNote = res.notes?.firstOrNull()
    if (firstNote != null) {
        SectionTitle("PROPÓSITO")
        Text(
            text     = firstNote.comment,
            style    = MaterialTheme.typography.bodyMedium,
            color    = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    // ── Previous admin notes (resolved) ───────────────────
    val adminNotes = res.notes?.drop(1) ?: emptyList()
    if (adminNotes.isNotEmpty() && res.status != ReservationStatus.PENDING) {
        SectionTitle("OBSERVACIONES")
        adminNotes.forEach { note ->
            val author = note.createdBy?.let {
                "${it.firstName} ${it.lastName}"
            } ?: "Admin"
            ObservationBox(
                observation  = note.comment,
                authorAndDate = author,
                modifier     = Modifier.padding(bottom = 8.dp)
            )
        }
    }

    // ── Observation field (always shown for Admin) ─────────
    if (res.status == ReservationStatus.PENDING) {
        SectionTitle("OBSERVACIONES (OPCIONAL)")
        OutlinedTextField(
            value       = state.observation,
            onValueChange = onObservationChange,
            placeholder = { Text("Agrega comentarios o instrucciones adicionales...") },
            modifier    = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape    = RoundedCornerShape(12.dp),
            colors   = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
        Text(
            text  = "Estas observaciones serán visibles para el solicitante",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // ── Action buttons ────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Deny
            Button(
                onClick  = onReject,
                enabled  = !state.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor   = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Close, contentDescription = "Denegar")
                Text(
                    text = "  Denegar",
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Approve
            Button(
                onClick  = onApprove,
                enabled  = !state.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor   = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = "Aprobar")
                Text(
                    text = "  Aprobar",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}

// ───────────────────────────── Previews ──────────────────────────────────────

// Removed previews that depended on hardcoded SizeClass for simplicity, but we can just pass a dummy windowSizeClass later


