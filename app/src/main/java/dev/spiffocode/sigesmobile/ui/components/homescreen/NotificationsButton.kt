package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
    hasNextPage: Boolean,
    onNotificationClick: (NotificationResponse) -> Unit = {},
    onMarkAllRead: () -> Unit = {},
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
                .clickable { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Notifications,
                contentDescription = "Notificaciones",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(padding)
            )
            if (notifications.isNotEmpty()) {

                val errorColor = MaterialTheme.colorScheme.error

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-5).dp, y = 5.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        errorColor,
                                        errorColor.copy(alpha = 0.4f),
                                        errorColor.copy(alpha = 0f)
                                    ),
                                    center = Offset(size.width / 2f, size.height / 2f),
                                    radius = size.width / 2f
                                )
                            )
                        }
                )
            }
        }
        NotificationsList(
            onNotificationClick = onNotificationClick,
            onMarkAllRead = onMarkAllRead,
            notifications = notifications,
            expanded = expanded,
            hasNextPage = hasNextPage
        )
    }
}

@Composable
@Preview
fun NotificationsButtonEmptyPreview(){
    SigesmobileTheme {
        NotificationsButton(
            notifications = emptyList(),
            hasNextPage = false
        )
    }
}


@Composable
@Preview
fun NotificationsButtonOnePagePreview(){
    SigesmobileTheme {
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
            hasNextPage = false
        )
    }
}