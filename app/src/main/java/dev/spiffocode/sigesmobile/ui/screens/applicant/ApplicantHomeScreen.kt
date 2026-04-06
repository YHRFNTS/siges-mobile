package dev.spiffocode.sigesmobile.ui.screens.applicant

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.UserRole
import dev.spiffocode.sigesmobile.ui.components.homescreen.AvailableItemCard
import dev.spiffocode.sigesmobile.ui.components.homescreen.HomeHeader
import dev.spiffocode.sigesmobile.ui.components.homescreen.QuickActionsGrid
import dev.spiffocode.sigesmobile.ui.components.homescreen.RequestCard
import dev.spiffocode.sigesmobile.ui.components.homescreen.SectionHeader
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.AvailableResourceUIItem
import dev.spiffocode.sigesmobile.viewmodel.HomeViewModel
import dev.spiffocode.sigesmobile.viewmodel.NotificationsViewModel
import dev.spiffocode.sigesmobile.viewmodel.ReservationUIItem
import kotlinx.datetime.LocalDateTime
import java.util.Collections.emptyList

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import dev.spiffocode.sigesmobile.ui.components.homescreen.ResponsiveGrid

@Composable
fun ApplicantHomeScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: HomeViewModel = hiltViewModel(),
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    onNavigateToAvailability: () -> Unit = {},
    onNavigateToNewRequest: () -> Unit = {},
    onNavigateToMyRequests: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    val notifState by notificationsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadHome() }

    val columns = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        WindowWidthSizeClass.Expanded -> 3
        else -> 1
    }

    ApplicantHomeScreen(
        columns = columns,
        userName = state.userName,
        userRole = state.userRole,
        isLoading = state.isLoading,
        myRecentReservations = state.myRecentReservations,
        availableSpaces = state.availableResources,
        error = state.error,
        notifications = notifState.notifications,
        hasNextNotificationPage = notifState.hasNextPage,
        onClickNotification = notificationsViewModel::onClick,
        markAllNotificationsAsRead = notificationsViewModel::markAllRead,
        onLoadMoreNotifications = notificationsViewModel::loadNextPage,
        onNavigateToAvailability = onNavigateToAvailability,
        onNavigateToNewRequest = onNavigateToNewRequest,
        onNavigateToMyRequests = onNavigateToMyRequests,
        onNavigateToDetail = onNavigateToDetail
    )

}


@Composable
fun ApplicantHomeScreen(
    columns: Int = 1,
    userName: String,
    userRole: UserRole,
    isLoading: Boolean,
    myRecentReservations: List<ReservationUIItem>,
    availableSpaces: List<AvailableResourceUIItem>,
    error: String?,
    notifications: List<NotificationResponse>,
    hasNextNotificationPage: Boolean,
    onClickNotification: (NotificationResponse) -> Unit = {},
    markAllNotificationsAsRead: () -> Unit = {},
    onLoadMoreNotifications: () -> Unit = {},
    onNavigateToAvailability: () -> Unit = {},
    onNavigateToNewRequest: () -> Unit = {},
    onNavigateToMyRequests: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        HomeHeader(
            userName         = userName,
            userRole         = userRole,
            notifications    = notifications,
            notificationsHasNextPage = hasNextNotificationPage,
            onNotificationClick = { notification ->
                onClickNotification(notification)
                val resId = notification.reservation?.id ?: notification.metadata?.reservationId
                if (resId != null) {
                    onNavigateToDetail(resId)
                }
            },
            onMarkAllNotificationsRead = markAllNotificationsAsRead,
            onLoadMoreNotifications = onLoadMoreNotifications
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-44).dp)
        ) {
            QuickActionsGrid(
                onNavigateToAvailability = onNavigateToAvailability,
                onNavigateToNewRequest = onNavigateToNewRequest,
                onNavigateToMyRequests = onNavigateToMyRequests
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
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    }
                }
                myRecentReservations.isEmpty() -> {
                    Text(
                        text     = "No tienes solicitudes recientes.",
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
                else -> {
                    ResponsiveGrid(
                        items = myRecentReservations,
                        columns = columns,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) { reservation ->
                        RequestCard(
                            title       = reservation.title,
                            startDateTime = reservation.dateStart,
                            endDateTime = reservation.dateEnd,
                            status      = reservation.status,
                            meta1       = reservation.meta1,
                            meta2       = reservation.meta2,
                            onClick     = { onNavigateToDetail(reservation.id) }
                        )
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
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            } else {
                ResponsiveGrid(
                    items = availableSpaces,
                    columns = columns,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) { resource ->
                    AvailableItemCard(
                        title  = resource.title,
                        meta   = resource.meta,
                        status = resource.status,
                        resourceType = resource.reservableType,
                        resourceCategory = resource.category
                    )
                }
            }

            error?.let { error ->
                Text(
                    text     = error,
                    color    = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
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
            userName = "John Doe",
            userRole = UserRole.STUDENT,
            isLoading = false,
            myRecentReservations = emptyList(),
            error = null,
            availableSpaces = emptyList(),
            notifications = emptyList(),
            hasNextNotificationPage = false
        )
    }
}


@Preview(showBackground = true, name = "Home with reservations")
@Composable
fun ApplicantHomeScreenWithReservations() {
    SigesmobileTheme {
        ApplicantHomeScreen(
            userName         = "John Doe",
            userRole         = UserRole.STUDENT,
            isLoading        = false,
            myRecentReservations = listOf(
                ReservationUIItem(
                    id = 1,
                    title = "Aula 1",
                    dateStart = LocalDateTime(2026, 1, 28, 10, 0),
                    dateEnd = LocalDateTime(2026, 1, 28, 12, 0),
                    status = ReservationStatus.PENDING,
                    meta1 = "10:00 - 11:00",
                    meta2 = "Edificio 1",
                    petitionerRole = UserRole.STUDENT,
                    petitionerName = "José"
                )
            ),
            error = null,
            availableSpaces = emptyList(),
            notifications = emptyList(),
            hasNextNotificationPage = false
        )
    }
}


@Preview(showBackground = true, name = "Home with spaces")
@Composable
fun ApplicantHomeScreenWithSpaces() {
    SigesmobileTheme {
        ApplicantHomeScreen(
            userName = "John Doe",
            userRole = UserRole.STUDENT,
            isLoading = false,
            availableSpaces = listOf(
                AvailableResourceUIItem(
                    title = "Lab de cómputo 1",
                    meta = "Capacidad: 30 personas",
                    status = ReservableStatus.AVAILABLE,
                    reservableType = ReservableType.SPACE,
                    category = "Aulas"
                )
            ),
            error = null,
            myRecentReservations = emptyList(),
            notifications = emptyList(),
            hasNextNotificationPage = false,
        )
    }
}