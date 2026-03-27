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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.ui.helpers.toBgColor
import dev.spiffocode.sigesmobile.ui.helpers.toCardDateString
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import kotlinx.datetime.LocalDateTime

@Composable
fun RequestCard(
    title: String,
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime,
    status: ReservationStatus,
    meta1: String,
    meta2: String,
    requesterName: String? = null,
    requesterRole: String? = null,
    onClick: () -> Unit = {}
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            if (requesterName != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = buildString {
                            append(requesterName)
                            if (requesterRole != null) append(" · $requesterRole")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(status.toBgColor())
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    StatusLabel(status = status)
                }
            }

            Text(
                text = startDateTime.toCardDateString(endDateTime),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = meta1,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = meta2,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RequestCardPendingPreview() {
    SigesmobileTheme(darkTheme = true) {
        RequestCard(
            title = "Sala de Juntas A",
            startDateTime = LocalDateTime(2026, 1, 28, 10, 0),
            endDateTime = LocalDateTime(2026, 1, 28, 12, 0),
            status = ReservationStatus.PENDING,
            meta1 = "Edificio B, Piso 2",
            meta2 = "15 personas",
            requesterName = "Ana Martínez López",
            requesterRole = "Personal Institucional"
        )
    }
}

@Preview
@Composable
fun RequestCardApprovedPreview() {
    SigesmobileTheme(darkTheme = true) {
        RequestCard(
            title = "Sala de Juntas A",
            startDateTime = LocalDateTime(2026, 1, 28, 10, 0),
            endDateTime = LocalDateTime(2026, 1, 28, 12, 0),
            status = ReservationStatus.APPROVED,
            meta1 = "Edificio B, Piso 2",
            meta2 = "15 personas",
            requesterName = "Ana Martínez López",
            requesterRole = "Personal Institucional"
        )
    }
}

@Preview
@Composable
fun RequestCardRejectedreview() {
    SigesmobileTheme {
        RequestCard(
            title = "Sala de Juntas A",
            startDateTime = LocalDateTime(2026, 1, 28, 10, 0),
            endDateTime = LocalDateTime(2026, 1, 28, 12, 0),
            status = ReservationStatus.REJECTED,
            meta1 = "Edificio B, Piso 2",
            meta2 = "15 personas",
            requesterName = "Ana Martínez López",
            requesterRole = "Personal Institucional"
        )
    }
}

@Preview
@Composable
fun RequestCardInProgressPreview() {
    SigesmobileTheme {
        RequestCard(
            title = "Sala de Juntas A",
            startDateTime = LocalDateTime(2026, 1, 28, 10, 0),
            endDateTime = LocalDateTime(2026, 1, 28, 12, 0),
            status = ReservationStatus.IN_PROGRESS,
            meta1 = "Edificio B, Piso 2",
            meta2 = "15 personas",
            requesterName = "Ana Martínez López",
            requesterRole = "Personal Institucional"
        )
    }
}

@Preview
@Composable
fun RequestCardFinishedPreview() {
    SigesmobileTheme {
        RequestCard(
            title = "Sala de Juntas A",
            startDateTime = LocalDateTime(2026, 1, 28, 10, 0),
            endDateTime = LocalDateTime(2026, 1, 28, 12, 0),
            status = ReservationStatus.FINISHED,
            meta1 = "Edificio B, Piso 2",
            meta2 = "15 personas",
            requesterName = "Ana Martínez López",
            requesterRole = "Personal Institucional"
        )
    }
}

@Preview
@Composable
fun RequestCardCancelledPreview() {
    SigesmobileTheme {
        RequestCard(
            title = "Sala de Juntas A",
            startDateTime = LocalDateTime(2026, 1, 28, 10, 0),
            endDateTime = LocalDateTime(2026, 1, 28, 12, 0),
            status = ReservationStatus.CANCELLED,
            meta1 = "Edificio B, Piso 2",
            meta2 = "15 personas",
            requesterName = "Ana Martínez López",
            requesterRole = "Personal Institucional"
        )
    }
}


@Preview
@Composable
fun RequestCardWithoutIssuerPreview() {
    SigesmobileTheme {
        RequestCard(
            title = "Sala de Juntas A",
            startDateTime = LocalDateTime(2026, 1, 28, 10, 0),
            endDateTime = LocalDateTime(2026, 1, 28, 12, 0),
            status = ReservationStatus.CANCELLED,
            meta1 = "Edificio B, Piso 2",
            meta2 = "15 personas"
        )
    }
}