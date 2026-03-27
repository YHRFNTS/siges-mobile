package dev.spiffocode.sigesmobile.ui.screens.applicant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.ui.components.FilterSelector
import dev.spiffocode.sigesmobile.ui.components.InfiniteScrollList
import dev.spiffocode.sigesmobile.ui.components.homescreen.RequestCard
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.MyReservationsTab
import dev.spiffocode.sigesmobile.viewmodel.MyReservationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    showBackButton: Boolean = false,
    viewModel: MyReservationsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToNewRequest: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    MyReservationsScreen(
        isLoading = state.isLoading,
        reservations = state.reservations,
        selectedTab = state.selectedTab,
        selectedReservableId = state.selectedReservableId,
        totalPages = state.totalPages,
        currentPage = state.currentPage,
        error = state.error,
        selectTab = viewModel::selectTab,
        filterByReservable = viewModel::filterByReservable,
        loadPage = viewModel::loadPage,
        showBackButton = showBackButton,
        onNavigateBack = onNavigateBack,
        onNavigateToNewRequest = onNavigateToNewRequest,
        onNavigateToDetail = onNavigateToDetail
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    isLoading: Boolean,
    reservations: List<ReservationResponse>,
    selectedTab: MyReservationsTab,
    selectedReservableId: Long?,
    totalPages: Int,
    currentPage: Int,
    error: String?,
    selectTab: (MyReservationsTab) -> Unit = {},
    filterByReservable: (Long?) -> Unit = {},
    loadPage: (Int) -> Unit = {},
    showBackButton: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onNavigateToNewRequest: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {}
) {

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

            // Resource Filter
            var expandedFilter by remember { mutableStateOf(false) }

            FilterSelector(
                value = if (selectedReservableId == null) "Todos los recursos" else "Recurso ID: ${selectedReservableId}",
                expanded = expandedFilter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onExpandedChange = {expandedFilter = it},
            ) {
                DropdownMenuItem(
                    text = { Text("Todos los recursos") },
                    onClick = {
                        filterByReservable(null)
                        expandedFilter = false
                    }
                )
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

                InfiniteScrollList(
                    elements = reservations,
                    key = { _, res -> res.id },
                    loadMoreItems = { loadPage(currentPage + 1) },
                    hasNextPage = hasNextPage,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    content = { reservation ->
                        val reservableTypeDisplay = when (reservation.reservable?.reservableType) {
                            ReservableType.SPACE -> "Espacio"
                            ReservableType.EQUIPMENT -> "Equipo"
                            null -> "--"
                        }

                        // Calculate duration heuristically based on start and end times assuming same day
                        val durationMins = java.time.Duration.between(reservation.startTime, reservation.endTime).toMinutes()
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
                                kotlinx.datetime.LocalDateTime(it.year, it.monthValue, it.dayOfMonth, it.hour, it.minute)
                            },
                            endDateTime = reservation.date.atTime(reservation.endTime).let {
                                kotlinx.datetime.LocalDateTime(it.year, it.monthValue, it.dayOfMonth, it.hour, it.minute)
                            },
                            status = reservation.status,
                            meta1 = reservableTypeDisplay,
                            meta2 = durationDisplay,
                            onClick = { onNavigateToDetail(reservation.id) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun MyReservationsScreenPreviewEmpty(){
    SigesmobileTheme {
        MyReservationsScreen(
            isLoading = false,
            reservations = emptyList(),
            selectedTab = MyReservationsTab.ALL,
            selectedReservableId = null,
            totalPages = 1,
            currentPage = 0,
            error = null
        )
    }
}
