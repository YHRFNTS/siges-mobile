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
import dev.spiffocode.sigesmobile.data.remote.dto.BuildingDto
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentTypeDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceAssetDto
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.ui.components.detail.InfoRow
import dev.spiffocode.sigesmobile.ui.components.detail.ObservationBox
import dev.spiffocode.sigesmobile.ui.components.detail.ResourceHeaderCard
import dev.spiffocode.sigesmobile.ui.components.detail.SectionTitle
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.SpaceDetailUiState
import dev.spiffocode.sigesmobile.viewmodel.SpaceDetailViewModel

@Composable
fun SpaceDetailScreen(
    windowSizeClass: WindowSizeClass,
    spaceId: Long,
    viewModel: SpaceDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToReserve: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(spaceId) {
        viewModel.loadSpace(spaceId)
    }

    SpaceDetailScreenContent(
        windowSizeClass = windowSizeClass,
        state = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToReserve = onNavigateToReserve,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceDetailScreenContent(
    windowSizeClass: WindowSizeClass? = null,
    state: SpaceDetailUiState,
    onNavigateBack: () -> Unit = {},
    onNavigateToReserve: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Espacio", fontWeight = FontWeight.Bold) },
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

            if (state.isLoading && state.space == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null && state.space == null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
            } else if (state.space != null) {
                val space = state.space
                val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded

                if (isExpanded) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                            SpaceDetailLeftSection(space)
                        }
                        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                            SpaceDetailRightSection(space, onNavigateToReserve)
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
                        SpaceDetailLeftSection(space)
                        SpaceDetailRightSection(space, onNavigateToReserve)
                    }
                }
            }

            if (state.error != null && state.space != null) {
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
fun SpaceDetailLeftSection(space: SpaceDto) {
    ResourceHeaderCard(
        status = space.status,
        title = space.name,
        subtitle = space.building?.name ?: "Sin edificio",
        modifier = Modifier.padding(bottom = 24.dp)
    )

    SectionTitle("INFORMACIÓN BÁSICA")
    
    InfoRow("Tipo", space.spaceType?.name ?: "--")
    InfoRow("Capacidad", if (space.capacity != null) "${space.capacity} personas" else "--")
    InfoRow("Disponible para Alumnos", if (space.availableForStudents) "Sí" else "No")
    
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun SpaceDetailRightSection(space: SpaceDto, onNavigateToReserve: () -> Unit) {
    if (!space.description.isNullOrBlank()) {
        SectionTitle("DESCRIPCIÓN")
        Text(
            text = space.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    if (!space.assets.isNullOrEmpty()) {
        SectionTitle("EQUIPAMIENTO INCLUIDO")
        space.assets.forEach { asset ->
            ObservationBox(
                observation = asset.name,
                authorAndDate = "Inv: ${asset.inventoryNum} • ${asset.type?.name ?: "Equipo"}",
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
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
fun SpaceDetailScreenPreview() {
    SigesmobileTheme {
        SpaceDetailScreenContent(
            state = SpaceDetailUiState(
                space = SpaceDto(
                    id          = 1,
                    name        = "Sala de Conferencias 1",
                    description = "Una sala cómoda para tus reuniones",
                    capacity    = 10,
                    status      = ReservableStatus.AVAILABLE,
                    building = BuildingDto(
                        id  = 1,
                        name = "Edificio A"
                    ),
                    assets = listOf(
                        SpaceAssetDto(
                            id          = 1,
                            name        = "Proyector",
                            inventoryNum = "ABC123",
                            type = EquipmentTypeDto(
                                id   = 1,
                                name = "Proyector"
                            ),
                            description = "Un proyector para ver tus reuniones"
                        )
                    ),
                    availableForStudents = true
                )
            )
        )
    }
}

