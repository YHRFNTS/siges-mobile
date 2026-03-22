package dev.spiffocode.sigesmobile.ui.screens.applicant

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.hilt.navigation.compose.hiltViewModel
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.data.remote.dto.UserRole
import dev.spiffocode.sigesmobile.ui.components.applicantHS.AvailableItemCard
import dev.spiffocode.sigesmobile.ui.components.applicantHS.RequestCard
import dev.spiffocode.sigesmobile.ui.components.applicantHS.SectionHeader
import dev.spiffocode.sigesmobile.ui.theme.Background
import dev.spiffocode.sigesmobile.ui.theme.Coral
import dev.spiffocode.sigesmobile.ui.theme.Lav
import dev.spiffocode.sigesmobile.ui.theme.Lemon
import dev.spiffocode.sigesmobile.ui.theme.Mint
import dev.spiffocode.sigesmobile.ui.theme.Peach
import dev.spiffocode.sigesmobile.ui.theme.Plum
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.ui.theme.Sky
import dev.spiffocode.sigesmobile.ui.theme.Slate
import dev.spiffocode.sigesmobile.ui.theme.Teal
import dev.spiffocode.sigesmobile.ui.theme.TextPrimary
import dev.spiffocode.sigesmobile.ui.theme.TextSecondary
import dev.spiffocode.sigesmobile.viewmodel.HomeViewModel
import java.time.temporal.ChronoUnit

@Composable
fun ApplicantHomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAvailability: () -> Unit = {},
    onNavigateToNewRequest: () -> Unit = {},
    onNavigateToMyRequests: () -> Unit = {},
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
        HomeHeader(
            userName         = state.userName,
            userRole         = state.userRole,
            onNotifications  = onNavigateToNotifications
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-44).dp)
        ) {
            QuickActionsGrid(
                onNavigateToAvailability = onNavigateToAvailability,
                onNavigateToNewRequest   = onNavigateToNewRequest,
                onNavigateToMyRequests   = onNavigateToMyRequests
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(
                title         = "Mis Solicitudes",
                actionText    = "Ver todas",
                onActionClick = onNavigateToMyRequests
            )

            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Plum, modifier = Modifier.size(24.dp))
                    }
                }
                state.myRecentReservations.isEmpty() -> {
                    Text(
                        text     = "No tienes solicitudes recientes.",
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
                        state.myRecentReservations.forEach { reservation ->
                            RequestCard(
                                title       = reservation.reservable?.name ?: "—",
                                date        = formatReservationDate(reservation),
                                status      = formatStatus(reservation.status),
                                statusColor = statusColor(reservation.status),
                                statusBg    = statusBg(reservation.status),
                                meta1       = reservation.reservable?.building?.name ?: "",
                                meta2       = if ((reservation.companions ?: 0) > 0) "${reservation.companions} personas" else "",
                                onClick     = { onNavigateToDetail(reservation.id) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title         = "Disponible Ahora",
                actionText    = "Ver todo",
                onActionClick = onNavigateToAvailability
            )

            if (state.availableSpaces.isEmpty() && !state.isLoading) {
                Text(
                    text     = "No hay recursos disponibles en este momento.",
                    color    = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            } else {
                Column(
                    modifier            = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    state.availableSpaces.forEach { space ->
                        AvailableItemCard(
                            title  = space.name,
                            meta   = space.capacity?.let { "Capacidad: $it personas" } ?: space.spaceType?.name ?: "",
                            status = formatSpaceStatus(space),
                            icon   = Icons.Default.Home
                        )
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
private fun HomeHeader(
    userName: String,
    userRole: UserRole,
    onNotifications: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(colors = listOf(Lav, Sky)))
            .padding(top = 56.dp, bottom = 64.dp, start = 24.dp, end = 24.dp)
    ) {
        Column {
            Row(
                modifier             = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment    = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hola de nuevo,", fontSize = 13.sp, color = Slate)
                    Text(
                        text       = userName.ifBlank { "—" },
                        fontSize   = 26.sp,
                        color      = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier         = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.5f))
                        .clickable { onNotifications() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        tint               = TextPrimary,
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
                    .background(Color.White.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Plum, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(formatRole(userRole), fontSize = 12.sp, color = Plum, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


@Composable
private fun QuickActionsGrid(
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
                iconBg  = Lav,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToAvailability
            )
            QuickCard(
                title   = "Nueva Solicitud",
                desc    = "Reservar recurso",
                icon    = Icons.Default.Add,
                iconBg  = Mint,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToNewRequest
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickCard(
                title   = "Mis Solicitudes",
                desc    = "Historial personal",
                icon    = Icons.Default.List,
                iconBg  = Peach,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToMyRequests
            )
            QuickCard(
                title   = "Calendario",
                desc    = "Ver reservas",
                icon    = Icons.Default.DateRange,
                iconBg  = Lemon,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToAvailability
            )
        }
    }
}

@Composable
private fun QuickCard(
    title: String,
    desc: String,
    icon: ImageVector,
    iconBg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier  = modifier.clickable { onClick() },
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier         = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Plum, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 14.sp)
        }
    }
}


private fun formatReservationDate(reservation: ReservationResponse): String {
    val date  = reservation.date
    val start = reservation.startTime.truncatedTo(ChronoUnit.MINUTES)
    val end   = reservation.endTime.truncatedTo(ChronoUnit.MINUTES)
    return "$date · $start – $end"
}

private fun formatStatus(status: ReservationStatus): String = when (status) {
    ReservationStatus.PENDING     -> "Pendiente"
    ReservationStatus.APPROVED    -> "Aprobada"
    ReservationStatus.REJECTED    -> "Denegada"
    ReservationStatus.CANCELLED   -> "Cancelada"
    ReservationStatus.IN_PROGRESS -> "En curso"
    ReservationStatus.FINISHED    -> "Completada"
}

private fun statusColor(status: ReservationStatus): Color = when (status) {
    ReservationStatus.APPROVED    -> Teal
    ReservationStatus.PENDING     -> Color(0xFFB8860B)
    ReservationStatus.REJECTED    -> Coral
    ReservationStatus.CANCELLED   -> Color(0xFF9E9E9E)
    ReservationStatus.IN_PROGRESS -> Color(0xFF1565C0)
    ReservationStatus.FINISHED    -> Color(0xFF757575)
}

private fun statusBg(status: ReservationStatus): Color = when (status) {
    ReservationStatus.APPROVED    -> Mint
    ReservationStatus.PENDING     -> Lemon
    ReservationStatus.REJECTED    -> Color(0xFFFFE5E5)
    ReservationStatus.CANCELLED   -> Color(0xFFF5F5F5)
    ReservationStatus.IN_PROGRESS -> Color(0xFFE3F2FD)
    ReservationStatus.FINISHED    -> Color(0xFFF5F5F5)
}

private fun formatSpaceStatus(space: SpaceDto): String = when (space.status) {
    ReservableStatus.AVAILABLE   -> "Disponible"
    ReservableStatus.MAINTENANCE -> "En mantenimiento"
    ReservableStatus.LOANED     -> "Prestado"
}

private fun formatRole(role: UserRole): String = when (role) {
    UserRole.INSTITUTIONAL_STAFF -> "Personal Institucional"
    UserRole.STUDENT             -> "Estudiante"
    UserRole.ADMIN               -> "Administrador"
}

@Preview(showBackground = true)
@Composable
fun ApplicantHomeScreenPreview() {
    SigesmobileTheme { ApplicantHomeScreen() }
}