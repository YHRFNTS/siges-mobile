package dev.spiffocode.sigesmobile.ui.helpers

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus

@Composable
fun ReservationStatus.toColor(): Color = when (this) {
    ReservationStatus.APPROVED    -> MaterialTheme.colorScheme.primary
    ReservationStatus.PENDING     -> Color(0xFFB8860B)
    ReservationStatus.REJECTED    -> MaterialTheme.colorScheme.error
    ReservationStatus.CANCELLED   -> MaterialTheme.colorScheme.outline
    ReservationStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
    ReservationStatus.FINISHED    -> MaterialTheme.colorScheme.outlineVariant
}

@Composable
fun ReservationStatus.toBgColor(): Color = when (this) {
    ReservationStatus.APPROVED    -> MaterialTheme.colorScheme.primaryContainer
    ReservationStatus.REJECTED    -> MaterialTheme.colorScheme.errorContainer
    else -> {MaterialTheme.colorScheme.primaryContainer}
}