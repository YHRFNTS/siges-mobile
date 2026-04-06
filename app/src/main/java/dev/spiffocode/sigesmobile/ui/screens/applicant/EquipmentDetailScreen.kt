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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
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
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.ui.components.detail.InfoRow
import dev.spiffocode.sigesmobile.ui.components.detail.ResourceHeaderCard
import dev.spiffocode.sigesmobile.ui.components.detail.SectionTitle
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.EquipmentDetailUiState
import dev.spiffocode.sigesmobile.viewmodel.EquipmentDetailViewModel

@Composable
fun EquipmentDetailScreen(
    windowSizeClass: WindowSizeClass,
    equipmentId: Long,
    viewModel: EquipmentDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToReserve: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(equipmentId) {
        viewModel.loadEquipment(equipmentId)
    }

    EquipmentDetailScreenContent(
        windowSizeClass = windowSizeClass,
        state = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToReserve = onNavigateToReserve,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentDetailScreenContent(
    windowSizeClass: WindowSizeClass? = null,
    state: EquipmentDetailUiState,
    onNavigateBack: () -> Unit = {},
    onNavigateToReserve: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Equipo", fontWeight = FontWeight.Bold) },
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

            if (state.isLoading && state.equipment == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null && state.equipment == null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
            } else if (state.equipment != null) {
                val equipment = state.equipment
                val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded

                if (isExpanded) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                            EquipmentDetailLeftSection(equipment)
                        }
                        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                            EquipmentDetailRightSection(equipment, onNavigateToReserve)
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
                        EquipmentDetailLeftSection(equipment)
                        EquipmentDetailRightSection(equipment, onNavigateToReserve)
                    }
                }
            }

            if (state.error != null && state.equipment != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter)
                ) {
                    Text(state.error)
                }
            }
        }
    }
}

@Composable
fun EquipmentDetailLeftSection(equipment: EquipmentDto) {
    ResourceHeaderCard(
        status = equipment.status,
        title = equipment.name,
        subtitle = equipment.type?.name ?: "Equipo General",
        modifier = Modifier.padding(bottom = 24.dp)
    )

    SectionTitle("INFORMACIÓN BÁSICA")
    
    if (!equipment.inventoryIdNum.isNullOrBlank()) {
        InfoRow("Identificador / Serie", equipment.inventoryIdNum)
    }
    
    InfoRow("Disponible para Alumnos", if (equipment.availableForStudents) "Sí" else "No")
    
    if (equipment.spaceAttached != null) {
        InfoRow("Espacio Asignado", equipment.spaceAttached.name)
    }
    if (equipment.building != null) {
        InfoRow("Edificio Fijo", equipment.building.name)
    }
    
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun EquipmentDetailRightSection(equipment: EquipmentDto, onNavigateToReserve: () -> Unit) {
    if (!equipment.description.isNullOrBlank()) {
        SectionTitle("DESCRIPCIÓN")
        Text(
            text = equipment.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    Spacer(modifier = Modifier.height(48.dp))

    Button(
        onClick = onNavigateToReserve,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text("Reservar", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
    }
}



@Preview
@Composable
fun EquipmentDetailScreenPreview() {
    SigesmobileTheme {
        EquipmentDetailScreenContent(
            state = EquipmentDetailUiState(
                equipment = EquipmentDto(
                    id                  = 1,
                    name                = "Equipo de Oficina",
                    description         = "Un equipo para trabajar en tu oficina",
                    inventoryIdNum      = "ABC123",
                    status              = ReservableStatus.AVAILABLE,
                    availableForStudents = true
                )
            )
        )
    }
}
