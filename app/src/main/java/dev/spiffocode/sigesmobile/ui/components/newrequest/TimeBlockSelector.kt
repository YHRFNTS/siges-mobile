package dev.spiffocode.sigesmobile.ui.components.newrequest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Shows selectable time-block chips for the given [date] from the availability list.
 * Already-occupied blocks are shown disabled for context.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeBlockSelector(
    availability: List<DayAvailabilityItem>,
    selectedDate: LocalDate?,
    selectedBlock: TimeBlockItem?,
    onBlockSelected: (TimeBlockItem, LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedDate == null) return

    val dayItem = availability.find { it.date == selectedDate }
    if (dayItem == null) {
        Text(
            text  = "No hay bloques disponibles para este día.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(top = 8.dp)
        )
        return
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text  = "HORARIOS DISPONIBLES",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(8.dp)
        ) {
            // Available blocks — selectable
            dayItem.availableBlocks.sortedBy { it.start }.forEach { block ->
                val isSelected = block == selectedBlock
                FilterChip(
                    selected = isSelected,
                    onClick  = { onBlockSelected(block, selectedDate) },
                    label    = {
                        Text(
                            text       = "${block.start.format(timeFormatter)} – ${block.end.format(timeFormatter)}",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape  = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor     = MaterialTheme.colorScheme.onPrimary,
                        containerColor         = MaterialTheme.colorScheme.secondaryContainer,
                        labelColor             = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }

            // Occupied blocks — informational, disabled
            dayItem.occupiedBlocks.sortedBy { it.start }.forEach { block ->
                FilterChip(
                    selected = false,
                    enabled  = false,
                    onClick  = {},
                    label    = {
                        Text(
                            text  = "${block.start.format(timeFormatter)} – ${block.end.format(timeFormatter)}",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    },
                    shape  = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        disabledContainerColor   = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        disabledLeadingIconColor = MaterialTheme.colorScheme.error
                    )
                )
            }
        }

        if (dayItem.availableBlocks.isEmpty()) {
            Text(
                text  = "Día completo — no quedan horarios disponibles.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewDate = LocalDate.now().plusDays(1)

private val previewAvailable = listOf(
    TimeBlockItem(LocalTime.of(8, 0),  LocalTime.of(10, 0)),
    TimeBlockItem(LocalTime.of(11, 0), LocalTime.of(13, 0)),
    TimeBlockItem(LocalTime.of(14, 0), LocalTime.of(16, 0)),
    TimeBlockItem(LocalTime.of(16, 0), LocalTime.of(18, 0))
)

private val previewOccupied = listOf(
    OccupiedBlockItem(LocalTime.of(10, 0), LocalTime.of(11, 0), ReservationStatus.APPROVED),
    OccupiedBlockItem(LocalTime.of(13, 0), LocalTime.of(14, 0), ReservationStatus.PENDING)
)

/** Bloques mixtos con uno seleccionado. */
@Preview(name = "Bloques mixtos — uno seleccionado", showBackground = true, widthDp = 380)
@Composable
private fun PreviewMixedBlocksWithSelection() {
    SigesmobileTheme {
        Surface {
            TimeBlockSelector(
                availability = listOf(DayAvailabilityItem(previewDate, previewAvailable, previewOccupied)),
                selectedDate  = previewDate,
                selectedBlock = previewAvailable[1],
                onBlockSelected = { _, _ -> },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/** Bloques mixtos sin selección. */
@Preview(name = "Bloques mixtos — ninguno seleccionado", showBackground = true, widthDp = 380)
@Composable
private fun PreviewMixedBlocksNoSelection() {
    SigesmobileTheme {
        Surface {
            TimeBlockSelector(
                availability = listOf(DayAvailabilityItem(previewDate, previewAvailable, previewOccupied)),
                selectedDate  = previewDate,
                selectedBlock = null,
                onBlockSelected = { _, _ -> },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/** Día completo: solo ocupados, sin disponibles. */
@Preview(name = "Día completo — sin slots disponibles", showBackground = true, widthDp = 380)
@Composable
private fun PreviewDayFull() {
    SigesmobileTheme {
        Surface {
            TimeBlockSelector(
                availability = listOf(DayAvailabilityItem(previewDate, emptyList(), previewOccupied)),
                selectedDate  = previewDate,
                selectedBlock = null,
                onBlockSelected = { _, _ -> },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/** Sin datos para la fecha seleccionada (dayItem == null). */
@Preview(name = "Sin datos para la fecha", showBackground = true, widthDp = 380)
@Composable
private fun PreviewNoDataForDate() {
    SigesmobileTheme {
        Surface {
            TimeBlockSelector(
                availability  = emptyList(),
                selectedDate  = previewDate,
                selectedBlock = null,
                onBlockSelected = { _, _ -> },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
