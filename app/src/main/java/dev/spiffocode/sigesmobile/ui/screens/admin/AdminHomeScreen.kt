package dev.spiffocode.sigesmobile.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.ui.components.applicantHS.SectionHeader
import dev.spiffocode.sigesmobile.ui.theme.Background
import dev.spiffocode.sigesmobile.ui.theme.Coral
import dev.spiffocode.sigesmobile.ui.theme.Lemon
import dev.spiffocode.sigesmobile.ui.theme.Mint
import dev.spiffocode.sigesmobile.ui.theme.Plum
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.ui.theme.Sky
import dev.spiffocode.sigesmobile.ui.theme.TextPrimary
import dev.spiffocode.sigesmobile.ui.theme.TextSecondary
import dev.spiffocode.sigesmobile.viewmodel.HomeViewModel
import java.time.temporal.ChronoUnit

@Composable
fun AdminHomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAllRequests: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) { viewModel.loadHome() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
    ) {
        AdminHeader(
            userName        = state.userName,
            onNotifications = onNavigateToNotifications
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-44).dp)
        ) {
            Row(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label    = "Solicitudes\nPendientes",
                    value    = state.pendingCount.toString(),
                    icon     = Icons.AutoMirrored.Filled.List,
                    iconBg   = Lemon,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label    = "Este Mes",
                    value    = state.thisMonthCount.toString(),
                    icon     = Icons.Default.DateRange,
                    iconBg   = Mint,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title         = "Solicitudes Pendientes",
                actionText    = "Ver todas",
                onActionClick = onNavigateToAllRequests
            )

            when {
                state.isLoading -> {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Plum, modifier = Modifier.size(24.dp))
                    }
                }
                state.pendingReservations.isEmpty() -> {
                    Text(
                        text     = "No hay solicitudes pendientes.",
                        color    = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
                else -> {
                    Column(
                        modifier            = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        state.pendingReservations.forEach { reservation ->
                            AdminPendingCard(
                                reservation = reservation,
                                onClick     = { onNavigateToDetail(reservation.id) }
                            )
                        }
                    }
                }
            }

            state.error?.let { error ->
                Text(
                    text     = error,
                    color    = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AdminHeader(
    userName: String,
    onNotifications: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(colors = listOf(Plum, Sky)))
            .padding(top = 56.dp, bottom = 64.dp, start = 24.dp, end = 24.dp)
    ) {
        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Panel de Administración", fontSize = 13.sp, color = Color.White.copy(alpha = 0.75f))
                    Text(
                        text       = userName.ifBlank { "—" },
                        fontSize   = 26.sp,
                        color      = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier         = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onNotifications() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        tint               = Color.White,
                        modifier           = Modifier.size(20.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-6).dp, y = 6.dp)
                            .background(Coral, shape = CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier          = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Administrador", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Plum, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text       = value,
                fontSize   = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = TextPrimary
            )
            Text(
                text       = label,
                fontSize   = 11.sp,
                color      = TextSecondary,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun AdminPendingCard(
    reservation: ReservationResponse,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB8860B))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = reservation.reservable?.name ?: "—",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = formatAdminReservationDate(reservation),
                    fontSize = 12.sp,
                    color    = TextSecondary
                )
                reservation.reservable?.building?.name?.let { building ->
                    Text(
                        text     = building,
                        fontSize = 11.sp,
                        color    = TextSecondary.copy(alpha = 0.7f)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Lemon)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text       = "Pendiente",
                    fontSize   = 11.sp,
                    color      = Color(0xFFB8860B),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun formatAdminReservationDate(reservation: ReservationResponse): String {
    val date  = reservation.date
    val start = reservation.startTime.truncatedTo(ChronoUnit.MINUTES)
    val end   = reservation.endTime.truncatedTo(ChronoUnit.MINUTES)
    return "$date · $start – $end"
}


@Preview(showBackground = true)
@Composable
fun AdminHomeScreenPreview() {
    SigesmobileTheme { AdminHomeScreen() }
}