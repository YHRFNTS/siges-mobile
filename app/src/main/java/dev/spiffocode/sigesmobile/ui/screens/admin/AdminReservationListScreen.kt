package dev.spiffocode.sigesmobile.ui.screens.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationType
import dev.spiffocode.sigesmobile.ui.components.FilterSelector
import dev.spiffocode.sigesmobile.ui.components.homescreen.RequestCard
import dev.spiffocode.sigesmobile.ui.helpers.toText
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.AdminReservationListUiState
import dev.spiffocode.sigesmobile.viewmodel.AdminReservationListViewModel
import dev.spiffocode.sigesmobile.viewmodel.AdminReservationTab
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime

@Composable
fun AdminReservationListScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: AdminReservationListViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    AdminReservationListScreen(
        windowSizeClass = windowSizeClass,
        state = state,
        onSelectTab = viewModel::selectTab,
        onFilterByReservable = viewModel::filterByReservable,
        onLoadPage = viewModel::loadPage,
        onNavigateToDetail = onNavigateToDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReservationListScreen(
    windowSizeClass: WindowSizeClass,
    state: AdminReservationListUiState,
    onSelectTab: (AdminReservationTab) -> Unit = {},
    onFilterByReservable: (Long?) -> Unit = {},
    onLoadPage: (Int) -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Todas las Solicitudes",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Tabs ─────────────────────────────────────────────────────────
            val selectedTabIndex = when (state.selectedTab) {
                AdminReservationTab.ALL      -> 0
                AdminReservationTab.PENDING  -> 1
                AdminReservationTab.RESOLVED -> 2
            }

            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.padding(horizontal = 24.dp),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                listOf(
                    "Todas"     to AdminReservationTab.ALL,
                    "Pendientes" to AdminReservationTab.PENDING,
                    "Resueltas"  to AdminReservationTab.RESOLVED
                ).forEach { (label, tab) ->
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick  = { onSelectTab(tab) },
                        text = {
                            Text(
                                label,
                                fontWeight = if (state.selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Resource filter ───────────────────────────────────────────────
            var expandedFilter by remember { mutableStateOf(false) }
            FilterSelector(
                value = if (state.selectedReservableId == null) "Todos los recursos"
                        else "Recurso ID: ${state.selectedReservableId}",
                expanded = expandedFilter,
                modifier = Modifier.padding(horizontal = 24.dp),
                onExpandedChange = { expandedFilter = it }
            ) {
                DropdownMenuItem(
                    text = { Text("Todos los recursos") },
                    onClick = {
                        onFilterByReservable(null)
                        expandedFilter = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Content ───────────────────────────────────────────────────────
            when {
                state.isLoading && state.reservations.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(state.error, color = MaterialTheme.colorScheme.error)
                    }
                }
                state.reservations.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No se encontraron solicitudes",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    val hasNextPage = state.currentPage < (state.totalPages - 1)
                    val columns = when (windowSizeClass.widthSizeClass) {
                        WindowWidthSizeClass.Compact -> 1
                        WindowWidthSizeClass.Medium -> 2
                        else -> 3
                    }

                    dev.spiffocode.sigesmobile.ui.components.InfiniteScrollGrid(
                        elements = state.reservations,
                        columns = columns,
                        key = { _, res -> res.id },
                        loadMoreItems = { onLoadPage(state.currentPage + 1) },
                        hasNextPage = hasNextPage,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) { reservation ->
                        val reservableTypeDisplay = when (reservation.reservable?.reservableType) {
                            ReservableType.SPACE     -> "Espacio"
                            ReservableType.EQUIPMENT -> "Equipo"
                            null                     -> "--"
                        }

                        val durationMins = java.time.Duration.between(
                            reservation.startTime, reservation.endTime
                        ).toMinutes()
                        val durationDisplay = if (durationMins >= 60) {
                            val h = durationMins / 60
                            val m = durationMins % 60
                            if (m == 0L) "$h horas" else "$h h $m min"
                        } else "$durationMins min"

                        val petitioner = reservation.petitioner
                        val petitionerName = petitioner?.let { "${it.firstName} ${it.lastName}" }
                        val petitionerRole = petitioner?.role?.toText()

                        RequestCard(
                            title       = reservation.reservable?.name ?: "Recurso no especificado",
                            startDateTime = reservation.date.atTime(reservation.startTime).let {
                                kotlinx.datetime.LocalDateTime(
                                    it.year, it.monthValue, it.dayOfMonth, it.hour, it.minute
                                )
                            },
                            endDateTime = reservation.date.atTime(reservation.endTime).let {
                                kotlinx.datetime.LocalDateTime(
                                    it.year, it.monthValue, it.dayOfMonth, it.hour, it.minute
                                )
                            },
                            status        = reservation.status,
                            meta1         = reservableTypeDisplay,
                            meta2         = durationDisplay,
                            requesterName = petitionerName,
                            requesterRole = petitionerRole,
                            onClick       = { onNavigateToDetail(reservation.id) },
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

// ───────────────────────────── Previews ──────────────────────────────────────

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Preview(showBackground = true)
@Composable
fun AdminReservationListScreenEmptyPreview() {
    SigesmobileTheme {
        AdminReservationListScreen(
            state = AdminReservationListUiState(isLoading = false),
            windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Preview(showBackground = true, name = "With reservations")
@Composable
fun AdminReservationListScreenPreview() {
    SigesmobileTheme {
        AdminReservationListScreen(

            windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
            state = AdminReservationListUiState(
                isLoading = false,
                reservations = listOf(
                    ReservationResponse(
                        id = 1,
                        reservable = ReservableDto(
                            id = 1,
                            name = "Sala de Juntas A",
                            reservableType = ReservableType.SPACE,
                            availableForStudents = true,
                            status = ReservableStatus.AVAILABLE
                        ),
                        date = LocalDate(2026, 1, 28).toJavaLocalDate(),
                        startTime = kotlinx.datetime.LocalTime(10, 0).toJavaLocalTime(),
                        endTime = kotlinx.datetime.LocalTime(12, 0).toJavaLocalTime(),
                        status = ReservationStatus.PENDING,
                        type = ReservationType.GROUP,
                        companions = 15
                    ),
                    ReservationResponse(
                        id = 2,
                        reservable = ReservableDto(
                            id = 2,
                            name = "Proyector HDMI (x3)",
                            reservableType = ReservableType.EQUIPMENT,
                            availableForStudents = true,
                            status = ReservableStatus.AVAILABLE
                        ),
                        date = LocalDate(2026, 1, 30).toJavaLocalDate(),
                        startTime = kotlinx.datetime.LocalTime(9, 0).toJavaLocalTime(),
                        endTime = kotlinx.datetime.LocalTime(13, 0).toJavaLocalTime(),
                        status = ReservationStatus.APPROVED,
                        type = ReservationType.SINGLE
                    )
                )
            )
        )
    }
}
