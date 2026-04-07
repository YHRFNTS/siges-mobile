package dev.spiffocode.sigesmobile.ui.screens.applicant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import dev.spiffocode.sigesmobile.data.remote.dto.DayAvailabilityItem
import dev.spiffocode.sigesmobile.data.remote.dto.OccupiedBlockItem
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.TimeBlockItem
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.ResourceCalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val timeFormatterRc = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceCalendarScreen(
    reservableId: Long,
    reservableName: String,
    viewModel: ResourceCalendarViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToNewRequest: (reservableId: Long, date: String, startTime: String, endTime: String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(reservableId) {
        viewModel.init(reservableId, reservableName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "Disponibilidad",
                            fontWeight = FontWeight.Bold,
                            style      = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text  = reservableName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading && state.availability.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null && state.availability.isEmpty()) {
                Text(
                    text     = state.error ?: "",
                    color    = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    WeekNavigationHeader(
                        weekStart   = state.weekStart,
                        isLoading   = state.isLoading,
                        onPrevWeek  = viewModel::previousWeek,
                        onNextWeek  = viewModel::nextWeek
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Legend
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LegendItem(MaterialTheme.colorScheme.primaryContainer, "Disponible")
                        LegendItem(MaterialTheme.colorScheme.errorContainer, "Ocupado")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Days of the week
                    val days = (0..6).map { state.weekStart.plusDays(it.toLong()) }
                    val locale = Locale("es", "ES")

                    days.forEach { date ->
                        val dayItem = state.availability.find { it.date == date }
                        DayAvailabilitySection(
                            date    = date,
                            dayItem = dayItem,
                            locale  = locale,
                            onBlockTapped = { block ->
                                onNavigateToNewRequest(
                                    reservableId,
                                    date.toString(),
                                    block.start.toString(),
                                    block.end.toString()
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekNavigationHeader(
    weekStart: LocalDate,
    isLoading: Boolean,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val rangeText = "${weekStart.format(DateTimeFormatter.ofPattern("dd MMM", Locale("es")))}" +
            " – ${weekStart.plusDays(6).format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es")))}"
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevWeek) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Semana anterior")
        }
        Column(
            modifier          = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = rangeText,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center
            )
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
            }
        }
        IconButton(onClick = onNextWeek) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Semana siguiente")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DayAvailabilitySection(
    date: LocalDate,
    dayItem: DayAvailabilityItem?,
    locale: Locale,
    onBlockTapped: (TimeBlockItem) -> Unit
) {
    val today         = LocalDate.now()
    val isPast        = date.isBefore(today)
    val dayName       = date.dayOfWeek.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }
    val dateFormatted = date.format(DateTimeFormatter.ofPattern("dd/MM", locale))

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (isPast)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isPast) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Day header
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = "$dayName, $dateFormatted",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = if (date == today) MaterialTheme.colorScheme.primary
                                 else MaterialTheme.colorScheme.onSurface
                )
                if (dayItem == null && !isPast) {
                    Text(
                        text  = "Sin datos",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isPast) {
                Text(
                    text  = "Día pasado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (dayItem != null) {
                // Available blocks
                if (dayItem.availableBlocks.isNotEmpty()) {
                    Text(
                        text  = "DISPONIBLE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(6.dp)
                    ) {
                        dayItem.availableBlocks.sortedBy { it.start }.forEach { block ->
                            TimeChip(
                                label     = "${block.start.format(timeFormatterRc)} – ${block.end.format(timeFormatterRc)}",
                                color     = MaterialTheme.colorScheme.primaryContainer,
                                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                onClick   = { onBlockTapped(block) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Occupied blocks
                if (dayItem.occupiedBlocks.isNotEmpty()) {
                    Text(
                        text  = "OCUPADO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(6.dp)
                    ) {
                        dayItem.occupiedBlocks.sortedBy { it.start }.forEach { block ->
                            TimeChip(
                                label     = "${block.start.format(timeFormatterRc)} – ${block.end.format(timeFormatterRc)}",
                                color     = MaterialTheme.colorScheme.errorContainer,
                                textColor = MaterialTheme.colorScheme.onErrorContainer,
                                onClick   = null
                            )
                        }
                    }
                }

                if (dayItem.availableBlocks.isEmpty() && dayItem.occupiedBlocks.isEmpty()) {
                    Text(
                        text  = "Sin horarios registrados para este día.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text  = "Sin información de disponibilidad.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TimeChip(
    label: String,
    color: Color,
    textColor: Color,
    onClick: (() -> Unit)?
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = textColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(4.dp)).background(color))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val rcToday = LocalDate.now()
private val rcLocale = Locale("es", "ES")

private fun rcSampleWeekData(weekStart: LocalDate): List<DayAvailabilityItem> {
    val available = listOf(
        TimeBlockItem(LocalTime.of(8, 0),  LocalTime.of(10, 0)),
        TimeBlockItem(LocalTime.of(14, 0), LocalTime.of(16, 0))
    )
    val occupied = listOf(
        OccupiedBlockItem(LocalTime.of(10, 0), LocalTime.of(12, 0), ReservationStatus.APPROVED),
        OccupiedBlockItem(LocalTime.of(12, 0), LocalTime.of(13, 0), ReservationStatus.PENDING)
    )
    return listOf(
        DayAvailabilityItem(weekStart,              available, occupied),
        DayAvailabilityItem(weekStart.plusDays(1),  emptyList(), occupied),
        DayAvailabilityItem(weekStart.plusDays(2),  available, emptyList()),
        DayAvailabilityItem(weekStart.plusDays(3),  emptyList(), emptyList())
        // days 4,5,6 → null (sin datos)
    )
}

/** Sección de un día con bloques disponibles y ocupados. */
@Preview(name = "DaySection — mixto disponible+ocupado", showBackground = true, widthDp = 380)
@Composable
private fun PreviewDaySectionMixed() {
    SigesmobileTheme {
        Surface {
            DayAvailabilitySection(
                date    = rcToday.plusDays(1),
                dayItem = DayAvailabilityItem(
                    date           = rcToday.plusDays(1),
                    availableBlocks = listOf(
                        TimeBlockItem(LocalTime.of(8, 0),  LocalTime.of(10, 0)),
                        TimeBlockItem(LocalTime.of(14, 0), LocalTime.of(16, 0))
                    ),
                    occupiedBlocks = listOf(
                        OccupiedBlockItem(LocalTime.of(10, 0), LocalTime.of(12, 0), ReservationStatus.APPROVED)
                    )
                ),
                locale        = rcLocale,
                onBlockTapped = {},
            )
        }
    }
}

/** Sección de un día completamente ocupado. */
@Preview(name = "DaySection — día completo (solo ocupado)", showBackground = true, widthDp = 380)
@Composable
private fun PreviewDaySectionFull() {
    SigesmobileTheme {
        Surface {
            DayAvailabilitySection(
                date    = rcToday.plusDays(2),
                dayItem = DayAvailabilityItem(
                    date            = rcToday.plusDays(2),
                    availableBlocks = emptyList(),
                    occupiedBlocks  = listOf(
                        OccupiedBlockItem(LocalTime.of(8, 0), LocalTime.of(18, 0), ReservationStatus.APPROVED)
                    )
                ),
                locale        = rcLocale,
                onBlockTapped = {},
            )
        }
    }
}

/** Sección de un día pasado. */
@Preview(name = "DaySection — día pasado", showBackground = true, widthDp = 380)
@Composable
private fun PreviewDaySectionPast() {
    SigesmobileTheme {
        Surface {
            DayAvailabilitySection(
                date          = rcToday.minusDays(2),
                dayItem       = null,
                locale        = rcLocale,
                onBlockTapped = {}
            )
        }
    }
}

/** Sección de un día sin datos del API. */
@Preview(name = "DaySection — sin datos", showBackground = true, widthDp = 380)
@Composable
private fun PreviewDaySectionNoData() {
    SigesmobileTheme {
        Surface {
            DayAvailabilitySection(
                date          = rcToday.plusDays(4),
                dayItem       = null,
                locale        = rcLocale,
                onBlockTapped = {}
            )
        }
    }
}
