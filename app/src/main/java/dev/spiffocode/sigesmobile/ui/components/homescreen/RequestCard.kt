package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.ui.helpers.toBgColor
import dev.spiffocode.sigesmobile.ui.helpers.toColor
import dev.spiffocode.sigesmobile.ui.helpers.toText
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
fun RequestCard(
    title: String,
    date: String,
    status: ReservationStatus,
    meta1: String,
    meta2: String,
    onClick: () -> Unit = {}
) {
    val textPrimary = Color(0xFF2D3142)
    val textSecondary = Color(0xFF6B7280)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Text(date, fontSize = 12.sp, color = textSecondary)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(status.toBgColor())
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(status.toText(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = status.toColor())
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(meta1, fontSize = 12.sp, color = textSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(meta2, fontSize = 12.sp, color = textSecondary)
                }
            }
        }
    }
}

@Composable
@Preview
fun RequestCardPendingPreview() {
    SigesmobileTheme {
        RequestCard(
            title = "Aula 1",
            date = "2023-06-21",
            status = ReservationStatus.PENDING,
            meta1 = "10:00 - 11:00",
            meta2 = "Edificio 1"
        )
    }
}


@Composable
@Preview
fun RequestCardApprovedPreview() {
    SigesmobileTheme {
        RequestCard(
            title = "Aula 1",
            date = "2023-06-21",
            status = ReservationStatus.APPROVED,
            meta1 = "10:00 - 11:00",
            meta2 = "Edificio 1"
        )
    }
}


@Composable
@Preview
fun RequestCardRejectedPreview() {
    SigesmobileTheme {
        RequestCard(
            title = "Aula 1",
            date = "2023-06-21",
            status = ReservationStatus.REJECTED,
            meta1 = "10:00 - 11:00",
            meta2 = "Edificio 1"
        )
    }
}


@Composable
@Preview
fun RequestCardInProgressPreview() {
    SigesmobileTheme {
        RequestCard(
            title = "Aula 1",
            date = "2023-06-21",
            status = ReservationStatus.IN_PROGRESS,
            meta1 = "10:00 - 11:00",
            meta2 = "Edificio 1"
        )
    }
}


@Composable
@Preview
fun RequestCardInFinishedPreview() {
    SigesmobileTheme {
        RequestCard(
            title = "Aula 1",
            date = "2023-06-21",
            status = ReservationStatus.FINISHED,
            meta1 = "10:00 - 11:00",
            meta2 = "Edificio 1"
        )
    }
}



@Composable
@Preview
fun RequestCardInCancelledreview() {
    SigesmobileTheme {
        RequestCard(
            title = "Aula 1",
            date = "2023-06-21",
            status = ReservationStatus.CANCELLED,
            meta1 = "10:00 - 11:00",
            meta2 = "Edificio 1"
        )
    }
}