package dev.spiffocode.sigesmobile.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.spiffocode.sigesmobile.viewmodel.CatalogTab
import dev.spiffocode.sigesmobile.viewmodel.CatalogsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogsScreen(
    viewModel: CatalogsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Catálogos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir registro")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            ScrollableTabRow(
                selectedTabIndex = state.selectedTab.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = state.selectedTab == CatalogTab.BUILDINGS,
                    onClick = { viewModel.selectTab(CatalogTab.BUILDINGS) },
                    text = { Text("Edificios") }
                )
                Tab(
                    selected = state.selectedTab == CatalogTab.SPACE_TYPES,
                    onClick = { viewModel.selectTab(CatalogTab.SPACE_TYPES) },
                    text = { Text("Tipos de Espacio") }
                )
                Tab(
                    selected = state.selectedTab == CatalogTab.EQUIPMENT_TYPES,
                    onClick = { viewModel.selectTab(CatalogTab.EQUIPMENT_TYPES) },
                    text = { Text("Tipos de Equipo") }
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (state.selectedTab == CatalogTab.BUILDINGS) {
                        if (state.buildings.isEmpty()) {
                            item { EmptyView("No hay edificios registrados") }
                        } else {
                            items(state.buildings, key = { it.id }) { building ->
                                CatalogItemCard(
                                    title = building.name,
                                    subtitle = null,
                                    onEdit = { viewModel.openEditDialog(building.id, building.name, null) },
                                    onDelete = { viewModel.deleteRecord(building.id) }
                                )
                            }
                        }
                    } else if (state.selectedTab == CatalogTab.SPACE_TYPES) {
                        if (state.spaceTypes.isEmpty()) {
                            item { EmptyView("No hay tipos de espacio registrados") }
                        } else {
                            items(state.spaceTypes, key = { it.id }) { sr ->
                                CatalogItemCard(
                                    title = sr.name,
                                    subtitle = sr.description,
                                    onEdit = { viewModel.openEditDialog(sr.id, sr.name, sr.description) },
                                    onDelete = { viewModel.deleteRecord(sr.id) }
                                )
                            }
                        }
                    } else if (state.selectedTab == CatalogTab.EQUIPMENT_TYPES) {
                        if (state.equipmentTypes.isEmpty()) {
                            item { EmptyView("No hay tipos de equipo registrados") }
                        } else {
                            items(state.equipmentTypes, key = { it.id }) { er ->
                                CatalogItemCard(
                                    title = er.name,
                                    subtitle = er.description,
                                    onEdit = { viewModel.openEditDialog(er.id, er.name, er.description) },
                                    onDelete = { viewModel.deleteRecord(er.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeDialog() },
            title = {
                Text(if (state.editingId == null) "Agregar Registro" else "Editar Registro")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.editingName,
                        onValueChange = { viewModel.onEditNameChange(it) },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    if (state.selectedTab != CatalogTab.BUILDINGS) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.editingDescription,
                            onValueChange = { viewModel.onEditDescriptionChange(it) },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 4
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.saveRecord() }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    state.error?.let { error ->
        LaunchedEffect(error) {
            // Un Snackbar host idealmente
        }
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) { Text("Aceptar") }
            }
        )
    }
}

@Composable
private fun CatalogItemCard(
    title: String,
    subtitle: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun EmptyView(msg: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(msg, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
