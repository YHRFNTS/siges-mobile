package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.ui.components.InfiniteScrollList
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
fun NotificationsList(
    expanded: Boolean = false,
    width: Dp = 300.dp,
    maxHeight: Dp = 400.dp,
    shape: CornerBasedShape = MaterialTheme.shapes.large,
    notifications: List<NotificationResponse>,
    hasNextPage: Boolean,
    onMarkAllRead: () -> Unit = {},
    onExpandedChange: (Boolean) -> Unit = {},
    onNotificationClick: (NotificationResponse) -> Unit = {}
) {

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandedChange(false) },
        modifier = Modifier
            .width(width)
            .clip(shape)
            .heightIn(max = maxHeight)
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
                modifier = Modifier.heightIn(max = 340.dp),
                elements = notifications,
                key = { _, notif -> notif.id},
                loadMoreItems = { },
                hasNextPage = hasNextPage
            ) { notif ->
                NotificationItem(notif, onClick = { onNotificationClick(notif) })
                HorizontalDivider()


                if (hasNextPage) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
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
        )
    }
}

