package dev.spiffocode.sigesmobile.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.UserRole
import dev.spiffocode.sigesmobile.ui.components.homescreen.DashboardMetrics
import dev.spiffocode.sigesmobile.ui.components.homescreen.HomeHeader
import dev.spiffocode.sigesmobile.ui.components.homescreen.RequestCard
import dev.spiffocode.sigesmobile.ui.components.homescreen.ResponsiveGrid
import dev.spiffocode.sigesmobile.ui.components.homescreen.SectionHeader
import dev.spiffocode.sigesmobile.ui.helpers.toText
import dev.spiffocode.sigesmobile.ui.theme.Plum
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.HomeViewModel
import dev.spiffocode.sigesmobile.viewmodel.NotificationsViewModel
import dev.spiffocode.sigesmobile.viewmodel.ReservationUIItem
import kotlinx.datetime.LocalDateTime

@Composable
fun AdminHomeScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: HomeViewModel = hiltViewModel(),
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    onNavigateToAllRequests: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCatalogs: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val notificationsState by notificationsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadHome() }

    val columns = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        WindowWidthSizeClass.Expanded -> 3
        else -> 1
    }

    AdminHomeScreen(
        columns = columns,
        userName = state.userName,
        userRole = state.userRole,
        notifications = notificationsState.notifications,
        unreadCount = notificationsState.unreadCount.toInt(),
        hasNextNotificationPage = notificationsState.hasNextPage,
        reservationsThisMonthCount = state.thisMonthCount,
        pendingReservationsCount = state.pendingCount,
        isLoading = state.isLoading,
        pendingReservations = state.pendingReservations,
        error = state.error,
        onClickNotification = notificationsViewModel::onClick,
        markAllNotificationsAsRead = notificationsViewModel::markAllRead,
        onLoadMoreNotifications = notificationsViewModel::loadNextPage,
        onRefresh = viewModel::loadHome,
        onNavigateToAllRequests = onNavigateToAllRequests,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToCatalogs = onNavigateToCatalogs
    )
}


@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    columns: Int = 1,
    userName: String,
    userRole: UserRole,
    notifications: List<NotificationResponse>,
    unreadCount: Int,
    hasNextNotificationPage: Boolean,
    reservationsThisMonthCount: Int,
    pendingReservationsCount: Int,
    isLoading: Boolean,
    pendingReservations: List<ReservationUIItem>,
    error: String?,
    onClickNotification: (NotificationResponse) -> Unit = {},
    markAllNotificationsAsRead: () -> Unit = {},
    onLoadMoreNotifications: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onNavigateToAllRequests: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCatalogs: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
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
            unreadCount      = unreadCount,
            notificationsHasNextPage = hasNextNotificationPage,
            onNotificationClick = {onClickNotification(it)},
            onMarkAllNotificationsRead = markAllNotificationsAsRead,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToProfile = onNavigateToProfile,
            onLoadMoreNotifications = onLoadMoreNotifications
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-44).dp)
        ) {
            DashboardMetrics(
                pendingReservationsCount = pendingReservationsCount,
                reservationsThisMonthCount = reservationsThisMonthCount
            )

            Spacer(modifier = Modifier.height(16.dp))

            androidx.compose.material3.OutlinedButton(
                onClick = onNavigateToCatalogs,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ) {
                androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Default.androidx.compose.material.icons.filled.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gestionar Catálogos")
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Solicitudes Pendientes",
                actionText = "Ver todas",
                onActionClick = onNavigateToAllRequests
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Plum, modifier = Modifier.size(24.dp))
                    }
                }

                pendingReservations.isEmpty() -> {
                    Text(
                        text = "No hay solicitudes pendientes.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                else -> {
                    ResponsiveGrid(
                        items = pendingReservations,
                        columns = columns,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) { reservation ->
                        RequestCard(
                            title = reservation.title,
                            startDateTime = reservation.dateStart,
                            endDateTime = reservation.dateEnd,
                            requesterName = reservation.petitionerName,
                            requesterRole = reservation.petitionerRole.toText(),
                            status = reservation.status,
                            meta1 = reservation.meta1,
                            meta2 = reservation.meta2,
                            onClick = { onNavigateToDetail(reservation.id) }
                        )
                    }
                }
            }

            error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminHomeScreenPreview() {
    SigesmobileTheme {
        AdminHomeScreen(
            userName = "John Doe",
            userRole = UserRole.STUDENT,
            isLoading = false,
            error = null,
            hasNextNotificationPage = false,
            reservationsThisMonthCount = 3,
            pendingReservationsCount = 1,
            notifications = emptyList(),
            unreadCount = 0,
            pendingReservations = emptyList()
        )
    }
}



@Preview(showBackground = true, name = "Home with reservations")
@Composable
fun AdminHomeScreenWithPendingReservations() {
    SigesmobileTheme {
        AdminHomeScreen(
            userName = "John Doe",
            userRole = UserRole.ADMIN,
            isLoading = false,
            error = null,
            hasNextNotificationPage = false,
            reservationsThisMonthCount = 3,
            pendingReservationsCount = 1,
            notifications = emptyList(),
            unreadCount = 0,
            pendingReservations = listOf(
                ReservationUIItem(
                    id = 1,
                    title = "Aula 1",
                    dateStart = LocalDateTime(2026, 1, 28, 10, 0),
                    dateEnd = LocalDateTime(2026, 1, 28, 12, 0),
                    status = ReservationStatus.PENDING,
                    meta1 = "10:00 - 11:00",
                    meta2 = "Edificio 1",
                    petitionerRole = UserRole.STUDENT,
                    petitionerName = "Carlos Emanuel Salgado Trujillo"
                )
            )
        )
    }
}


@Preview(showBackground = true, name = "Home with reservations (Dark Theme)")
@Composable
fun AdminHomeScreenWithPending_Dark_Reservations() {
    SigesmobileTheme(darkTheme = true) {
        AdminHomeScreen(
            userName = "John Doe",
            userRole = UserRole.ADMIN,
            isLoading = false,
            error = null,
            hasNextNotificationPage = false,
            reservationsThisMonthCount = 3,
            pendingReservationsCount = 1,
            unreadCount = 0,
            notifications = emptyList(),
            pendingReservations = listOf(
                ReservationUIItem(
                    id = 1,
                    title = "Aula 1",
                    dateStart = LocalDateTime(2026, 1, 28, 10, 0),
                    dateEnd = LocalDateTime(2026, 1, 28, 12, 0),
                    status = ReservationStatus.PENDING,
                    meta1 = "10:00 - 11:00",
                    meta2 = "Edificio 1",
                    petitionerRole = UserRole.STUDENT,
                    petitionerName = "Carlos Emanuel Salgado Trujillo"
                )
            )
        )
    }
}