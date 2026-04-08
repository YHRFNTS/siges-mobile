package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationMetadata
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationReadStatus
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationType
import dev.spiffocode.sigesmobile.ui.helpers.toHumanString
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime

@Composable
fun NotificationItem(
    notification: NotificationResponse,
    onClick: () -> Unit = {}
) {
    val isUnread = notification.readStatus == NotificationReadStatus.UNREAD
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isUnread) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isUnread) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(Modifier.width(12.dp))
        } else {
            Spacer(Modifier.width(20.dp)) // Maintain alignment
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                buildAnnotatedString {
                    notification.metadata?.issuedByName?.let {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(it)
                        }
                        append(" -")
                    }
                    append(" ${notification.message}")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUnread) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = notification.sentAt.toKotlinLocalDateTime().toHumanString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun NotificationItemPreview(){
    SigesmobileTheme {
        NotificationItem(
            notification = NotificationResponse(
                id = 1,
                title = "Título de la notificación",
                message = "Se creó la reservación",
                readStatus = NotificationReadStatus.UNREAD,
                type = NotificationType.RESERVATION_CREATED,
                sentAt = LocalDateTime.now(),
                reservation = null,
                metadata = NotificationMetadata(
                    issuedByName = "John Doe"
                )
            ),
        )
    }
}