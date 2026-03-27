package dev.spiffocode.sigesmobile.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.ui.theme.SigesTheme


fun ReservableStatus.toText(): String = when (this) {
    ReservableStatus.AVAILABLE   -> "Disponible"
    ReservableStatus.MAINTENANCE -> "En mantenimiento"
    ReservableStatus.LOANED     -> "Prestado"
}

@Composable
fun ReservableStatus.toColor(): Color = when(this){
    ReservableStatus.MAINTENANCE -> SigesTheme.extendedColors.maintenance
    ReservableStatus.AVAILABLE -> SigesTheme.extendedColors.available
    ReservableStatus.LOANED -> SigesTheme.extendedColors.loaned
}