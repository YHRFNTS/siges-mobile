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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.ui.components.newrequest.DatePickerField
import dev.spiffocode.sigesmobile.ui.components.newrequest.ResourceSelectionSection
import dev.spiffocode.sigesmobile.ui.components.newrequest.ResourceTypeTabs
import dev.spiffocode.sigesmobile.ui.components.newrequest.TimePickerField
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.CreateReservationUiState
import dev.spiffocode.sigesmobile.viewmodel.CreateReservationViewModel
import dev.spiffocode.sigesmobile.viewmodel.ResourceType
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

@Composable
fun NewRequestScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: CreateReservationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.createdReservation) {
        uiState.createdReservation?.let {
            viewModel.resetForm()
            onNavigateToDetail(it.id)
        }
    }

    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    NewRequestScreenContent(
        isCompact = isCompact,
        state = uiState,
        onNavigateBack = onNavigateBack,
        onTypeSelected = viewModel::selectResourceType,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSpaceSelected = viewModel::selectSpace,
        onEquipmentSelected = viewModel::selectEquipment,
        onDateChange = viewModel::onDateChange,
        onStartTimeChange = viewModel::onStartTimeChange,
        onEndTimeChange = viewModel::onEndTimeChange,
        onCompanionsChange = viewModel::onCompanionsChange,
        onPurposeChange = viewModel::onPurposeChange,
        onSubmit = viewModel::submit,
        onClearError = viewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestScreenContent(
    isCompact: Boolean = true,
    state: CreateReservationUiState,
    onNavigateBack: () -> Unit = {},
    onTypeSelected: (ResourceType) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onSpaceSelected: (SpaceDto) -> Unit = {},
    onEquipmentSelected: (EquipmentDto) -> Unit = {},
    onDateChange: (LocalDate) -> Unit = {},
    onStartTimeChange: (LocalTime) -> Unit = {},
    onEndTimeChange: (LocalTime) -> Unit = {},
    onCompanionsChange: (String) -> Unit = {},
    onPurposeChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onClearError: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Solicitud", fontWeight = FontWeight.Bold) },
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(if (isCompact) 24.dp else 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isCompact) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        NewRequestFormFields(
                            state = state,
                            onTypeSelected = onTypeSelected,
                            onSearchQueryChange = onSearchQueryChange,
                            onSpaceSelected = onSpaceSelected,
                            onEquipmentSelected = onEquipmentSelected,
                            onDateChange = onDateChange,
                            onStartTimeChange = onStartTimeChange,
                            onEndTimeChange = onEndTimeChange,
                            onCompanionsChange = onCompanionsChange,
                            onPurposeChange = onPurposeChange,
                            onSubmit = onSubmit
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier.widthIn(max = 600.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(32.dp)) {
                            NewRequestFormFields(
                                state = state,
                                onTypeSelected = onTypeSelected,
                                onSearchQueryChange = onSearchQueryChange,
                                onSpaceSelected = onSpaceSelected,
                                onEquipmentSelected = onEquipmentSelected,
                                onDateChange = onDateChange,
                                onStartTimeChange = onStartTimeChange,
                                onEndTimeChange = onEndTimeChange,
                                onCompanionsChange = onCompanionsChange,
                                onPurposeChange = onPurposeChange,
                                onSubmit = onSubmit
                            )
                        }
                    }
                }
            }

            if (state.error != null) {
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
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

@Composable
fun NewRequestFormFields(
    state: CreateReservationUiState,
    onTypeSelected: (ResourceType) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSpaceSelected: (SpaceDto) -> Unit,
    onEquipmentSelected: (EquipmentDto) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onStartTimeChange: (LocalTime) -> Unit,
    onEndTimeChange: (LocalTime) -> Unit,
    onCompanionsChange: (String) -> Unit,
    onPurposeChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Text(
        text = "TIPO DE RECURSO *",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    ResourceTypeTabs(
        selectedType = state.resourceType,
        onTypeSelected = onTypeSelected
    )

    Spacer(modifier = Modifier.height(24.dp))

    ResourceSelectionSection(
        searchQuery = state.searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        searchResults = state.searchResults,
        isSearching = state.isSearching,
        selectedSpace = state.selectedSpace,
        selectedEquipment = state.selectedEquipment,
        onSpaceSelected = onSpaceSelected,
        onEquipmentSelected = onEquipmentSelected
    )

    Spacer(modifier = Modifier.height(24.dp))

    DatePickerField(
        date = state.date,
        onDateChange = onDateChange
    )

    Spacer(modifier = Modifier.height(24.dp))

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

    Button(
        onClick = onSubmit,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.large,
        enabled = !state.isLoading
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Icon(Icons.Default.Send, contentDescription = "Enviar", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enviar Solicitud", fontWeight = FontWeight.SemiBold)
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
}

@Preview(showBackground = true)
@Composable
fun NewRequestScreenPreview() {
    SigesmobileTheme {
        NewRequestScreenContent(
            state = CreateReservationUiState(
                resourceType = ResourceType.SPACE,
                searchQuery = "Sala de Juntas A",
                selectedSpace = SpaceDto(
                    id = 1L, name = "Sala de Juntas B",
                    status = ReservableStatus.AVAILABLE,
                    capacity = 10,
                    availableForStudents = true,
                    bookInAdvanceDuration = 1.hours.toJavaDuration()
                ),
                date = LocalDate.of(2026, 3, 27),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0),
                companions = "15",
                purpose = "Reunión de avance"
            )
        )
    }
}
