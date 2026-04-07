package dev.spiffocode.sigesmobile.ui.screens.applicant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.spiffocode.sigesmobile.data.remote.dto.TimeBlockItem
import dev.spiffocode.sigesmobile.ui.components.newrequest.AvailabilityCalendarPicker
import dev.spiffocode.sigesmobile.ui.components.newrequest.DatePickerField
import dev.spiffocode.sigesmobile.ui.components.newrequest.TimePickerField
import dev.spiffocode.sigesmobile.ui.components.newrequest.TimeRangePicker
import dev.spiffocode.sigesmobile.viewmodel.CalendarMode
import dev.spiffocode.sigesmobile.viewmodel.InputMode
import dev.spiffocode.sigesmobile.viewmodel.RescheduleUiState
import dev.spiffocode.sigesmobile.viewmodel.RescheduleViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@Composable
fun RescheduleScreen(
    windowSizeClass: WindowSizeClass,
    reservationId: Long,
    viewModel: RescheduleViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(reservationId) {
        viewModel.loadReservation(reservationId)
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.clearMessages()
            onSaveSuccess()
        }
    }

    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    RescheduleScreenContent(
        isCompact             = isCompact,
        state                 = uiState,
        onNavigateBack        = onNavigateBack,
        onInputModeChanged    = viewModel::onInputModeChanged,
        onCalendarModeChanged = viewModel::onCalendarModeChanged,
        onMonthChanged        = viewModel::onMonthChanged,
        onWeekChanged         = viewModel::onWeekChanged,
        onDayTappedInMonthly  = viewModel::onDayTappedInMonthly,
        onBlockSelected       = viewModel::onBlockSelected,
        onDateChange          = viewModel::onDateChange,
        onStartTimeChange     = viewModel::onStartTimeChange,
        onEndTimeChange       = viewModel::onEndTimeChange,
        onTimeRangeSelected   = viewModel::onTimeRangeSelected,
        onSave                = viewModel::saveChanges,
        onClearError          = viewModel::clearMessages
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescheduleScreenContent(
    isCompact: Boolean = true,
    state: RescheduleUiState,
    onNavigateBack: () -> Unit = {},
    onInputModeChanged: (InputMode) -> Unit = {},
    onCalendarModeChanged: (CalendarMode) -> Unit = {},
    onMonthChanged: (YearMonth) -> Unit = {},
    onWeekChanged: (LocalDate) -> Unit = {},
    onDayTappedInMonthly: (LocalDate) -> Unit = {},
    onBlockSelected: (TimeBlockItem, LocalDate) -> Unit = { _, _ -> },
    onDateChange: (LocalDate) -> Unit = {},
    onStartTimeChange: (LocalTime) -> Unit = {},
    onEndTimeChange: (LocalTime) -> Unit = {},
    onTimeRangeSelected: (LocalTime, LocalTime) -> Unit = { _, _ -> },
    onSave: () -> Unit = {},
    onClearError: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reagendar Reserva", fontWeight = FontWeight.Bold) },
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading && state.resourceName.isBlank()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(if (isCompact) 24.dp else 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.widthIn(max = 800.dp),
                        shape    = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                "Reprogramando para:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                state.resourceName,
                                style      = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            
                            state.bookInAdvanceDuration?.let { duration ->
                                if (duration.toMinutes() > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "⏱ Tiempo de anticipación: ${duration.toHours()}h",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Mode Toggle
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().widthIn(max = 400.dp)) {
                        SegmentedButton(
                            selected = state.inputMode == InputMode.CALENDAR,
                            onClick  = { onInputModeChanged(InputMode.CALENDAR) },
                            shape    = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            icon     = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        ) {
                            Text("Calendario")
                        }
                        SegmentedButton(
                            selected = state.inputMode == InputMode.PICKERS,
                            onClick  = { onInputModeChanged(InputMode.PICKERS) },
                            shape    = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            icon     = { Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        ) {
                            Text("Manual")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Input Areas
                    AnimatedVisibility(
                        visible = state.inputMode == InputMode.CALENDAR,
                        enter   = fadeIn() + expandVertically(),
                        exit    = fadeOut() + shrinkVertically()
                    ) {
                        AvailabilityCalendarPicker(
                            calendarMode          = state.calendarMode,
                            currentMonth          = state.calendarMonth,
                            weekStart             = state.calendarWeekStart,
                            availability          = state.availability,
                            selectedDate          = state.date,
                            isLoadingCalendar     = state.isLoadingCalendar,
                            onCalendarModeChanged = onCalendarModeChanged,
                            onMonthChanged        = onMonthChanged,
                            onWeekChanged         = onWeekChanged,
                            onDayTappedInMonthly  = onDayTappedInMonthly,
                            onDaySelectedInWeekly = onDateChange,
                            minDate              = state.earliestSelectableDateTime.toLocalDate(),
                            availableDates       = state.availableDatesForPicker.takeIf { it.isNotEmpty() }
                        )
                    }

                    AnimatedVisibility(
                        visible = state.inputMode == InputMode.PICKERS,
                        enter   = fadeIn() + expandVertically(),
                        exit    = fadeOut() + shrinkVertically()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp)) {
                            // Date Picker with restrictions
                            DatePickerField(
                                date           = state.date,
                                minDate        = state.earliestSelectableDateTime.toLocalDate(),
                                selectableDates = state.availableDatesForPicker,
                                onDateChange   = onDateChange
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Custom Range Slider Picker
                            Text(
                                "Selecciona el horario:",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier   = Modifier.padding(bottom = 8.dp)
                            )
                            
                            TimeRangePicker(
                                availableBlocks = state.availability.find { it.date == state.date }?.availableBlocks ?: emptyList(),
                                selectedStart   = state.startTime,
                                selectedEnd     = state.endTime,
                                minTime         = if (state.date == state.earliestSelectableDateTime.toLocalDate()) state.earliestSelectableDateTime.toLocalTime() else null,
                                onRangeChanged  = onTimeRangeSelected,
                                occupiedBlocks  = state.availability.find { it.date == state.date }?.occupiedBlocks ?: emptyList(),
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Classic pickers as fallback/alternative logic
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                TimePickerField(
                                    time          = state.startTime,
                                    label         = "DESDE",
                                    minTime       = if (state.date == state.earliestSelectableDateTime.toLocalDate()) state.earliestSelectableDateTime.toLocalTime() else null,
                                    allowedRanges = state.allowedTimeRangesForDate,
                                    onTimeChange  = onStartTimeChange,
                                    modifier      = Modifier.weight(1f)
                                )
                                TimePickerField(
                                    time          = state.endTime,
                                    label         = "HASTA",
                                    minTime       = state.startTime,
                                    allowedRanges = state.allowedTimeRangesForDate,
                                    onTimeChange  = onEndTimeChange,
                                    modifier      = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick  = onSave,
                        modifier = Modifier.fillMaxWidth().widthIn(max = 400.dp).height(56.dp),
                        shape    = RoundedCornerShape(16.dp),
                        enabled  = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Update, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirmar Reagendado", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (state.error != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter),
                    action   = { TextButton(onClick = onClearError) { Text("OK", color = MaterialTheme.colorScheme.inversePrimary) } }
                ) { Text(state.error) }
            }
        }
    }
}
