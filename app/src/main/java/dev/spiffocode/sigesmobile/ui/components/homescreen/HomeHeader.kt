package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.UserRole
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
public fun HomeHeader(
    welcomeMessage: String = "Hola de nuevo,",
    userName: String,
    userRole: UserRole,
    notifications: List<NotificationResponse>,
    unreadCount: Int,
    notificationsHasNextPage: Boolean,
    onNotificationClick: (NotificationResponse) -> Unit = {},
    onMarkAllNotificationsRead: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onLoadMoreNotifications: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(colors = listOf(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.secondaryContainer))
            )
            .padding(top = 56.dp, bottom = 64.dp, start = 24.dp, end = 24.dp)
    ) {
        Column {
            Row(
                modifier             = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment    = Alignment.CenterVertically
            ) {
                Column {
                    Text(welcomeMessage, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text       = userName.ifBlank { "—" },
                        style      = MaterialTheme.typography.headlineLarge,
                        color      = MaterialTheme.colorScheme.onSurface,
                    )
                }
                NotificationsButton(
                    notifications = notifications,
                    unreadCount = unreadCount,
                    hasNextPage  = notificationsHasNextPage,
                    onNotificationClick = onNotificationClick,
                    onMarkAllRead = onMarkAllNotificationsRead,
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToProfile = onNavigateToProfile,
                    onLoadMoreItems = onLoadMoreNotifications
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            UserRoleChip(userRole = userRole)
        }
    }
}

@Composable
@Preview
fun HomeHeaderPreview(){
    SigesmobileTheme {
        HomeHeader(
            userName = "Caryuter",
            userRole = UserRole.STUDENT,
            notifications = emptyList(),
            unreadCount = 0,
            notificationsHasNextPage = false
        )
    }
}