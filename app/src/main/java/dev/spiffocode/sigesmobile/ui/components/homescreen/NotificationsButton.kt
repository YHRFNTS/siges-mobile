package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationReadStatus
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationType
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import java.time.LocalDateTime

@Composable
fun NotificationsButton(
    notifications: List<NotificationResponse>,
    unreadCount: Int,
    hasNextPage: Boolean,
    onNotificationClick: (NotificationResponse) -> Unit = {},
    onMarkAllRead: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onLoadMoreItems: () -> Unit = {},
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    padding: Dp = 20.dp,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                .clickable { expanded = !expanded },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Notifications,
                contentDescription = "Notificaciones",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(padding)
            )
        }

        if (unreadCount > 0) {
            Badge(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-2).dp)
            ) {
                Text(
                    text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        NotificationsList(
            onNotificationClick = onNotificationClick,
            onMarkAllRead = onMarkAllRead,
            notifications = notifications,
            expanded = expanded,
            onExpandedChange = { expanded = it },
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToProfile = onNavigateToProfile,
            hasNextPage = hasNextPage,
            onLoadMoreItems = onLoadMoreItems
        )
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xffe1cfff)
fun NotificationsButtonEmptyPreview(){
    SigesmobileTheme {
        NotificationsButton(
            notifications = emptyList(),
            hasNextPage = false,
            unreadCount = 0
        )
    }
}


@Composable
@Preview(showBackground = true, backgroundColor = 0xffe1cfff)
fun NotificationsButtonOnePagePreview(){
    SigesmobileTheme(darkTheme = true) {
        NotificationsButton(
            notifications = listOf(
                NotificationResponse(
                    id = 1,
                    title = "Título de la notificación",
                    message = "Se creó la reservación",
                    readStatus = NotificationReadStatus.UNREAD,
                    type = NotificationType.RESERVATION_CREATED,
                    sentAt = LocalDateTime.now(),
                    reservation = null,
                    metadata = null
                )
            ),
            hasNextPage = false,
            unreadCount = 1
        )
    }
}