package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationMetadata
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationReadStatus
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationType
import dev.spiffocode.sigesmobile.ui.helpers.toHumanString
import dev.spiffocode.sigesmobile.ui.navigation.Routes
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime


@Composable
fun NotificationItem(
    notification: NotificationResponse,
    onClick: () -> Unit = {},
    navController: NavController = rememberNavController()
) {
    DropdownMenuItem(
        text = {
            Column {
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
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = notification.sentAt.toKotlinLocalDateTime().toHumanString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        onClick = {
            onClick()

            when (notification.type) {
                NotificationType.RESERVATION_RESCHEDULE,
                NotificationType.COMMENT_ON_RESERVATION,
                NotificationType.RESERVATION_REMINDER,
                NotificationType.RESERVATION_CREATED,
                NotificationType.RESERVATION_APPROVED,
                NotificationType.RESERVATION_REJECTED,
                NotificationType.RESERVATION_CANCELLED -> notification.reservation?.id?.let {
                    navController.navigate(
                        Routes.requestDetail(it)
                    )
                }

                NotificationType.PASSWORD_CHANGED,
                NotificationType.LOGIN_NEW_DEVICE -> navController.navigate(Routes.PROFILE)
            }
        }
    )
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