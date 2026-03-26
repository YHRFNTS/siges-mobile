package dev.spiffocode.sigesmobile.ui.helpers

import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus


fun ReservableStatus.toText(): String = when (this) {
    ReservableStatus.AVAILABLE   -> "Disponible"
    ReservableStatus.MAINTENANCE -> "En mantenimiento"
    ReservableStatus.LOANED     -> "Prestado"
}