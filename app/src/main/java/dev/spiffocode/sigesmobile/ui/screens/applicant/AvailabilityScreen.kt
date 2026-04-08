package dev.spiffocode.sigesmobile.ui.screens.applicant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentDto
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentTypeDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceTypeDto
import dev.spiffocode.sigesmobile.ui.components.FilterSelector
import dev.spiffocode.sigesmobile.ui.components.InfiniteScrollGrid
import dev.spiffocode.sigesmobile.ui.components.SearchBar
import dev.spiffocode.sigesmobile.ui.components.newrequest.DatePickerField
import dev.spiffocode.sigesmobile.ui.components.newrequest.TimePickerField
import dev.spiffocode.sigesmobile.ui.components.homescreen.AvailableItemCard
import java.time.LocalDate
import java.time.LocalTime
import dev.spiffocode.sigesmobile.viewmodel.AvailabilityTab
import dev.spiffocode.sigesmobile.viewmodel.AvailabilityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailabilityScreen(
    windowSizeClass: WindowSizeClass,
    showBackButton: Boolean = false,
    viewModel: AvailabilityViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToSpaceDetail: (Long) -> Unit = {},
    onNavigateToEquipmentDetail: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    AvailabilityScreen(
        windowSizeClass = windowSizeClass,
        spaces = state.spaces,
        equipments = state.equipments,
        searchQuery = state.searchQuery,
        selectedTab = state.selectedTab,
        spaceTypes = state.spaceTypes,
        selectedSpaceTypeId = state.selectedSpaceTypeId,
        equipmentTypes = state.equipmentTypes,
        selectedEquipmentTypeId = state.selectedEquipmentTypeId,
        sortBy = state.sortBy,
        isLoading = state.isLoading,
        totalPages = state.totalPages,
        currentPage = state.currentPage,
        error = state.error,
        loadPage = viewModel::loadPage,
        onSortBy = viewModel::sortBy,
        filterBySpaceType = viewModel::filterBySpaceType,
        filterByEquipmentType = viewModel::filterByEquipmentType,
        onSelectTab = viewModel::selectTab,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        selectedDate = state.selectedDate,
        selectedStartTime = state.selectedStartTime,
        selectedEndTime = state.selectedEndTime,
        onDateChange = viewModel::onDateChange,
        onStartTimeChange = viewModel::onStartTimeChange,
        onEndTimeChange = viewModel::onEndTimeChange,
        onRefresh = {viewModel.selectTab(state.selectedTab)},
        isFilterExpanded = state.isFilterExpanded,
        onToggleFilters = viewModel::toggleFilters,
        showBackButton = showBackButton,
        onNavigateBack = onNavigateBack,
        onNavigateToSpaceDetail = onNavigateToSpaceDetail,
        onNavigateToEquipmentDetail = onNavigateToEquipmentDetail
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailabilityScreen(
    windowSizeClass: WindowSizeClass,
    spaces: List<SpaceDto>,
    equipments: List<EquipmentDto>,
    searchQuery: String,
    selectedTab: AvailabilityTab,
    spaceTypes: List<SpaceTypeDto>,
    selectedSpaceTypeId: Long?,
    equipmentTypes: List<EquipmentTypeDto>,
    selectedEquipmentTypeId: Long?,
    sortBy: String?,
    isLoading: Boolean,
    totalPages: Int,
    currentPage: Int,
    error: String?,
    loadPage: (Int) -> Unit = {},
    onSortBy: (String?) -> Unit = {},
    filterBySpaceType: (Long?) -> Unit = {},
    filterByEquipmentType: (Long?) -> Unit = {},
    onSelectTab: (AvailabilityTab) -> Unit = {},
    onRefresh: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    selectedDate: LocalDate? = null,
    selectedStartTime: LocalTime? = null,
    selectedEndTime: LocalTime? = null,
    onDateChange: (LocalDate?) -> Unit = {},
    onStartTimeChange: (LocalTime?) -> Unit = {},
    onEndTimeChange: (LocalTime?) -> Unit = {},
    showBackButton: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onNavigateToSpaceDetail: (Long) -> Unit = {},
    onNavigateToEquipmentDetail: (Long) -> Unit = {}
) {
    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Disponibilidad",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // Tabs
            SecondaryTabRow(
                selectedTabIndex = if (selectedTab == AvailabilityTab.SPACES) 0 else 1,
                modifier = Modifier.padding(horizontal = 24.dp),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {},
            ) {
                Tab(
                    selected = selectedTab == AvailabilityTab.SPACES,
                    onClick = { onSelectTab(AvailabilityTab.SPACES) },
                    text = {
                        Text(
                            "Espacios",
                            fontWeight = if (selectedTab == AvailabilityTab.SPACES) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == AvailabilityTab.EQUIPMENTS,
                    onClick = { onSelectTab(AvailabilityTab.EQUIPMENTS) },
                    text = {
                        Text(
                            "Equipos",
                            fontWeight = if (selectedTab == AvailabilityTab.EQUIPMENTS) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SearchBar(
                        searchQuery = searchQuery,
                        onSearchQueryChange = onSearchQueryChange
                    )
                }
                androidx.compose.material3.IconButton(
                    onClick = onToggleFilters,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isFilterExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.FilterList,
                        contentDescription = "Toggle filters",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            AnimatedVisibility(
                visible = isFilterExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ScheduleFilters(
                        selectedDate = selectedDate,
                        startTime = selectedStartTime,
                        endTime = selectedEndTime,
                        onDateChange = onDateChange,
                        onStartTimeChange = onStartTimeChange,
                        onEndTimeChange = onEndTimeChange
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        var expandedType by remember { mutableStateOf(false) }

                        FilterSelector(
                            value = if (selectedTab == AvailabilityTab.SPACES) {
                                spaceTypes.find { it.id == selectedSpaceTypeId }?.name ?: "Tipo de espacio"
                            } else {
                                equipmentTypes.find { it.id == selectedEquipmentTypeId }?.name
                                    ?: "Tipo de equipo"
                            },
                            modifier = Modifier.weight(1f),
                            expanded = expandedType,
                            onExpandedChange = { expandedType = it },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todos") },
                                onClick = {
                                    if (selectedTab == AvailabilityTab.SPACES) filterBySpaceType(null)
                                    else filterByEquipmentType(null)
                                    expandedType = false
                                }
                            )
                            if (selectedTab == AvailabilityTab.SPACES) {
                                spaceTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.name) },
                                        onClick = {
                                            filterBySpaceType(type.id)
                                            expandedType = false
                                        }
                                    )
                                }
                            } else {
                                equipmentTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.name) },
                                        onClick = {
                                            filterByEquipmentType(type.id)
                                            expandedType = false
                                        }
                                    )
                                }
                            }
                        }

                        var expandedSort by remember { mutableStateOf(false) }
                        FilterSelector(
                            value = when (sortBy) {
                                "name,asc" -> "Nombre (A-Z)"
                                "name,desc" -> "Nombre (Z-A)"
                                "capacity,asc" -> "Capacidad (menor)"
                                "capacity,desc" -> "Capacidad (mayor)"
                                else -> "Ordenar por"
                            },
                            modifier = Modifier.weight(1f),
                            expanded = expandedSort,
                            onExpandedChange = { expandedSort = it },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Nombre (A-Z)") },
                                onClick = { onSortBy("name,asc"); expandedSort = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Nombre (Z-A)") },
                                onClick = { onSortBy("name,desc"); expandedSort = false }
                            )
                            if (selectedTab == AvailabilityTab.SPACES) {
                                DropdownMenuItem(
                                    text = { Text("Capacidad (menor)") },
                                    onClick = { onSortBy("capacity,asc"); expandedSort = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Capacidad (mayor)") },
                                    onClick = { onSortBy("capacity,desc"); expandedSort = false }
                                )
                            }
                        }
                    }
                }
            }

                // Sort Filter
                var expandedSort by remember { mutableStateOf(false) }
                val sortOptions =
                    listOf("Nombre (A-Z)" to "name,asc", "Nombre (Z-A)" to "name,desc")
                val currentSortLabel =
                    sortOptions.find { it.second == sortBy }?.first ?: "Ordenar por"

                FilterSelector(
                    value = currentSortLabel,
                    expanded = expandedSort,
                    onExpandedChange = { expandedSort = it },
                    modifier = Modifier.weight(1f)
                ) {
                    DropdownMenuItem(
                        text = { Text("Sin ordenar") },
                        onClick = {
                            onSortBy(null)
                            expandedSort = false
                        }
                    )
                    sortOptions.forEach { (label, sortValue) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                onSortBy(sortValue)
                                expandedSort = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading && currentPage == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            } else {
                val hasNextPage = currentPage < (totalPages - 1)
                val columns = when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> 1
                    WindowWidthSizeClass.Medium -> 2
                    else -> 3
                }

                if (selectedTab == AvailabilityTab.SPACES) {
                    if (spaces.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No se encontraron espacios",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        InfiniteScrollGrid(
                            elements = spaces,
                            columns = columns,
                            key = { _, space -> space.id },
                            loadMoreItems = { loadPage(currentPage + 1) },
                            hasNextPage = hasNextPage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            spacing = Arrangement.spacedBy(4.dp),
                            content = { space ->
                                AvailableItemCard(
                                    title = space.name,
                                    meta = "Capacidad para ${space.capacity ?: "--"} personas",
                                    status = space.status,
                                    resourceCategory = space.spaceType?.name ?: "Espacio",
                                    resourceType = ReservableType.SPACE,
                                    onClick = { onNavigateToSpaceDetail(space.id) }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        )
                    }
                } else {
                    if (equipments.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No se encontraron equipos",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        InfiniteScrollGrid(
                            elements = equipments,
                            columns = columns,
                            key = { _, eq -> eq.id },
                            loadMoreItems = { loadPage(currentPage + 1) },
                            hasNextPage = hasNextPage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            content = { eq ->
                                AvailableItemCard(
                                    title = eq.name,
                                    meta = eq.building?.name ?: "--",
                                    status = eq.status,
                                    resourceCategory = eq.type?.name ?: "Equipo",
                                    resourceType = ReservableType.EQUIPMENT,
                                    onClick = { onNavigateToEquipmentDetail(eq.id) }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        )
                    }
                }
            }
        }
    }
}

// Previews removed to avoid WindowSizeClass mock errors

// removed

@Composable
fun ScheduleFilters(
    selectedDate: LocalDate?,
    startTime: LocalTime?,
    endTime: LocalTime?,
    onDateChange: (LocalDate?) -> Unit,
    onStartTimeChange: (LocalTime?) -> Unit,
    onEndTimeChange: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Filtrar por horario",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (selectedDate != null || startTime != null || endTime != null) {
                androidx.compose.material3.TextButton(
                    onClick = {
                        onDateChange(null)
                        onStartTimeChange(null)
                        onEndTimeChange(null)
                    }
                ) {
                    Text("Limpiar", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        DatePickerField(
            date = selectedDate,
            onDateChange = { onDateChange(it) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TimePickerField(
                time = startTime,
                label = "Inicio",
                onTimeChange = { onStartTimeChange(it) },
                modifier = Modifier.weight(1f)
            )
            TimePickerField(
                time = endTime,
                label = "Fin",
                onTimeChange = { onEndTimeChange(it) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
