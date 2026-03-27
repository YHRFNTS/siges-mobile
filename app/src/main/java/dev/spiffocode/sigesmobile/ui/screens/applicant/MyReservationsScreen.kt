package dev.spiffocode.sigesmobile.ui.screens.applicant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.ui.components.InfiniteScrollList
import dev.spiffocode.sigesmobile.ui.components.homescreen.RequestCard
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
            val selectedTabIndex = when (state.selectedTab) {
                MyReservationsTab.ALL -> 0
                MyReservationsTab.PENDING -> 1
                MyReservationsTab.APPROVED -> 2
            }

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 24.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = state.selectedTab == MyReservationsTab.ALL,
                    onClick = { viewModel.selectTab(MyReservationsTab.ALL) },
                    text = { Text("Todas", fontWeight = if (state.selectedTab == MyReservationsTab.ALL) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = state.selectedTab == MyReservationsTab.PENDING,
                    onClick = { viewModel.selectTab(MyReservationsTab.PENDING) },
                    text = { Text("Pendientes", fontWeight = if (state.selectedTab == MyReservationsTab.PENDING) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = state.selectedTab == MyReservationsTab.APPROVED,
                    onClick = { viewModel.selectTab(MyReservationsTab.APPROVED) },
                    text = { Text("Aprobadas", fontWeight = if (state.selectedTab == MyReservationsTab.APPROVED) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resource Filter
            var expandedFilter by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedFilter,
                onExpandedChange = { expandedFilter = !expandedFilter },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                OutlinedTextField(
                    value = if (state.selectedReservableId == null) "Todos los recursos" else "Recurso ID: ${state.selectedReservableId}",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFilter) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedFilter,
                    onDismissRequest = { expandedFilter = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todos los recursos") },
                        onClick = {
                            viewModel.filterByReservable(null)
                            expandedFilter = false
                        }
                    )
                    // At the moment MyReservationsViewModel doesn't expose a list of reservables to pick from,
                    // so we only provide the reset option based on the API limits.
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content List
            if (state.isLoading && state.currentPage == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                }
            } else if (state.reservations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron solicitudes", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val hasNextPage = state.currentPage < (state.totalPages - 1)

                InfiniteScrollList(
                    elements = state.reservations,
                    key = { _, res -> res.id },
                    loadMoreItems = { viewModel.loadPage(state.currentPage + 1) },
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
