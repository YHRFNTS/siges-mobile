package dev.spiffocode.sigesmobile.ui.components.newrequest

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.viewmodel.ResourceType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ResourceTypeTabs(
    selectedType: ResourceType,
    onTypeSelected: (ResourceType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        val spaceSelected = selectedType == ResourceType.SPACE
        Button(
            onClick = { onTypeSelected(ResourceType.SPACE) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (spaceSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                contentColor = if (spaceSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp, topEnd = 0.dp, bottomEnd = 0.dp),
            border = if (!spaceSelected) ButtonDefaults.outlinedButtonBorder else null
        ) {
            Text("Espacio", fontWeight = FontWeight.SemiBold)
        }
        
        val equipSelected = selectedType == ResourceType.EQUIPMENT
        Button(
            onClick = { onTypeSelected(ResourceType.EQUIPMENT) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (equipSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                contentColor = if (equipSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 12.dp, bottomEnd = 12.dp),
            border = if (!equipSelected) ButtonDefaults.outlinedButtonBorder else null
        ) {
            Text("Equipo", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ClickableOutlinedTextField(
    value: String,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                onClick()
            }
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Date picker field that:
 * - Blocks dates before [minDate] (defaults to today).
 * - When [selectableDates] is provided, only those dates are selectable
 *   (used in manual mode to only allow dates that have availability).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    date: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    minDate: LocalDate = LocalDate.now(),
    selectableDates: Set<LocalDate>? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("dd / MM / yyyy")

    ClickableOutlinedTextField(
        value = date?.format(formatter) ?: "",
        label = "Fecha de uso *",
        placeholder = "dd / mm / aaaa",
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Fecha de uso")
        },
        onClick = { showDialog = true },
        modifier = modifier
    )

    if (showDialog) {
        // The M3 DatePicker works in UTC epoch millis.
        // We convert our LocalDate constraints to millis in UTC midnight.
        val minMillis = minDate
            .atStartOfDay(ZoneId.of("UTC"))
            .toInstant().toEpochMilli()

        val selectableDatesObj = remember(selectableDates, minMillis) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val candidate = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.of("UTC")).toLocalDate()
                    if (candidate.isBefore(minDate)) return false
                    return selectableDates == null || candidate in selectableDates
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year >= minDate.year
                }
            }
        }

        val datePickerState = rememberDatePickerState(
            selectableDates = selectableDatesObj
        )

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC")).toLocalDate()
                        onDateChange(localDate)
                    }
                    showDialog = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * Time picker field that optionally validates the selected time against:
 * - [minTime]: the selected time must be >= minTime.
 * - [allowedRanges]: the selected time must fall within at least one of the provided ranges.
 *
 * If validation fails, an error message is shown instead of calling [onTimeChange].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    time: LocalTime?,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "-- : --",
    minTime: LocalTime? = null,
    allowedRanges: List<Pair<LocalTime, LocalTime>> = emptyList(),
    onTimeChange: (LocalTime) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")

    Column(modifier = modifier) {
        ClickableOutlinedTextField(
            value = time?.format(formatter) ?: "",
            label = label,
            placeholder = placeholder,
            trailingIcon = { Icon(Icons.Default.AccessTime, contentDescription = "Selector de hora") },
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        )
        if (validationError != null) {
            Text(
                text     = validationError!!,
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.error,
                modifier = Modifier
            )
        }
    }

    if (showDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour   = time?.hour   ?: (minTime?.hour   ?: 8),
            initialMinute = time?.minute ?: (minTime?.minute ?: 0)
        )
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val selected = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    val error = validateTime(selected, minTime, allowedRanges)
                    if (error == null) {
                        validationError = null
                        onTimeChange(selected)
                        showDialog = false
                    } else {
                        validationError = error
                        // Keep dialog open so user can adjust
                        showDialog = false
                    }
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; validationError = null }) {
                    Text("Cancelar")
                }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}

/** Returns an error string or null if valid. */
private fun validateTime(
    selected: LocalTime,
    minTime: LocalTime?,
    allowedRanges: List<Pair<LocalTime, LocalTime>>
): String? {
    if (minTime != null && selected.isBefore(minTime)) {
        return "Debe ser a partir de ${minTime.format(DateTimeFormatter.ofPattern("HH:mm"))}."
    }
    if (allowedRanges.isNotEmpty()) {
        val inRange = allowedRanges.any { (s, e) -> !selected.isBefore(s) && !selected.isAfter(e) }
        if (!inRange) {
            val labels = allowedRanges.joinToString(", ") { (s, e) ->
                "${s.format(DateTimeFormatter.ofPattern("HH:mm"))}–${e.format(DateTimeFormatter.ofPattern("HH:mm"))}"
            }
            return "Hora disponible: $labels."
        }
    }
    return null
}
