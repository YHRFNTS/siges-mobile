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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.ui.theme.Lav
import dev.spiffocode.sigesmobile.ui.theme.Plum
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.ui.theme.Sky
import dev.spiffocode.sigesmobile.ui.theme.Slate
import dev.spiffocode.sigesmobile.ui.theme.TextPrimary

@Composable
public fun HomeHeader(
    userName: String,
    userRole: String,
    notifications: List<NotificationResponse>,
    notificationsHasNextPage: Boolean,
    onNotificationClick: (NotificationResponse) -> Unit = {},
    onMarkAllNotificationsRead: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(colors = listOf(Lav, Sky)))
            .padding(top = 56.dp, bottom = 64.dp, start = 24.dp, end = 24.dp)
    ) {
        Column {
            Row(
                modifier             = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment    = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hola de nuevo,", fontSize = 13.sp, color = Slate)
                    Text(
                        text       = userName.ifBlank { "—" },
                        fontSize   = 26.sp,
                        color      = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                NotificationsButton(
                    notifications = notifications,
                    hasNextPage  = notificationsHasNextPage,
                    onNotificationClick = onNotificationClick,
                    onMarkAllRead = onMarkAllNotificationsRead
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier          = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Plum, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(userRole, fontSize = 12.sp, color = Plum, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
@Preview
fun HomeHeaderPreview(){
    SigesmobileTheme {
        HomeHeader(
            userName = "Caryuter",
            userRole = "Student",
            notifications = emptyList(),
            notificationsHasNextPage = false
        )
    }
}