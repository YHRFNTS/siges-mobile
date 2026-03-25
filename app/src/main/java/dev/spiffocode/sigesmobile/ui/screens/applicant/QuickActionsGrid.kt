package dev.spiffocode.sigesmobile.ui.screens.applicant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.ui.components.applicantHS.QuickCard

@Composable
fun QuickActionsGrid(
    onNavigateToAvailability: () -> Unit,
    onNavigateToNewRequest: () -> Unit,
    onNavigateToMyRequests: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickCard(
                title   = "Disponibilidad",
                desc    = "Ver espacios y equipos",
                icon    = Icons.Default.Search,
                iconBg  = MaterialTheme.colorScheme.primaryContainer,
                onClick = onNavigateToAvailability
            )
            QuickCard(
                title   = "Nueva Solicitud",
                desc    = "Reservar recurso",
                icon    = Icons.Default.Add,
                iconBg  = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onNavigateToNewRequest
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickCard(
                title   = "Mis Solicitudes",
                desc    = "Historial personal",
                icon    = Icons.AutoMirrored.Filled.List,
                iconBg  = MaterialTheme.colorScheme.tertiaryContainer,
                onClick = onNavigateToMyRequests
            )
            QuickCard(
                title   = "Calendario",
                desc    = "Ver reservas",
                icon    = Icons.Default.DateRange,
                iconBg  = MaterialTheme.colorScheme.surfaceVariant,
                onClick = onNavigateToAvailability
            )
        }
    }
}