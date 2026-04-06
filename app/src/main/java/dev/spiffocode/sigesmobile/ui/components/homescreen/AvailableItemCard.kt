package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.DoorFront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.ui.helpers.toColor
import dev.spiffocode.sigesmobile.ui.helpers.toText
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
fun AvailableItemCard(
    padding: Dp = 16.dp,
    iconSize: Dp = 52.dp,
    textSpacing: Dp = 3.dp,
    title: String,
    meta: String,
    status: ReservableStatus,
    resourceCategory: String,
    resourceType: ReservableType,
    onClick: () -> Unit = {}
) {

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(padding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Brush.linearGradient(colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer
                    ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (resourceType) {
                    ReservableType.SPACE -> Icons.Outlined.DoorFront
                    ReservableType.EQUIPMENT -> Icons.Outlined.Computer
                }
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(iconSize / 2))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(textSpacing)
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("$resourceCategory - $meta", style = MaterialTheme.typography.bodySmall, color =  MaterialTheme.colorScheme.onSurfaceVariant)
                Text(status.toText(), style = MaterialTheme.typography.labelMedium, color = status.toColor())
            }
        }
    }
}

@Composable
@Preview
fun AvailableItemCardSpacePreview() {
    SigesmobileTheme {
        AvailableItemCard(
            title = "Lab de Cómputo 2",
            meta = "Capacidad para 30 personas",
            status = ReservableStatus.AVAILABLE,
            resourceCategory = "Aulas",
            resourceType = ReservableType.SPACE
        )
    }
}


@Composable
@Preview
fun AvailableItemCardSpaceMaintenancePreview() {
    SigesmobileTheme {
        AvailableItemCard(
            title = "Lab de Cómputo 2",
            meta = "Capacidad para 30 personas",
            status = ReservableStatus.MAINTENANCE,
            resourceCategory = "Aulas",
            resourceType = ReservableType.SPACE
        )
    }
}


@Composable
@Preview
fun AvailableItemCardSpaceLoanedPreview() {
    SigesmobileTheme {
        AvailableItemCard(
            title = "Lab de Cómputo 2",
            meta = "Capacidad para 30 personas",
            status = ReservableStatus.LOANED,
            resourceCategory = "Aulas",
            resourceType = ReservableType.SPACE
        )
    }
}


@Composable
@Preview
fun AvailableItemCardEquipmentPreview() {
    SigesmobileTheme {
        AvailableItemCard(
            title = "Pantalla interactiva",
            meta = "85'' Táctil",
            status = ReservableStatus.AVAILABLE,
            resourceCategory = "Computadores",
            resourceType = ReservableType.EQUIPMENT
        )
    }
}

