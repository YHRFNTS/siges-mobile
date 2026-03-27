package dev.spiffocode.sigesmobile.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.ui.theme.SigesTheme

@Composable
fun ReservationStatus.toColor(): Color = when (this) {
    ReservationStatus.APPROVED    -> SigesTheme.extendedColors.onStatusApproved
    ReservationStatus.PENDING     -> SigesTheme.extendedColors.onStatusPending
    ReservationStatus.REJECTED    -> SigesTheme.extendedColors.onStatusDenied
    ReservationStatus.CANCELLED   -> SigesTheme.extendedColors.onStatusCancelled
    ReservationStatus.IN_PROGRESS -> SigesTheme.extendedColors.onStatusApproved
    ReservationStatus.FINISHED    -> SigesTheme.extendedColors.onStatusFinished
}

@Composable
fun ReservationStatus.toBgColor(): Color = when (this) {
    ReservationStatus.APPROVED    -> SigesTheme.extendedColors.statusApproved
    ReservationStatus.PENDING     -> SigesTheme.extendedColors.statusPending
    ReservationStatus.REJECTED    -> SigesTheme.extendedColors.statusDenied
    ReservationStatus.CANCELLED   -> SigesTheme.extendedColors.statusCancelled
    ReservationStatus.IN_PROGRESS -> SigesTheme.extendedColors.statusApproved
    ReservationStatus.FINISHED    -> SigesTheme.extendedColors.statusFinished
}

fun ReservationStatus.toText(): String = when (this) {
    ReservationStatus.PENDING     -> "Pendiente"
    ReservationStatus.APPROVED    -> "Aprobada"
    ReservationStatus.REJECTED    -> "Denegada"
    ReservationStatus.CANCELLED   -> "Cancelada"
    ReservationStatus.IN_PROGRESS -> "En curso"
    ReservationStatus.FINISHED    -> "Completada"
}