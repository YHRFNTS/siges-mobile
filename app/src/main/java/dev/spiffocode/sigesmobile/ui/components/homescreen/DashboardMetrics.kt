package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
public fun DashboardMetrics(
    horizontalPadding: Dp = 20.dp,
    spacing: Dp = 12.dp,
    pendingReservationsCount: Int,
    reservationsThisMonthCount: Int
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        StatCard(
            label    = "Pendientes",
            value    = pendingReservationsCount.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label    = "Este Mes",
            value    = reservationsThisMonthCount.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
@Preview
fun DashboardMetricsPreview(){
    SigesmobileTheme {
        DashboardMetrics(
            pendingReservationsCount = 6,
            reservationsThisMonthCount = 3
        )
    }
}