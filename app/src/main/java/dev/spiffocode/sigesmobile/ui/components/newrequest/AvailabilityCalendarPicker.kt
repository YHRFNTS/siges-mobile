package dev.spiffocode.sigesmobile.ui.components.newrequest

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.spiffocode.sigesmobile.data.remote.dto.DayAvailabilityItem
import dev.spiffocode.sigesmobile.data.remote.dto.OccupiedBlockItem
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.TimeBlockItem
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.CalendarMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val dayHeaderFormatter = DateTimeFormatter.ofPattern("EEE dd", Locale("es", "ES"))
private val monthFormatter      = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))

/**
 * The top-level availability calendar that embeds a month/week mode toggle and delegates
 * rendering to [MonthCalendar] or [WeekCalendar].
 */
@Composable
fun AvailabilityCalendarPicker(
    calendarMode: CalendarMode,
    currentMonth: YearMonth,
    weekStart: LocalDate,
    availability: List<DayAvailabilityItem>,
    selectedDate: LocalDate?,
    isLoadingCalendar: Boolean,
    onCalendarModeChanged: (CalendarMode) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    onWeekChanged: (LocalDate) -> Unit,
    onDayTappedInMonthly: (LocalDate) -> Unit,
    onDaySelectedInWeekly: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {

        // ── Mode toggle ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalendarModeChip(
                label    = "Mes",
                selected = calendarMode == CalendarMode.MONTHLY,
                onClick  = { onCalendarModeChanged(CalendarMode.MONTHLY) },
                modifier = Modifier.weight(1f)
            )
            CalendarModeChip(
                label    = "Semana",
                selected = calendarMode == CalendarMode.WEEKLY,
                onClick  = { onCalendarModeChanged(CalendarMode.WEEKLY) },
                modifier = Modifier.weight(1f)
            )
        }

        if (isLoadingCalendar) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        } else {
            when (calendarMode) {
                CalendarMode.MONTHLY -> MonthCalendar(
                    currentMonth  = currentMonth,
                    availability  = availability,
                    selectedDate  = selectedDate,
                    onMonthPrev   = { onMonthChanged(currentMonth.minusMonths(1)) },
                    onMonthNext   = { onMonthChanged(currentMonth.plusMonths(1)) },
                    onDayTapped   = onDayTappedInMonthly
                )
                CalendarMode.WEEKLY -> WeekCalendar(
                    weekStart     = weekStart,
                    availability  = availability,
                    selectedDate  = selectedDate,
                    onWeekPrev    = { onWeekChanged(weekStart.minusWeeks(1)) },
                    onWeekNext    = { onWeekChanged(weekStart.plusWeeks(1)) },
                    onDaySelected = onDaySelectedInWeekly
                )
            }
        }
    }
}

// ── Month Calendar ────────────────────────────────────────────────────────────

