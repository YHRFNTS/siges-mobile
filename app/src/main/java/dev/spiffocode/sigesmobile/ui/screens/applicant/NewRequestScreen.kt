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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.ui.components.SigesErrorBanner
import dev.spiffocode.sigesmobile.ui.components.SigesNumberSpinner
import dev.spiffocode.sigesmobile.ui.components.newrequest.AvailabilityCalendarPicker
import dev.spiffocode.sigesmobile.ui.components.newrequest.DatePickerField
import dev.spiffocode.sigesmobile.ui.components.newrequest.ResourceSelectionSection
import dev.spiffocode.sigesmobile.ui.components.newrequest.ResourceTypeTabs
import dev.spiffocode.sigesmobile.ui.components.newrequest.TimePickerField
import dev.spiffocode.sigesmobile.ui.components.newrequest.TimeRangePicker
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.CalendarMode
import dev.spiffocode.sigesmobile.viewmodel.CreateReservationUiState
import dev.spiffocode.sigesmobile.viewmodel.CreateReservationViewModel
import dev.spiffocode.sigesmobile.viewmodel.InputMode
import dev.spiffocode.sigesmobile.viewmodel.ResourceType
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

@Composable
fun NewRequestScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: CreateReservationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    prefillResourceId: String = "",
    prefillType: String = "",
    prefillDate: String = "",
    prefillStartTime: String = "",
    prefillEndTime: String = ""
) {
    val uiState by viewModel.uiState.collectAsState()

    // Pre-fill resource from detail navigation
    LaunchedEffect(prefillResourceId, prefillType) {
        if (prefillResourceId.isNotBlank() && prefillType.isNotBlank()) {
            viewModel.prefillResource(prefillResourceId, prefillType)
        }
    }

    // Pre-fill time/date from calendar navigation
    LaunchedEffect(prefillDate, prefillStartTime, prefillEndTime) {
        if (prefillDate.isNotBlank() || prefillStartTime.isNotBlank() || prefillEndTime.isNotBlank()) {
            viewModel.prefillFrom(prefillDate, prefillStartTime, prefillEndTime)
        }
    }

    LaunchedEffect(uiState.createdReservation) {
        uiState.createdReservation?.let {
            viewModel.resetForm()
            onNavigateToDetail(it.id)
        }
    }

    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    NewRequestScreenContent(
        isCompact             = isCompact,
        state                 = uiState,
        onNavigateBack        = onNavigateBack,
        onTypeSelected        = viewModel::selectResourceType,
        onSearchQueryChange   = viewModel::onSearchQueryChange,
        onSearchFocused       = viewModel::onSearchFocused,
        onSpaceSelected       = viewModel::selectSpace,
        onEquipmentSelected   = viewModel::selectEquipment,
        onInputModeChanged    = viewModel::onInputModeChanged,
        onCalendarModeChanged = viewModel::onCalendarModeChanged,
        onMonthChanged        = viewModel::onMonthChanged,
        onWeekChanged         = viewModel::onWeekChanged,
        onDayTappedInMonthly  = viewModel::onDayTappedInMonthly,
        onDaySelectedInWeekly = viewModel::onDateChange,
        onTimeRangeSelected   = { start, end -> viewModel.onTimeRangeSelected(start, end) },
        onDateChange          = viewModel::onDateChange,
        onStartTimeChange     = viewModel::onStartTimeChange,
        onEndTimeChange       = viewModel::onEndTimeChange,
        onCompanionsChange    = viewModel::onCompanionsChange,
        onPurposeChange       = viewModel::onPurposeChange,
        onSubmit              = viewModel::submit,
        onClearError          = viewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestScreenContent(
    isCompact: Boolean = true,
    state: CreateReservationUiState,
    onNavigateBack: () -> Unit = {},
    onTypeSelected: (ResourceType) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onSearchFocused: () -> Unit = {},
    onSpaceSelected: (SpaceDto) -> Unit = {},
    onEquipmentSelected: (EquipmentDto) -> Unit = {},
    onInputModeChanged: (InputMode) -> Unit = {},
    onCalendarModeChanged: (CalendarMode) -> Unit = {},
    onMonthChanged: (YearMonth) -> Unit = {},
    onWeekChanged: (LocalDate) -> Unit = {},
    onDayTappedInMonthly: (LocalDate) -> Unit = {},
    onDaySelectedInWeekly: (LocalDate) -> Unit = {},
    onTimeRangeSelected: (LocalTime, LocalTime) -> Unit = { _, _ -> },
    onDateChange: (LocalDate) -> Unit = {},
    onStartTimeChange: (LocalTime) -> Unit = {},
    onEndTimeChange: (LocalTime) -> Unit = {},
    onCompanionsChange: (String) -> Unit = {},
    onPurposeChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onClearError: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Solicitud", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor      = MaterialTheme.colorScheme.background,
                    titleContentColor   = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(if (isCompact) 24.dp else 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SigesErrorBanner(errorMessage = state.error)
                if (isCompact) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        NewRequestFormFields(
                            state                 = state,
                            onTypeSelected        = onTypeSelected,
                            onSearchQueryChange   = onSearchQueryChange,
                            onSearchFocused       = onSearchFocused,
                            onSpaceSelected       = onSpaceSelected,
                            onEquipmentSelected   = onEquipmentSelected,
                            onInputModeChanged    = onInputModeChanged,
                            onCalendarModeChanged = onCalendarModeChanged,
                            onMonthChanged        = onMonthChanged,
                            onWeekChanged         = onWeekChanged,
                            onDayTappedInMonthly  = onDayTappedInMonthly,
                            onDaySelectedInWeekly = onDaySelectedInWeekly,
                            onTimeRangeSelected   = onTimeRangeSelected,
                            onDateChange          = onDateChange,
                            onStartTimeChange     = onStartTimeChange,
                            onEndTimeChange       = onEndTimeChange,
                            onCompanionsChange    = onCompanionsChange,
                            onPurposeChange       = onPurposeChange,
                            onSubmit              = onSubmit
                        )
                    }
                } else {
                    Card(
                        modifier  = Modifier.widthIn(max = 660.dp),
                        shape     = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(32.dp)) {
                            NewRequestFormFields(
                                state                 = state,
                                onTypeSelected        = onTypeSelected,
                                onSearchQueryChange   = onSearchQueryChange,
                                onSearchFocused       = onSearchFocused,
                                onSpaceSelected       = onSpaceSelected,
                                onEquipmentSelected   = onEquipmentSelected,
                                onInputModeChanged    = onInputModeChanged,
                                onCalendarModeChanged = onCalendarModeChanged,
                                onMonthChanged        = onMonthChanged,
                                onWeekChanged         = onWeekChanged,
                                onDayTappedInMonthly  = onDayTappedInMonthly,
                                onDaySelectedInWeekly = onDaySelectedInWeekly,
                                onTimeRangeSelected   = onTimeRangeSelected,
                                onDateChange          = onDateChange,
                                onStartTimeChange     = onStartTimeChange,
                                onEndTimeChange       = onEndTimeChange,
                                onCompanionsChange    = onCompanionsChange,
                                onPurposeChange       = onPurposeChange,
                                onSubmit              = onSubmit
                            )
                        }
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestFormFields(
    state: CreateReservationUiState,
    onTypeSelected: (ResourceType) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchFocused: () -> Unit = {},
    onSpaceSelected: (SpaceDto) -> Unit,
    onEquipmentSelected: (EquipmentDto) -> Unit,
    onInputModeChanged: (InputMode) -> Unit = {},
    onCalendarModeChanged: (CalendarMode) -> Unit = {},
    onMonthChanged: (YearMonth) -> Unit = {},
    onWeekChanged: (LocalDate) -> Unit = {},
    onDayTappedInMonthly: (LocalDate) -> Unit = {},
    onDaySelectedInWeekly: (LocalDate) -> Unit = {},
    onTimeRangeSelected: (LocalTime, LocalTime) -> Unit = { _, _ -> },
    onDateChange: (LocalDate) -> Unit,
    onStartTimeChange: (LocalTime) -> Unit,
    onEndTimeChange: (LocalTime) -> Unit,
    onCompanionsChange: (String) -> Unit,
    onPurposeChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    // ── Resource type tabs ────────────────────────────────────────────────────
    Text(
        text       = "TIPO DE RECURSO *",
        style      = MaterialTheme.typography.labelSmall,
        color      = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
        modifier   = Modifier.padding(bottom = 8.dp)
    )
    ResourceTypeTabs(
        selectedType = state.resourceType,
        onTypeSelected = onTypeSelected
    )

    Spacer(modifier = Modifier.height(24.dp))

    // ── Resource search ───────────────────────────────────────────────────────
    ResourceSelectionSection(
        searchQuery        = state.searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        onFocusGained      = onSearchFocused,
        searchResults      = state.searchResults,
        isSearching        = state.isSearching,
        selectedSpace      = state.selectedSpace,
        selectedEquipment  = state.selectedEquipment,
        onSpaceSelected    = onSpaceSelected,
        onEquipmentSelected = onEquipmentSelected
    )

    Spacer(modifier = Modifier.height(24.dp))

    // ── Date & Time mode toggle (shown once a resource is selected) ───────────
    val resourceSelected = state.selectedSpace != null || state.selectedEquipment != null

    AnimatedVisibility(
        visible = resourceSelected,
        enter   = fadeIn() + expandVertically(),
        exit    = fadeOut() + shrinkVertically()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text       = "FECHA Y HORARIO *",
                style      = MaterialTheme.typography.labelSmall,
                color      = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(bottom = 8.dp)
            )

            // Mode segmented button
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = state.inputMode == InputMode.CALENDAR,
                    onClick  = { onInputModeChanged(InputMode.CALENDAR) },
                    shape    = SegmentedButtonDefaults.itemShape(0, 2),
                    icon     = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp)) }
                ) {
                    Text("Calendario")
                }
                SegmentedButton(
                    selected = state.inputMode == InputMode.PICKERS,
                    onClick  = { onInputModeChanged(InputMode.PICKERS) },
                    shape    = SegmentedButtonDefaults.itemShape(1, 2),
                    icon     = { Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(16.dp)) }
                ) {
                    Text("Manual")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Calendar mode ─────────────────────────────────────────────────
            AnimatedVisibility(
                visible = state.inputMode == InputMode.CALENDAR,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
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
                        onDaySelectedInWeekly = onDaySelectedInWeekly,
                        minDate              = state.earliestSelectableDateTime.toLocalDate(),
                        availableDates       = state.availableDatesForPicker.takeIf { it.isNotEmpty() }
                    )

                    // Block selector (shown when a date is picked in weekly view)
                    AnimatedVisibility(
                        visible = state.date != null && state.inputMode == InputMode.CALENDAR,
                        enter   = fadeIn() + expandVertically(),
                        exit    = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            val selectedDayItem = state.availability.find { it.date == state.date }
                            val isLimitDay = state.date == state.earliestSelectableDateTime.toLocalDate()
                            TimeRangePicker(
                                availableBlocks = selectedDayItem?.availableBlocks ?: emptyList(),
                                occupiedBlocks  = selectedDayItem?.occupiedBlocks  ?: emptyList(),
                                selectedStart   = state.startTime,
                                selectedEnd     = state.endTime,
                                onRangeChanged  = { start, end -> onTimeRangeSelected(start, end) },
                                minTime         = if (isLimitDay) state.earliestSelectableDateTime.toLocalTime() else null
                            )
                        }
                    }
                }
            }

            // ── Picker mode ───────────────────────────────────────────────────
            AnimatedVisibility(
                visible = state.inputMode == InputMode.PICKERS,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Lead-time info banner
                    if (state.bookInAdvanceDuration != null && state.bookInAdvanceDuration.toMinutes() > 0) {
                        val hours = state.bookInAdvanceDuration.toHours()
                        val mins  = state.bookInAdvanceDuration.toMinutes() % 60
                        val label = when {
                            hours > 0 && mins > 0 -> "${hours}h ${mins}min"
                            hours > 0              -> "${hours}h"
                            else                   -> "${mins}min"
                        }
                        Text(
                            text     = "⏱ Este recurso requiere reservarse con al menos $label de anticipación.",
                            style    = MaterialTheme.typography.bodySmall,
                            color    = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    val minDate = state.earliestSelectableDateTime.toLocalDate()

                    DatePickerField(
                        date           = state.date,
                        onDateChange   = onDateChange,
                        minDate        = minDate,
                        selectableDates = state.availableDatesForPicker
                            .takeIf { it.isNotEmpty() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Earliest selectable time: combine past restriction + lead time
                    val isToday   = state.date == java.time.LocalDate.now()
                    val minStart  = if (isToday) state.earliestSelectableDateTime.toLocalTime() else null

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TimePickerField(
                            time          = state.startTime,
                            label         = "HORA INICIO *",
                            minTime       = minStart,
                            allowedRanges = state.allowedTimeRangesForDate,
                            onTimeChange  = onStartTimeChange,
                            modifier      = Modifier.weight(1f)
                        )
                        TimePickerField(
                            time          = state.endTime,
                            label         = "HORA FIN *",
                            minTime       = state.startTime?.plusMinutes(30),
                            allowedRanges = state.allowedTimeRangesForDate,
                            onTimeChange  = onEndTimeChange,
                            modifier      = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    val maxCap = state.maxCapacity ?: 0

    SigesNumberSpinner(
        value         = if (resourceSelected) state.companions else "",
        onValueChange = onCompanionsChange,
        max           = maxCap,
        label         = if (resourceSelected) "NÚMERO DE ASISTENTES (Máx. $maxCap) *" else "NÚMERO DE ASISTENTES *",
        placeholder   = if (resourceSelected) "Selecciona la cantidad..." else "Selecciona un recurso primero",
        enabled       = resourceSelected && maxCap > 0,
        modifier      = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(24.dp))

    // ── Purpose ───────────────────────────────────────────────────────────────
    OutlinedTextField(
        value         = state.purpose,
        onValueChange = onPurposeChange,
        label         = { Text("PROPÓSITO DE LA RESERVA *") },
        placeholder   = { Text("Describe el propósito...") },
        shape         = RoundedCornerShape(12.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedBorderColor   = MaterialTheme.colorScheme.primary
        ),
        modifier      = Modifier
            .fillMaxWidth()
            .height(120.dp),
        maxLines = 4
    )

    Spacer(modifier = Modifier.height(48.dp))

    // ── Submit ────────────────────────────────────────────────────────────────
    Button(
        onClick  = onSubmit,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape    = MaterialTheme.shapes.large,
        enabled  = !state.isLoading
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color    = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Icon(Icons.Default.Send, contentDescription = "Enviar", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enviar Solicitud", fontWeight = FontWeight.SemiBold)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Preview(showBackground = true)
@Composable
fun NewRequestScreenPreview() {
    SigesmobileTheme {
        NewRequestScreenContent(
            state = CreateReservationUiState(
                resourceType = ResourceType.SPACE,
                searchQuery  = "Sala de Juntas A",
                selectedSpace = SpaceDto(
                    id                   = 1L,
                    name                 = "Sala de Juntas B",
                    status               = ReservableStatus.AVAILABLE,
                    capacity             = 10,
                    availableForStudents = true,
                    bookInAdvanceDuration = 1.hours.toJavaDuration()
                ),
                inputMode     = InputMode.CALENDAR,
                calendarMode  = CalendarMode.MONTHLY,
                calendarMonth = YearMonth.now()
            )
        )
    }
}
