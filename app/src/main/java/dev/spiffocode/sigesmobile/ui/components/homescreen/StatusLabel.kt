package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.ui.helpers.toColor
import dev.spiffocode.sigesmobile.ui.helpers.toText

@Composable
public fun StatusLabel(
    status: ReservationStatus
){
    Text(status.toText(), style = MaterialTheme.typography.labelLarge, color = status.toColor())
}

@Composable
public fun ResourceStatusLabel(
    status: dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
){
    Text(status.toText(), style = MaterialTheme.typography.labelLarge, color = status.toColor())
}