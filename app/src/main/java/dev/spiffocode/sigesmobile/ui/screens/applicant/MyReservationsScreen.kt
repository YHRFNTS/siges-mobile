package dev.spiffocode.sigesmobile.ui.screens.applicant

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationType
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceTypeDto
import dev.spiffocode.sigesmobile.ui.components.FilterSelector
import dev.spiffocode.sigesmobile.ui.components.InfiniteScrollGrid
import dev.spiffocode.sigesmobile.ui.components.homescreen.RequestCard
import dev.spiffocode.sigesmobile.ui.components.newrequest.ClickableOutlinedTextField
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.MyReservationsTab
import dev.spiffocode.sigesmobile.viewmodel.MyReservationsUiState
import dev.spiffocode.sigesmobile.viewmodel.MyReservationsViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    windowSizeClass: WindowSizeClass,
    showBackButton: Boolean = false,
    viewModel: MyReservationsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToNewRequest: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MyReservationsScreen(
        windowSizeClass = windowSizeClass,
        isLoading = state.isLoading,
        reservations = state.reservations,
        selectedTab = state.selectedTab,
        selectedReservable = state.selectedReservable,
        totalPages = state.totalPages,
        currentPage = state.currentPage,
        error = state.error,
        selectTab = viewModel::selectTab,
        filterByReservable = viewModel::filterByReservable,
        onSetDateRange = viewModel::setDateRange,
        onSetSort = viewModel::setSort,
        onRefresh = viewModel::refresh,
        loadPage = viewModel::loadPage,
        showBackButton = showBackButton,
        onNavigateBack = onNavigateBack,
        onNavigateToNewRequest = onNavigateToNewRequest,
        onNavigateToDetail = onNavigateToDetail,
        state = state
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    windowSizeClass: WindowSizeClass,
    isLoading: Boolean,
    reservations: List<ReservationResponse>,
    selectedTab: MyReservationsTab,
    selectedReservable: ReservableDto? = null,
    totalPages: Int,
    currentPage: Int,
    error: String?,
    selectTab: (MyReservationsTab) -> Unit = {},
    filterByReservable: (ReservableDto?) -> Unit = {},
    onSetDateRange: (java.time.LocalDate?, java.time.LocalDate?) -> Unit = { _, _ -> },
    onSetSort: (String, String) -> Unit = { _, _ -> },
    onRefresh: () -> Unit = {},
    loadPage: (Int) -> Unit = {},
    showBackButton: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onNavigateToNewRequest: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    state: MyReservationsUiState = MyReservationsUiState()
) {
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }

    if (showFromDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showFromDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis?.let {
                        java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneOffset.UTC).toLocalDate()
                    }
                    onSetDateRange(selectedDate, state.dateTo)
                    showFromDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    onSetDateRange(null, state.dateTo)
                    showFromDatePicker = false
                }) { Text("Limpiar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showToDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showToDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis?.let {
                        java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneOffset.UTC).toLocalDate()
                    }
                    onSetDateRange(state.dateFrom, selectedDate)
                    showToDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    onSetDateRange(state.dateFrom, null)
                    showToDatePicker = false
                }) { Text("Limpiar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewRequest,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva solicitud")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
            // App Bar
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Solicitudes",
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
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
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
            val selectedTabIndex = when (selectedTab) {
                MyReservationsTab.ALL -> 0
                MyReservationsTab.PENDING -> 1
                MyReservationsTab.APPROVED -> 2
            }

            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.padding(horizontal = 24.dp),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == MyReservationsTab.ALL,
                    onClick = { selectTab(MyReservationsTab.ALL) },
                    text = {
                        Text(
                            "Todas",
                            fontWeight = if (selectedTab == MyReservationsTab.ALL) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == MyReservationsTab.PENDING,
                    onClick = { selectTab(MyReservationsTab.PENDING) },
                    text = {
                        Text(
                            "Pendientes",
                            fontWeight = if (selectedTab == MyReservationsTab.PENDING) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == MyReservationsTab.APPROVED,
                    onClick = { selectTab(MyReservationsTab.APPROVED) },
                    text = {
                        Text(
                            "Aprobadas",
                            fontWeight = if (selectedTab == MyReservationsTab.APPROVED) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filters & Sort Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Resource Filter
                var expandedFilter by remember { mutableStateOf(false) }
                FilterSelector(
                    value = selectedReservable?.name ?: "Recurso",
                    expanded = expandedFilter,
                    modifier = Modifier.weight(1f),
                    onExpandedChange = { expandedFilter = it },
                ) {
                    DropdownMenuItem(
                        text = { Text("Todos") },
                        onClick = {
                            filterByReservable(null)
                            expandedFilter = false
                        }
                    )
                    state.reservables.forEach { reservable ->
                        DropdownMenuItem(
                            text = { Text(reservable.name) },
                            onClick = {
                                filterByReservable(reservable)
                                expandedFilter = false
                            }
                        )
                    }
                }

                // Sort filter
                var expandedSort by remember { mutableStateOf(false) }
                val sortLabel = when {
                    state.sort.startsWith("createdAt") -> "Fecha Sol."
                    state.sort.startsWith("reservable") -> "Recurso"
                    state.sort.startsWith("status") -> "Estado"
                    else -> "Ordenar"
                }

                FilterSelector(
                    value = sortLabel,
                    expanded = expandedSort,
                    modifier = Modifier.weight(1f),
                    onExpandedChange = { expandedSort = it }
                ) {
                    listOf(
                        "Reciente" to "createdAt,desc",
                        "Antiguo" to "createdAt,asc",
                        "Recurso (A-Z)" to "reservable,asc",
                        "Estado" to "status,asc"
                    ).forEach { (label, sortValue) ->
                        val parts = sortValue.split(",")
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                onSetSort(parts[0], parts[1])
                                expandedSort = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date Range Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dateFromStr = state.dateFrom?.format(DateTimeFormatter.ofPattern("dd/MM/yy")) ?: "Desde"
                val dateToStr = state.dateTo?.format(DateTimeFormatter.ofPattern("dd/MM/yy")) ?: "Hasta"

                Box(modifier = Modifier.weight(1f)) {
                    ClickableOutlinedTextField(
                        label = "Desde",
                        value = dateFromStr,
                        placeholder = "Desde",
                        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        onClick = { showFromDatePicker = true }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    ClickableOutlinedTextField(
                        label = "Hasta",
                        value = dateToStr,
                        placeholder = "Hasta",
                        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        onClick = { showToDatePicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content List
            if (isLoading && currentPage == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            } else if (reservations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron solicitudes", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val hasNextPage = currentPage < (totalPages - 1)
                val columns = when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> 1
                    WindowWidthSizeClass.Medium -> 2
                    else -> 3
                }

                InfiniteScrollGrid(
                    elements = reservations,
                    columns = columns,
                    key = { _, res -> res.id },
                    loadMoreItems = { loadPage(currentPage + 1) },
                    hasNextPage = hasNextPage,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    spacing = Arrangement.spacedBy(4.dp),
                    content = { reservation ->
                        val reservableTypeDisplay = when (reservation.reservable?.reservableType) {
                            ReservableType.SPACE -> "Espacio"
                            ReservableType.EQUIPMENT -> "Equipo"
                            null -> "--"
                        }

                        // Calculate duration heuristically based on start and end times assuming same day
                        val durationMins =
                            Duration.between(reservation.startTime, reservation.endTime).toMinutes()
                        val durationDisplay = if (durationMins >= 60) {
                            val hours = durationMins / 60
                            val remainder = durationMins % 60
                            if (remainder == 0L) "$hours horas" else "$hours h $remainder min"
                        } else {
                            "$durationMins min"
                        }

                        RequestCard(
                            title = reservation.reservable?.name ?: "Recurso no especificado",
                            startDateTime = reservation.date.atTime(reservation.startTime).let {
                                LocalDateTime(
                                    it.year,
                                    it.monthValue,
                                    it.dayOfMonth,
                                    it.hour,
                                    it.minute
                                )
                            },
                            endDateTime = reservation.date.atTime(reservation.endTime).let {
                                LocalDateTime(
                                    it.year,
                                    it.monthValue,
                                    it.dayOfMonth,
                                    it.hour,
                                    it.minute
                                )
                            },
                            status = reservation.status,
                            meta1 = reservableTypeDisplay,
                            meta2 = durationDisplay,
                            createdAt = reservation.createdAt?.let {
                                LocalDateTime(
                                    it.year,
                                    it.monthValue,
                                    it.dayOfMonth,
                                    it.hour,
                                    it.minute
                                )
                            },
                            onClick = { onNavigateToDetail(reservation.id) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))


                    }
                )
            }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
@Preview
fun MyReservationsScreenPreviewEmpty(){
    SigesmobileTheme {
        MyReservationsScreen(
            windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        )
    }
}


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
@Preview
fun MyReservationsScreenPreviewWithItems(){
    SigesmobileTheme {
        MyReservationsScreen(
            windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
            isLoading = false,
            reservations = listOf(
                ReservationResponse(
                    id = 1,
                    reservable = ReservableDto(
                        id = 1,
                        name = "Aula 1",
                        reservableType = ReservableType.SPACE,
                        availableForStudents = true,
                        status = ReservableStatus.AVAILABLE,
                        capacity = 10,
                        spaceType = SpaceTypeDto(
                            id = 1,
                            name = "Aulas"
                        )
                    ),
                    date = LocalDate(2026,1,28).toJavaLocalDate(),
                    startTime = LocalTime(10,0).toJavaLocalTime(),
                    endTime = LocalTime(12,0).toJavaLocalTime(),
                    status = ReservationStatus.PENDING,
                    type = ReservationType.GROUP,
                    companions = 7
                )
            ),
            selectedTab = MyReservationsTab.ALL,
            selectedReservable = null,
            totalPages = 1,
            currentPage = 0,
            error = null,

        )
    }
}
