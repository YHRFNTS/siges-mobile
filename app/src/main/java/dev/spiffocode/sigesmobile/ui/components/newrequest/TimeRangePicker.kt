package dev.spiffocode.sigesmobile.ui.components.newrequest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.data.remote.dto.DayAvailabilityItem
import dev.spiffocode.sigesmobile.data.remote.dto.OccupiedBlockItem
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.TimeBlockItem
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.roundToInt

private val hhmm = DateTimeFormatter.ofPattern("HH:mm")

// ── Public API ────────────────────────────────────────────────────────────────

/**
 * Interactive time-range picker that lets the user choose start/end within the union
 * of [availableBlocks]. Shows a color-coded track:
 * 🟢 green = available | 🔴 red = occupied | ⬜ gray = outside window.
 *
 * The selection is constrained so both thumbs stay inside a single
 * contiguous free interval (merging adjacent/overlapping available blocks).
 *
 * @param stepMinutes  Snap granularity (default 30 min).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRangePicker(
    availableBlocks: List<TimeBlockItem>,
    occupiedBlocks:  List<OccupiedBlockItem>,
    selectedStart:   LocalTime?,
    selectedEnd:     LocalTime?,
    onRangeChanged:  (start: LocalTime, end: LocalTime) -> Unit,
    modifier:        Modifier = Modifier,
    stepMinutes:     Int = 30
) {
    if (availableBlocks.isEmpty()) {
        Text(
            "Sin franjas disponibles para este día.",
            style    = MaterialTheme.typography.bodySmall,
            color    = MaterialTheme.colorScheme.error,
            modifier = modifier.padding(vertical = 8.dp)
        )
        return
    }

    // ── Day window ────────────────────────────────────────────────────────────
    val allTimes = availableBlocks.flatMap { listOf(it.start, it.end) } +
                   occupiedBlocks.flatMap  { listOf(it.start, it.end) }
    val dayStart  = LocalTime.of(allTimes.min().hour, 0)
    val dayEnd    = allTimes.max().let { t ->
        if (t.minute == 0) t else LocalTime.of((t.hour + 1).coerceAtMost(23), 0)
    }
    val totalMins = ChronoUnit.MINUTES.between(dayStart, dayEnd).toInt()
        .coerceAtLeast(stepMinutes * 4)

    // ── Merged free intervals ─────────────────────────────────────────────────
    val mergedFree: List<Pair<LocalTime, LocalTime>> = remember(availableBlocks) {
        mergeIntervals(availableBlocks.map { it.start to it.end })
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    fun LocalTime.toF(): Float = ChronoUnit.MINUTES.between(dayStart, this).toFloat()
    fun Float.toSnapped(): LocalTime {
        val raw     = toInt().coerceIn(0, totalMins)
        val snapped = (raw / stepMinutes) * stepMinutes
        return dayStart.plusMinutes(snapped.toLong())
    }
    fun Pair<LocalTime, LocalTime>.containsRange(s: LocalTime, e: LocalTime) =
        !s.isBefore(first) && !e.isAfter(second)

    fun isValid(sF: Float, eF: Float): Boolean {
        val s = sF.toSnapped(); val e = eF.toSnapped()
        return e.isAfter(s) && mergedFree.any { it.containsRange(s, e) }
    }

    // ── Initial state ─────────────────────────────────────────────────────────
    val firstFree = mergedFree.first()
    val initStart = (selectedStart?.takeIf { t -> mergedFree.any { !t.isBefore(it.first) && !t.isAfter(it.second) } }
        ?: firstFree.first).toF()
    val initEnd   = (selectedEnd?.takeIf { t -> mergedFree.any { !t.isBefore(it.first) && !t.isAfter(it.second) } }
        ?: minOf(firstFree.first.plusMinutes((stepMinutes * 2).toLong()), firstFree.second)).toF()

    var startF by remember { mutableFloatStateOf(initStart) }
    var endF   by remember { mutableFloatStateOf(initEnd) }

    LaunchedEffect(Unit) { onRangeChanged(startF.toSnapped(), endF.toSnapped()) }

    // ── Derived display ───────────────────────────────────────────────────────
    val dispStart  = startF.toSnapped()
    val dispEnd    = endF.toSnapped()
    val durMins    = ChronoUnit.MINUTES.between(dispStart, dispEnd).toInt()
    val durLabel   = buildDurationLabel(durMins)

    // Color references for track
    val colorAvail  = MaterialTheme.colorScheme.primaryContainer
    val colorOccupy = MaterialTheme.colorScheme.errorContainer
    val colorGray   = MaterialTheme.colorScheme.surfaceVariant
    val colorSel    = MaterialTheme.colorScheme.primary

    Column(modifier = modifier.fillMaxWidth()) {

        // ── Summary row ───────────────────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeChipLabel(dispStart.format(hhmm))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null,
                modifier = Modifier.size(14.dp),
                tint     = MaterialTheme.colorScheme.onSurfaceVariant)
            TimeChipLabel(dispEnd.format(hhmm))
            Spacer(Modifier.weight(1f))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = colorSel.copy(alpha = 0.12f)
            ) {
                Text(
                    text       = durLabel,
                    style      = MaterialTheme.typography.labelMedium,
                    color      = colorSel,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Slider ────────────────────────────────────────────────────────────
        RangeSlider(
            value         = startF..endF,
            onValueChange = { r ->
                val ns = r.start; val ne = r.endInclusive
                if (isValid(ns, ne)) {
                    startF = ns; endF = ne
                    onRangeChanged(ns.toSnapped(), ne.toSnapped())
                } else {
                    // Determine which thumb moved and clamp it
                    val startMoved = abs(ns - startF) > abs(ne - endF)
                    if (startMoved) {
                        val endInterval = mergedFree.find { !endF.toSnapped().isBefore(it.first) && !endF.toSnapped().isAfter(it.second) }
                        if (endInterval != null) {
                            val clamped = ((maxOf(ns, endInterval.first.toF()) / stepMinutes).roundToInt() * stepMinutes).toFloat()
                            if (isValid(clamped, endF)) {
                                startF = clamped
                                onRangeChanged(clamped.toSnapped(), endF.toSnapped())
                            }
                        }
                    } else {
                        val startInterval = mergedFree.find { !startF.toSnapped().isBefore(it.first) && !startF.toSnapped().isAfter(it.second) }
                        if (startInterval != null) {
                            val clamped = ((minOf(ne, startInterval.second.toF()) / stepMinutes).roundToInt() * stepMinutes).toFloat()
                            if (isValid(startF, clamped)) {
                                endF = clamped
                                onRangeChanged(startF.toSnapped(), clamped.toSnapped())
                            }
                        }
                    }
                }
            },
            valueRange = 0f..totalMins.toFloat(),
            steps      = (totalMins / stepMinutes) - 1,
            track      = { state ->
                AvailabilityTrack(
                    state           = state,
                    dayStart        = dayStart,
                    totalMins       = totalMins,
                    availableBlocks = availableBlocks,
                    occupiedBlocks  = occupiedBlocks,
                    colorAvail      = colorAvail,
                    colorOccupy     = colorOccupy,
                    colorGray       = colorGray,
                    colorSel        = colorSel
                )
            }
        )

        // ── Time axis labels ──────────────────────────────────────────────────
        Spacer(Modifier.height(4.dp))
        TimeAxis(dayStart = dayStart, dayEnd = dayEnd)
    }
}

// ── Internal composables ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvailabilityTrack(
    state:           RangeSliderState,
    dayStart:        LocalTime,
    totalMins:       Int,
    availableBlocks: List<TimeBlockItem>,
    occupiedBlocks:  List<OccupiedBlockItem>,
    colorAvail:      Color,
    colorOccupy:     Color,
    colorGray:       Color,
    colorSel:        Color
) {
    fun LocalTime.frac(): Float =
        ChronoUnit.MINUTES.between(dayStart, this).toFloat() / totalMins

    val range    = state.valueRange
    val selStart = (state.activeRangeStart - range.start) / (range.endInclusive - range.start)
    val selEnd   = (state.activeRangeEnd   - range.start) / (range.endInclusive - range.start)

    Box(
        Modifier
            .fillMaxWidth()
            .height(10.dp)
            .drawBehind {
                val w = size.width; val h = size.height
                val cr = CornerRadius(h / 2)

                // Base: gray
                drawRoundRect(color = colorGray, cornerRadius = cr)

                // Available blocks: green
                availableBlocks.forEach { blk ->
                    drawRect(
                        color   = colorAvail,
                        topLeft = Offset(blk.start.frac() * w, 0f),
                        size    = Size((blk.end.frac() - blk.start.frac()) * w, h)
                    )
                }

                // Occupied blocks: red
                occupiedBlocks.forEach { blk ->
                    drawRect(
                        color   = colorOccupy,
                        topLeft = Offset(blk.start.frac() * w, 0f),
                        size    = Size((blk.end.frac() - blk.start.frac()) * w, h)
                    )
                }

                // Selected range: primary
                drawRect(
                    color   = colorSel.copy(alpha = 0.85f),
                    topLeft = Offset(selStart * w, 0f),
                    size    = Size((selEnd - selStart) * w, h)
                )
            }
    )
}

@Composable
private fun TimeAxis(dayStart: LocalTime, dayEnd: LocalTime) {
    val fmt = DateTimeFormatter.ofPattern("HH:mm")
    // Show a label every 2 hours (or 1h if window is small)
    val stepH  = if (ChronoUnit.HOURS.between(dayStart, dayEnd) <= 6) 1L else 2L
    val labels = mutableListOf<LocalTime>()
    var t = dayStart
    while (!t.isAfter(dayEnd)) { labels.add(t); t = t.plusHours(stepH) }
    if (labels.last() != dayEnd) labels.add(dayEnd)

    Row(Modifier.fillMaxWidth()) {
        labels.forEachIndexed { i, time ->
            val weight = if (i < labels.lastIndex)
                ChronoUnit.MINUTES.between(time, labels[i + 1]).toFloat()
            else 1f
            Text(
                text     = time.format(fmt),
                style    = MaterialTheme.typography.labelSmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(weight.coerceAtLeast(1f))
            )
        }
    }
}

@Composable
private fun TimeChipLabel(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text       = text,
            style      = MaterialTheme.typography.labelLarge,
            color      = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// ── Utility functions ─────────────────────────────────────────────────────────

private fun mergeIntervals(
    blocks: List<Pair<LocalTime, LocalTime>>
): List<Pair<LocalTime, LocalTime>> {
    if (blocks.isEmpty()) return emptyList()
    val sorted = blocks.sortedBy { it.first }
    val merged = mutableListOf(sorted.first())
    sorted.drop(1).forEach { blk ->
        val last = merged.last()
        if (!blk.first.isAfter(last.second))
            merged[merged.lastIndex] = last.first to maxOf(last.second, blk.second)
        else
            merged.add(blk)
    }
    return merged
}

private fun buildDurationLabel(minutes: Int): String {
    val h = minutes / 60; val m = minutes % 60
    return when {
        h > 0 && m > 0 -> "${h}h ${m}min"
        h > 0           -> "${h}h"
        else            -> "${minutes}min"
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val pDay = LocalDate.now().plusDays(1)

private val pAvail = listOf(
    TimeBlockItem(LocalTime.of(8, 0),  LocalTime.of(12, 0)),
    TimeBlockItem(LocalTime.of(14, 0), LocalTime.of(18, 0))
)
private val pOccupied = listOf(
    OccupiedBlockItem(LocalTime.of(12, 0), LocalTime.of(14, 0), ReservationStatus.APPROVED)
)

@Preview(name = "Sin selección previa", showBackground = true, widthDp = 380)
@Composable
private fun PreviewNoSelection() {
    SigesmobileTheme {
        TimeRangePicker(
            availableBlocks = pAvail,
            occupiedBlocks  = pOccupied,
            selectedStart   = null,
            selectedEnd     = null,
            onRangeChanged  = { _, _ -> },
            modifier        = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Con rango preseleccionado (9-11)", showBackground = true, widthDp = 380)
@Composable
private fun PreviewWithSelection() {
    SigesmobileTheme {
        TimeRangePicker(
            availableBlocks = pAvail,
            occupiedBlocks  = pOccupied,
            selectedStart   = LocalTime.of(9, 0),
            selectedEnd     = LocalTime.of(11, 0),
            onRangeChanged  = { _, _ -> },
            modifier        = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Sin bloques disponibles", showBackground = true, widthDp = 380)
@Composable
private fun PreviewNoAvailability() {
    SigesmobileTheme {
        TimeRangePicker(
            availableBlocks = emptyList(),
            occupiedBlocks  = pOccupied,
            selectedStart   = null,
            selectedEnd     = null,
            onRangeChanged  = { _, _ -> },
            modifier        = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Ventana pequeña (2h disponibles)", showBackground = true, widthDp = 380)
@Composable
private fun PreviewSmallWindow() {
    SigesmobileTheme {
        TimeRangePicker(
            availableBlocks = listOf(TimeBlockItem(LocalTime.of(10, 0), LocalTime.of(12, 0))),
            occupiedBlocks  = emptyList(),
            selectedStart   = LocalTime.of(10, 30),
            selectedEnd     = LocalTime.of(11, 30),
            onRangeChanged  = { _, _ -> },
            modifier        = Modifier.padding(16.dp)
        )
    }
}
