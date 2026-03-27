package dev.spiffocode.sigesmobile.ui.screens.applicant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.spiffocode.sigesmobile.ui.components.newrequest.DatePickerField
import dev.spiffocode.sigesmobile.ui.components.newrequest.TimePickerField
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.EditReservationUiState
import dev.spiffocode.sigesmobile.viewmodel.EditReservationViewModel
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun EditReservationScreen(
    reservationId: Long,
    viewModel: EditReservationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(reservationId) {
        viewModel.loadReservation(reservationId)
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.clearMessages()
            onSaveSuccess()
        }
    }

    EditReservationScreenContent(
        state = uiState,
        onNavigateBack = onNavigateBack,
        onDateChange = viewModel::onDateChange,
        onStartTimeChange = viewModel::onStartTimeChange,
        onEndTimeChange = viewModel::onEndTimeChange,
        onCompanionsChange = viewModel::onCompanionsChange,
        onPurposeChange = viewModel::onPurposeChange,
        onSave = viewModel::saveChanges,
        onClearError = viewModel::clearMessages
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReservationScreenContent(
    state: EditReservationUiState,
    onNavigateBack: () -> Unit = {},
    onDateChange: (LocalDate) -> Unit = {},
    onStartTimeChange: (LocalTime) -> Unit = {},
    onEndTimeChange: (LocalTime) -> Unit = {},
    onCompanionsChange: (String) -> Unit = {},
    onPurposeChange: (String) -> Unit = {},
    onSave: () -> Unit = {},
    onClearError: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Solicitud", fontWeight = FontWeight.Bold) },
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
            if (state.isLoading && state.resourceName.isBlank()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(24.dp)
                ) {
                    // Resource Field (Disabled)
                    OutlinedTextField(
                        value = state.resourceName,
                        onValueChange = {},
                        enabled = false,
                        label = { Text("RECURSO (NO EDITABLE)") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Date Picker
                    DatePickerField(
                        date = state.date,
                        onDateChange = onDateChange
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Time Pickers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TimePickerField(
                            time = state.startTime,
                            label = "HORARIO *",
                            onTimeChange = onStartTimeChange,
                            modifier = Modifier.weight(1f)
                        )
                        
                        TimePickerField(
                            time = state.endTime,
                            label = "HASTA *",
                            onTimeChange = onEndTimeChange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Companions (Attendees)
                    OutlinedTextField(
                        value = state.companions,
                        onValueChange = onCompanionsChange,
                        label = { Text("NÚMERO DE ASISTENTES *") },
                        placeholder = { Text("Ej: 15") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = "Asistentes") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Purpose
                    OutlinedTextField(
                        value = state.purpose,
                        onValueChange = onPurposeChange,
                        label = { Text("PROPÓSITO DE LA RESERVA *") },
                        placeholder = { Text("Describe el propósito...") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Submit Button
                    Button(
                        onClick = onSave,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "Guardar", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar Cambios", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Error Snackbar
            if (state.error != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = onClearError) {
                            Text("OK", color = MaterialTheme.colorScheme.inversePrimary)
                        }
                    }
                ) {
                    Text(state.error)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditReservationScreenPreview() {
    SigesmobileTheme {
        EditReservationScreenContent(
            state = EditReservationUiState(
                resourceName = "Sala de Juntas A",
                date = LocalDate.of(2026, 1, 28),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0),
                companions = "15",
                purpose = "Reunión de seguimiento del proyecto de desarrollo de software para el semestre actual."
            )
        )
    }
}