@Composable
private fun MonthCalendar(
    currentMonth: YearMonth,
    availability: List<DayAvailabilityItem>,
    selectedDate: LocalDate?,
    onMonthPrev:  () -> Unit,
    onMonthNext:  () -> Unit,
    onDayTapped:  (LocalDate) -> Unit
) {
    val today       = LocalDate.now()
    val firstDay    = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    // Offset so the first day lands on the right column (Mon=0 … Sun=6)
    val startOffset = (firstDay.dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7

    val availableSet = availability.filter { it.availableBlocks.isNotEmpty() }.map { it.date }.toSet()

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onMonthPrev, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
                }
                Text(
                    text      = currentMonth.format(monthFormatter).replaceFirstChar { it.uppercase() },
                    style     = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier  = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = onMonthNext, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Day-of-week headers
            val dayNames = listOf("L", "M", "X", "J", "V", "S", "D")
            Row(modifier = Modifier.fillMaxWidth()) {
                dayNames.forEach { d ->
                    Text(
                        text      = d,
                        modifier  = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style     = MaterialTheme.typography.labelSmall,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Day grid
            val totalCells = startOffset + daysInMonth
            val rows = (totalCells + 6) / 7
            var cellIndex = 0

            repeat(rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) { col ->
                        val dayNum = cellIndex - startOffset + 1
                        cellIndex++
                        if (dayNum in 1..daysInMonth) {
                            val date     = currentMonth.atDay(dayNum)
                            val isPast   = date.isBefore(today)
                            val hasSlots = date in availableSet
                            val isSelected = date == selectedDate

                            DayCell(
                                day        = dayNum,
                                isAvailable = hasSlots && !isPast,
                                isPast      = isPast,
                                isSelected  = isSelected,
                                isToday     = date == today,
                                modifier    = Modifier.weight(1f),
                                onClick     = if (hasSlots && !isPast) ({ onDayTapped(date) }) else null
                            )
                        } else {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isAvailable: Boolean,
    isPast: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected  -> MaterialTheme.colorScheme.primary
            isAvailable -> MaterialTheme.colorScheme.primaryContainer
            else        -> Color.Transparent
        },
        animationSpec = tween(150), label = "dayCell"
    )
    val textColor = when {
        isSelected                -> MaterialTheme.colorScheme.onPrimary
        isAvailable               -> MaterialTheme.colorScheme.onPrimaryContainer
        isPast                    -> MaterialTheme.colorScheme.outlineVariant
        else                      -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(bgColor)
            .then(if (isToday && !isSelected) Modifier.border(1.dp, MaterialTheme.colorScheme.primary, CircleShape) else Modifier)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = "$day",
            fontSize = 13.sp,
            color = textColor,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ── Week Calendar ─────────────────────────────────────────────────────────────

@Composable
fun WeekCalendar(
    weekStart: LocalDate,
    availability: List<DayAvailabilityItem>,
    selectedDate: LocalDate?,
    onWeekPrev: () -> Unit,
    onWeekNext: () -> Unit,
    onDaySelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val days  = (0..6).map { weekStart.plusDays(it.toLong()) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Week header with navigation
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onWeekPrev, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Semana anterior")
                }
                val rangeText = "${weekStart.format(DateTimeFormatter.ofPattern("dd MMM", Locale("es")))}" +
                        " – ${weekStart.plusDays(6).format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es")))}"
                Text(
                    text      = rangeText,
                    style     = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier  = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = onWeekNext, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Semana siguiente")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Day strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                days.forEach { date ->
                    val dayItem    = availability.find { it.date == date }
                    val hasSlots   = (dayItem?.availableBlocks?.isNotEmpty() == true) && !date.isBefore(today)
                    val isSelected = date == selectedDate
                    val isToday    = date == today

                    WeekDayChip(
                        date       = date,
                        hasSlots   = hasSlots,
                        isSelected = isSelected,
                        isToday    = isToday,
                        onClick    = if (hasSlots) ({ onDaySelected(date) }) else null,
                        modifier   = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekDayChip(
    date: LocalDate,
    hasSlots: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val locale = Locale("es", "ES")
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        hasSlots   -> MaterialTheme.colorScheme.primaryContainer
        else       -> MaterialTheme.colorScheme.surfaceVariant
    }
    val onBgColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        hasSlots   -> MaterialTheme.colorScheme.onPrimaryContainer
        else       -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .then(if (isToday && !isSelected) Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)) else Modifier)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text  = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale).take(1).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = onBgColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text  = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = onBgColor,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ── Internal chip toggle ──────────────────────────────────────────────────────

@Composable
private fun CalendarModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick  = onClick,
        label    = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        modifier = modifier,
        shape    = RoundedCornerShape(12.dp),
        colors   = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor     = MaterialTheme.colorScheme.onPrimary
        )
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewToday = LocalDate.now()

private fun sampleAvailability(month: YearMonth): List<DayAvailabilityItem> {
    val available = listOf(
        TimeBlockItem(LocalTime.of(8, 0),  LocalTime.of(10, 0)),
        TimeBlockItem(LocalTime.of(11, 0), LocalTime.of(12, 0)),
        TimeBlockItem(LocalTime.of(14, 0), LocalTime.of(16, 0))
    )
    val occupied = listOf(
        OccupiedBlockItem(LocalTime.of(10, 0), LocalTime.of(11, 0), ReservationStatus.APPROVED)
    )
    return (1..month.lengthOfMonth() step 2).mapNotNull { day ->
        val date = try { month.atDay(day) } catch (e: Exception) { return@mapNotNull null }
        if (!date.isBefore(previewToday)) DayAvailabilityItem(date, available, occupied) else null
    }
}

private fun sampleWeekAvailability(weekStart: LocalDate): List<DayAvailabilityItem> {
    val available = listOf(
        TimeBlockItem(LocalTime.of(8, 0),  LocalTime.of(10, 0)),
        TimeBlockItem(LocalTime.of(14, 0), LocalTime.of(16, 0))
    )
    val occupied = listOf(
        OccupiedBlockItem(LocalTime.of(10, 0), LocalTime.of(12, 0), ReservationStatus.APPROVED)
    )
    return listOf(
        DayAvailabilityItem(weekStart.plusDays(1), available, occupied),
        DayAvailabilityItem(weekStart.plusDays(3), available, emptyList()),
        DayAvailabilityItem(weekStart.plusDays(4), emptyList(), occupied)
    )
}

@Preview(name = "Mes — con disponibilidad + día seleccionado", showBackground = true, widthDp = 380)
@Composable
private fun PreviewMonthlyWithAvailability() {
    SigesmobileTheme {
        Surface {
            AvailabilityCalendarPicker(
                calendarMode          = CalendarMode.MONTHLY,
                currentMonth          = YearMonth.now(),
                weekStart             = previewToday.with(DayOfWeek.MONDAY),
                availability          = sampleAvailability(YearMonth.now()),
                selectedDate          = previewToday.plusDays(2),
                isLoadingCalendar     = false,
                onCalendarModeChanged = {},
                onMonthChanged        = {},
                onWeekChanged         = {},
                onDayTappedInMonthly  = {},
                onDaySelectedInWeekly = {},
                modifier              = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Mes — sin disponibilidad (vacío)", showBackground = true, widthDp = 380)
@Composable
private fun PreviewMonthlyEmpty() {
    SigesmobileTheme {
        Surface {
            AvailabilityCalendarPicker(
                calendarMode          = CalendarMode.MONTHLY,
                currentMonth          = YearMonth.now(),
                weekStart             = previewToday.with(DayOfWeek.MONDAY),
                availability          = emptyList(),
                selectedDate          = null,
                isLoadingCalendar     = false,
                onCalendarModeChanged = {},
                onMonthChanged        = {},
                onWeekChanged         = {},
                onDayTappedInMonthly  = {},
                onDaySelectedInWeekly = {},
                modifier              = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Mes — cargando", showBackground = true, widthDp = 380)
@Composable
private fun PreviewMonthlyLoading() {
    SigesmobileTheme {
        Surface {
            AvailabilityCalendarPicker(
                calendarMode          = CalendarMode.MONTHLY,
                currentMonth          = YearMonth.now(),
                weekStart             = previewToday.with(DayOfWeek.MONDAY),
                availability          = emptyList(),
                selectedDate          = null,
                isLoadingCalendar     = true,
                onCalendarModeChanged = {},
                onMonthChanged        = {},
                onWeekChanged         = {},
                onDayTappedInMonthly  = {},
                onDaySelectedInWeekly = {},
                modifier              = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Semana — día seleccionado", showBackground = true, widthDp = 380)
@Composable
private fun PreviewWeeklyWithSelection() {
    val weekStart = previewToday.with(DayOfWeek.MONDAY)
    SigesmobileTheme {
        Surface {
            AvailabilityCalendarPicker(
                calendarMode          = CalendarMode.WEEKLY,
                currentMonth          = YearMonth.now(),
                weekStart             = weekStart,
                availability          = sampleWeekAvailability(weekStart),
                selectedDate          = weekStart.plusDays(1),
                isLoadingCalendar     = false,
                onCalendarModeChanged = {},
                onMonthChanged        = {},
                onWeekChanged         = {},
                onDayTappedInMonthly  = {},
                onDaySelectedInWeekly = {},
                modifier              = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Semana — sin slots disponibles", showBackground = true, widthDp = 380)
@Composable
private fun PreviewWeeklyNoSlots() {
    val weekStart = previewToday.with(DayOfWeek.MONDAY)
    SigesmobileTheme {
        Surface {
            AvailabilityCalendarPicker(
                calendarMode          = CalendarMode.WEEKLY,
                currentMonth          = YearMonth.now(),
                weekStart             = weekStart,
                availability          = emptyList(),
                selectedDate          = null,
                isLoadingCalendar     = false,
                onCalendarModeChanged = {},
                onMonthChanged        = {},
                onWeekChanged         = {},
                onDayTappedInMonthly  = {},
                onDaySelectedInWeekly = {},
                modifier              = Modifier.padding(16.dp)
            )
        }
    }
}
