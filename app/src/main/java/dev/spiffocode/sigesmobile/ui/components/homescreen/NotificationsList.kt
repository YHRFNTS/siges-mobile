package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
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
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationMetadata
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationReadStatus
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationType
import dev.spiffocode.sigesmobile.ui.components.InfiniteScrollList
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import java.time.LocalDateTime
import kotlin.random.Random

@Composable
fun NotificationsList(
    expanded: Boolean = false,
    width: Dp = 300.dp,
    maxHeight: Dp = 400.dp,
    height: Dp = maxHeight - 60.dp,
    shape: CornerBasedShape = MaterialTheme.shapes.extraLarge,
    notifications: List<NotificationResponse>,
    hasNextPage: Boolean,
    onMarkAllRead: () -> Unit = {},
    onExpandedChange: (Boolean) -> Unit = {},
    onNotificationClick: (NotificationResponse) -> Unit = {},
    onLoadMoreItems: () -> Unit = {}
) {
    val itemHeight = 64.dp
    val headerHeight = 60.dp
    val loaderHeight = if (hasNextPage) 44.dp else 0.dp

    val calculatedHeight = remember(notifications.size, hasNextPage) {
        if (notifications.isEmpty()) {
            140.dp // High enough for the message and padding
        } else {
            val listHeight = itemHeight * notifications.size.coerceAtMost(5) + loaderHeight
            (headerHeight + listHeight).coerceAtMost(maxHeight)
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandedChange(false) },
        modifier = Modifier
            .width(width)
            .clip(shape)
            .height(calculatedHeight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Notificaciones", style = MaterialTheme.typography.titleMedium)
            if (notifications.isNotEmpty()) {
                Text(
                    text = "marcar todas como leídas",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable { onMarkAllRead() }
                )
            }
        }

        HorizontalDivider()

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes notificaciones pendientes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            InfiniteScrollList(
                modifier = Modifier
                    .width(width)
                    .height(calculatedHeight - headerHeight),
                elements = notifications,
                key = { _, notif -> notif.id},
                loadMoreItems = onLoadMoreItems,
                hasNextPage = hasNextPage
            ) { notif ->
                NotificationItem(notif, onClick = { onNotificationClick(notif) })
                HorizontalDivider()
            }
        }
    }
}


@Composable
@Preview
fun NotificationsListEmptyPreview(){

    SigesmobileTheme {
        NotificationsList(
            notifications = emptyList(),
            hasNextPage = false,
            expanded = true
        )
    }
}


@Composable
@Preview
fun NotificationsListWithNotificationsPreview(){

    SigesmobileTheme {
        NotificationsList(
            notifications = createMockNotification(1),
            hasNextPage = false,
            expanded = true
        )
    }
}




@Composable
@Preview
fun NotificationsListWithManyNotificationsPreview(){

    var notifications by remember { mutableStateOf(createMockNotification(5)) }

    SigesmobileTheme {
        NotificationsList(
            notifications = notifications,
            hasNextPage = true,
            expanded = true,
            onLoadMoreItems = {notifications + createMockNotification(5)}
        )
    }
}


private fun createMockNotification(size: Int): List<NotificationResponse>{
    return MutableList(size){
        NotificationResponse(
            id = Random.nextLong(),
            title = "Título de la notificación",
            message = "Se creó la reservación",
            readStatus = NotificationReadStatus.UNREAD,
            type = NotificationType.RESERVATION_CREATED,
            sentAt = LocalDateTime.now(),
            reservation = null,
            metadata = NotificationMetadata(
                issuedByName = "John Doe"
            )
        )
    }
}

