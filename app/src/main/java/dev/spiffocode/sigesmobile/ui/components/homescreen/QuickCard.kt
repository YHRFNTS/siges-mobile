package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
public fun RowScope.QuickCard(
    weight: Float = 1f,
    padding: Dp = 20.dp,
    iconSize: Dp = 44.dp,
    title: String,
    desc: String,
    icon: ImageVector,
    iconBg: Color,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier
            .weight(weight)
            .clickable { onClick() },
        shape     = MaterialTheme.shapes.large,
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(padding)) {
            Box(
                modifier         = Modifier.size(iconSize).clip(MaterialTheme.shapes.medium).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(iconSize / 2))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
@Preview
fun QuickCardPreview(){
    SigesmobileTheme {
        Row {
            QuickCard(
                title   = "Nueva Solicitud",
                desc    = "Reservar recurso",
                icon    = Icons.Default.Add,
                iconBg  = MaterialTheme.colorScheme.primaryContainer,
                onClick = {}
            )
        }
    }
}