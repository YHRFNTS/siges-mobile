package dev.spiffocode.sigesmobile.ui.screens.applicant

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.ui.components.homescreen.AvailableItemCard
import dev.spiffocode.sigesmobile.ui.components.homescreen.HomeHeader
import dev.spiffocode.sigesmobile.ui.components.homescreen.RequestCard
import dev.spiffocode.sigesmobile.ui.components.homescreen.SectionHeader
import dev.spiffocode.sigesmobile.ui.theme.Background
import dev.spiffocode.sigesmobile.ui.theme.Plum
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.ui.theme.TextSecondary
import dev.spiffocode.sigesmobile.viewmodel.AvailableResourceUIItem
import dev.spiffocode.sigesmobile.viewmodel.HomeViewModel
import dev.spiffocode.sigesmobile.viewmodel.NotificationsViewModel
import dev.spiffocode.sigesmobile.viewmodel.ReservationUIItem
import java.sql.Date

@Composable
fun ApplicantHomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAvailability: () -> Unit = {},
    onNavigateToNewRequest: () -> Unit = {},
    onNavigateToMyRequests: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadHome() }

    ApplicantHomeScreen(
        userName = state.userName,
        userRole = state.userRole,
        isLoading = state.isLoading,
        myRecentReservations = state.myRecentReservations,
        availableSpaces = state.availableResources,
        error = state.error,
        onNavigateToAvailability = onNavigateToAvailability,
        onNavigateToNewRequest = onNavigateToNewRequest,
        onNavigateToMyRequests = onNavigateToMyRequests,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToNotifications = onNavigateToNotifications
    )

}


@Composable
fun ApplicantHomeScreen(
    userName: String,
    userRole: String,
    isLoading: Boolean,
    myRecentReservations: List<ReservationUIItem>,
    availableSpaces: List<AvailableResourceUIItem>,
    error: String?,
    onNavigateToAvailability: () -> Unit = {},
    onNavigateToNewRequest: () -> Unit = {},
    onNavigateToMyRequests: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    val activity = LocalActivity.current as ComponentActivity
    val notificationsViewModel: NotificationsViewModel = viewModel(activity)

    val notifState by notificationsViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
    ) {
        HomeHeader(
            userName         = userName,
            userRole         = userRole,
            notifications    = notifState.notifications,
            notificationsHasNextPage = notifState.hasNextPage,
            onNotificationClick = {notificationsViewModel.onClick(it)},
            onMarkAllNotificationsRead = {notificationsViewModel.markAllRead()},
            onLoadMoreNotifications = {notificationsViewModel.loadNextPage()}
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-44).dp)
        ) {
            QuickActionsGrid(
                onNavigateToAvailability = onNavigateToAvailability,
                onNavigateToNewRequest   = onNavigateToNewRequest,
                onNavigateToMyRequests   = onNavigateToMyRequests
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(
                title         = "Mis Solicitudes",
                actionText    = "Ver todas",
                onActionClick = onNavigateToMyRequests
            )

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Plum, modifier = Modifier.size(24.dp))
                    }
                }
                myRecentReservations.isEmpty() -> {
                    Text(
                        text     = "No tienes solicitudes recientes.",
                        color    = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
                else -> {
                    Column(
                        modifier            = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        myRecentReservations.forEach { reservation ->
                            RequestCard(
                                title       = reservation.title,
                                date        = reservation.date,
                                status      = reservation.status,
                                meta1       = reservation.meta1,
                                meta2       = reservation.meta2,
                                onClick     = { onNavigateToDetail(reservation.id) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title         = "Disponible Ahora",
                actionText    = "Ver todo",
                onActionClick = onNavigateToAvailability
            )

            if (availableSpaces.isEmpty() && !isLoading) {
                Text(
                    text     = "No hay recursos disponibles en este momento.",
                    color    = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            } else {
                Column(
                    modifier            = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    availableSpaces.forEach {
                        AvailableItemCard(
                            title  = it.title,
                            meta   = it.meta,
                            status = it.status,
                            resourceType = it.reservableType,
                            resourceCategory = it.category
                        )
                    }
                }
            }

            error?.let { error ->
                Text(
                    text     = error,
                    color    = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, name = "Default Home Screen")
@Composable
fun ApplicantHomeScreenPreview() {
    SigesmobileTheme {
        ApplicantHomeScreen(
            userName         = "John Doe",
            userRole         = "Estudiante",
            isLoading        = false,
            myRecentReservations = emptyList(),
            availableSpaces = emptyList(),
            error = null
        )
    }
}


@Preview(showBackground = true, name = "Home with reservations")
@Composable
fun ApplicantHomeScreenWithReservations() {
    SigesmobileTheme {
        ApplicantHomeScreen(
            userName         = "John Doe",
            userRole         = "Estudiante",
            isLoading        = false,
            myRecentReservations = listOf(
                ReservationUIItem(
                    id = 1,
                    title = "Aula 1",
                    date = Date.valueOf("2026-06-25").toString(),
                    status = ReservationStatus.PENDING,
                    meta1 = "10:00 - 11:00",
                    meta2 = "Edificio 1"
                )
            ),
            availableSpaces = emptyList(),
            error = null
        )
    }
}


@Preview(showBackground = true, name = "Home with spaces")
@Composable
fun ApplicantHomeScreenWithSpaces() {
    SigesmobileTheme {
        ApplicantHomeScreen(
            userName         = "John Doe",
            userRole         = "Estudiante",
            isLoading        = false,
            myRecentReservations = emptyList(),
            availableSpaces = listOf(
                AvailableResourceUIItem(
                    title = "Lab de cómputo 1",
                    meta = "Capacidad: 30 personas",
                    status = ReservableStatus.AVAILABLE,
                    reservableType = ReservableType.SPACE,
                    category = "Aulas"
                )
            ),
            error = null
        )
    }
}