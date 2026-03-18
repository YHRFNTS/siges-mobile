package dev.spiffocode.sigesmobile.ui.screens.applicant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

import dev.spiffocode.sigesmobile.ui.components.applicantHS.AvailableItemCard
import dev.spiffocode.sigesmobile.ui.components.applicantHS.RequestCard
import dev.spiffocode.sigesmobile.ui.components.applicantHS.SectionHeader
import dev.spiffocode.sigesmobile.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToAvailability: () -> Unit = {},
    onNavigateToNewRequest: () -> Unit = {},
    onNavigateToMyRequests: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
    ) {
        HomeHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-44).dp)
        ) {
            QuickActionsGrid(
                onNavigateToAvailability = onNavigateToAvailability,
                onNavigateToNewRequest = onNavigateToNewRequest,
                onNavigateToMyRequests = onNavigateToMyRequests
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(
                title = "Mis Solicitudes",
                actionText = "Ver todas",
                onActionClick = onNavigateToMyRequests
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                RequestCard(
                    title = "Sala de Juntas A", date = "28 Ene · 10:00 – 12:00", status = "Aprobada",
                    statusColor = Teal, statusBg = Mint, meta1 = "Edificio B, Piso 2", meta2 = "15 personas"
                )
                RequestCard(
                    title = "Proyector HDMI", date = "30 Ene · 09:00 – 13:00", status = "Pendiente",
                    statusColor = Color(0xFFB8860B), statusBg = Lemon, meta1 = "3 unidades", meta2 = "4 horas"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Disponible Ahora",
                actionText = "Ver todo",
                onActionClick = onNavigateToAvailability
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AvailableItemCard(
                    title = "Lab de Cómputo 2", meta = "Capacidad: 30 personas", status = "Disponible todo el día",
                    icon = Icons.Default.Home
                )
                AvailableItemCard(
                    title = "Pantalla Interactiva", meta = "85\" · Táctil", status = "2 unidades disponibles",
                    icon = Icons.Default.Build
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(colors = listOf(Lav, Sky)))
            .padding(top = 56.dp, bottom = 64.dp, start = 24.dp, end = 24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hola de nuevo,", fontSize = 13.sp, color = Slate)
                    Text("Ana Martínez", fontSize = 26.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.5f))
                        .clickable { /* Handle notifications click */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
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
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Plum, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Personal Institucional", fontSize = 12.sp, color = Plum, fontWeight = FontWeight.SemiBold)
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
                title = "Disponibilidad",
                desc = "Ver espacios y equipos",
                icon = Icons.Default.Search,
                iconBg = Lav,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToAvailability
            )
            QuickCard(
                title = "Nueva Solicitud",
                desc = "Reservar recurso",
                icon = Icons.Default.Add,
                iconBg = Mint,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToNewRequest
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickCard(
                title = "Mis Solicitudes",
                desc = "Historial personal",
                icon = Icons.Default.List,
                iconBg = Peach,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToMyRequests
            )
            QuickCard(
                title = "Calendario",
                desc = "Ver reservas",
                icon = Icons.Default.DateRange,
                iconBg = Lemon,
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
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SigesmobileTheme { HomeScreen() }
}