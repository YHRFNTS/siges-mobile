package dev.spiffocode.sigesmobile.ui.screens.admin

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.UserRole
import dev.spiffocode.sigesmobile.ui.components.homescreen.AdminPendingCard
import dev.spiffocode.sigesmobile.ui.components.homescreen.DashboardMetrics
import dev.spiffocode.sigesmobile.ui.components.homescreen.HomeHeader
import dev.spiffocode.sigesmobile.ui.components.homescreen.SectionHeader
import dev.spiffocode.sigesmobile.ui.theme.Background
import dev.spiffocode.sigesmobile.ui.theme.Plum
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.ui.theme.TextSecondary
import dev.spiffocode.sigesmobile.viewmodel.HomeViewModel
import dev.spiffocode.sigesmobile.viewmodel.NotificationsViewModel
import dev.spiffocode.sigesmobile.viewmodel.ReservationUIItem
import java.sql.Date

@Composable
fun AdminHomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    onNavigateToAllRequests: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val notificationsState by notificationsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadHome() }
    AdminHomeScreen(
        userName = state.userName,
        userRole = state.userRole,
        notifications = notificationsState.notifications,
        hasNextNotificationPage = notificationsState.hasNextPage,
        reservationsThisMonthCount = state.thisMonthCount,
        pendingReservationsCount = state.pendingCount,
        isLoading = state.isLoading,
        pendingReservations = state.pendingReservations,
        error = state.error,
        onClickNotification = notificationsViewModel::onClick,
        markAllNotificationsAsRead = notificationsViewModel::markAllRead,
        onLoadMoreNotifications = notificationsViewModel::loadNextPage,
        onNavigateToAllRequests = onNavigateToAllRequests,
        onNavigateToDetail = onNavigateToDetail
    )
}


@Composable
fun AdminHomeScreen(
    userName: String,
    userRole: UserRole,
    notifications: List<NotificationResponse>,
    hasNextNotificationPage: Boolean,
    reservationsThisMonthCount: Int,
    pendingReservationsCount: Int,
    isLoading: Boolean,
    pendingReservations: List<ReservationUIItem>,
    error: String?,
    onClickNotification: (NotificationResponse) -> Unit = {},
    markAllNotificationsAsRead: () -> Unit = {},
    onLoadMoreNotifications: () -> Unit = {},
    onNavigateToAllRequests: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
    ) {

        HomeHeader(
            userName         = userName,
            userRole         = userRole,
            notifications    = notifications,
            notificationsHasNextPage = hasNextNotificationPage,
            onNotificationClick = {onClickNotification(it)},
            onMarkAllNotificationsRead = markAllNotificationsAsRead,
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

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title         = "Solicitudes Pendientes",
                actionText    = "Ver todas",
                onActionClick = onNavigateToAllRequests
            )

            when {
                isLoading -> {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Plum, modifier = Modifier.size(24.dp))
                    }
                }
                pendingReservations.isEmpty() -> {
                    Text(
                        text     = "No hay solicitudes pendientes.",
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
                        pendingReservations.forEach { reservation ->
                            AdminPendingCard(
                                reservation = reservation,
                                onClick     = { onNavigateToDetail(reservation.id) }
                            )
                        }
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
            pendingReservations = listOf(
                ReservationUIItem(
                    id = 1,
                    title = "Aula 1",
                    date = Date.valueOf("2026-06-25").toString(),
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