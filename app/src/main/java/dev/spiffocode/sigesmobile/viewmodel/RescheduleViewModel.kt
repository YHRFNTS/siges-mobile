package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.DayAvailabilityItem
import dev.spiffocode.sigesmobile.data.remote.dto.TimeBlockItem
import dev.spiffocode.sigesmobile.domain.repository.ReservationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject

data class RescheduleUiState(
    val reservationId: Long = -1L,
    val resourceId: Long = -1L,
    val resourceName: String = "",
    
    // Date / time selection
    val inputMode: InputMode = InputMode.CALENDAR,
    val date: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,

    // Availability state
    val calendarMode: CalendarMode = CalendarMode.MONTHLY,
    val calendarMonth: YearMonth = YearMonth.now(),
    val calendarWeekStart: LocalDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY),
    val availability: List<DayAvailabilityItem> = emptyList(),
    val isLoadingCalendar: Boolean = false,
    val selectedBlock: TimeBlockItem? = null,

    // Restrictions
    val bookInAdvanceDuration: java.time.Duration? = null,
    val earliestSelectableDateTime: LocalDateTime = LocalDateTime.now(),
    val availableDatesForPicker: Set<LocalDate> = emptySet(),
    val allowedTimeRangesForDate: List<Pair<LocalTime, LocalTime>> = emptyList(),

    // UI state
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RescheduleViewModel @Inject constructor(
    private val repository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RescheduleUiState())
    val uiState: StateFlow<RescheduleUiState> = _uiState.asStateFlow()

    private var calendarJob: Job? = null

    fun loadReservation(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getReservation(id)) {
                is NetworkResult.Success -> {
                    val res = result.data
                    val reservableId = res.reservable?.id ?: -1L
                    val leadTime = res.reservable?.bookInAdvanceDuration

                    _uiState.update {
                        it.copy(
                            reservationId         = res.id,
                            resourceId            = reservableId,
                            resourceName          = res.reservable?.name!!,
                            date                  = res.date,
                            startTime             = res.startTime,
                            endTime               = res.endTime,
                            bookInAdvanceDuration = leadTime,
                            earliestSelectableDateTime = computeEarliest(leadTime),
                            isLoading             = false
                        )
                    }
                    if (reservableId != -1L) {
                        loadAvailability(reservableId)
                    }
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    // ── Input Mode & Calendar Navigation ──────────────────────────────────────

    fun onInputModeChanged(mode: InputMode) = _uiState.update { it.copy(inputMode = mode) }
    fun onCalendarModeChanged(mode: CalendarMode) = _uiState.update { it.copy(calendarMode = mode) }

    fun onMonthChanged(month: YearMonth) {
        _uiState.update { it.copy(calendarMonth = month) }
        val state = _uiState.value
        if (state.resourceId != -1L) {
            loadCalendarRange(state.resourceId, month.atDay(1), month.atEndOfMonth())
        }
    }

    fun onWeekChanged(weekStart: LocalDate) {
        _uiState.update { it.copy(calendarWeekStart = weekStart) }
        val state = _uiState.value
        if (state.resourceId != -1L) {
            loadCalendarRange(state.resourceId, weekStart, weekStart.plusDays(6))
        }
    }

    fun onDayTappedInMonthly(date: LocalDate) {
        val weekStart = date.with(java.time.DayOfWeek.MONDAY)
        _uiState.update { it.copy(calendarMode = CalendarMode.WEEKLY, calendarWeekStart = weekStart, date = date) }
        val state = _uiState.value
        if (state.resourceId != -1L) {
            loadCalendarRange(state.resourceId, weekStart, weekStart.plusDays(6))
        }
    }

    // ── Selection Callbacks ───────────────────────────────────────────────────

    fun onBlockSelected(block: TimeBlockItem, date: LocalDate) {
        _uiState.update {
            it.copy(
                date          = date,
                startTime     = block.start,
                endTime       = block.end,
                selectedBlock = block
            )
        }
    }

    fun onDateChange(value: LocalDate) {
        _uiState.update { state ->
            val dayItem = state.availability.find { it.date == value }
            val ranges  = dayItem?.availableBlocks?.map { it.start to it.end } ?: emptyList()
            state.copy(
                date                    = value,
                startTime               = null,
                endTime                 = null,
                selectedBlock           = null,
                allowedTimeRangesForDate = ranges,
                error                   = null
            )
        }
    }

    fun onStartTimeChange(value: LocalTime) = _uiState.update { it.copy(startTime = value, error = null, selectedBlock = null) }
    fun onEndTimeChange(value: LocalTime)   = _uiState.update { it.copy(endTime = value, error = null, selectedBlock = null) }
    fun onTimeRangeSelected(start: LocalTime, end: LocalTime) = _uiState.update { it.copy(startTime = start, endTime = end) }

    // ── Actions ───────────────────────────────────────────────────────────────

    fun saveChanges() {
        val state = _uiState.value
        if (state.reservationId == -1L) return

        when {
            state.date == null ->
                _uiState.update { it.copy(error = "Selecciona una fecha.") }
            state.startTime == null || state.endTime == null ->
                _uiState.update { it.copy(error = "Selecciona el horario de inicio y fin.") }
            !state.endTime.isAfter(state.startTime) ->
                _uiState.update { it.copy(error = "La hora de fin debe ser después de la hora de inicio.") }
            state.date.isBefore(LocalDate.now()) ->
                _uiState.update { it.copy(error = "La fecha no puede ser en el pasado.") }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                when (val result = repository.rescheduleReservation(
                    id        = state.reservationId,
                    date      = state.date,
                    startTime = state.startTime,
                    endTime   = state.endTime
                )) {
                    is NetworkResult.Success -> {
                        _uiState.update { it.copy(isLoading = false, isSaved = true) }
                    }
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = when (result.code) {
                                409  -> "El nuevo horario ya está ocupado."
                                422  -> "Debes reprogramar con más anticipación."
                                403  -> "Solo el solicitante puede reprogramar."
                                else -> result.message ?: "Error al guardar."
                            }
                        )
                    }
                    NetworkResult.Loading -> Unit
                }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, isSaved = false) }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private fun loadAvailability(reservableId: Long) {
        val today = LocalDate.now()
        _uiState.update { it.copy(isLoadingCalendar = true, availability = emptyList()) }
        calendarJob?.cancel()
        calendarJob = viewModelScope.launch {
            val from = today.withDayOfMonth(1)
            val to   = today.plusMonths(2).withDayOfMonth(1).minusDays(1)
            loadCalendarRange(reservableId, from, to)
        }
    }

    private fun loadCalendarRange(reservableId: Long, from: LocalDate, to: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCalendar = true) }
            when (val result = repository.getCalendar(reservableId, from, to)) {
                is NetworkResult.Success -> {
                    _uiState.update { state ->
                        val merged = (state.availability.filterNot { d -> d.date >= from && d.date <= to } + result.data)
                            .sortedBy { d -> d.date }
                        
                        val earliestDate = state.earliestSelectableDateTime.toLocalDate()
                        val earliestTime = state.earliestSelectableDateTime.toLocalTime()
                        
                        val availDates = merged
                            .filter { d ->
                                if (d.date.isBefore(earliestDate)) false
                                else if (d.date == earliestDate) {
                                    d.availableBlocks.any { b -> b.end.isAfter(earliestTime) }
                                } else {
                                    d.availableBlocks.isNotEmpty()
                                }
                            }
                            .map { d -> d.date }
                            .toSet()

                        state.copy(
                            availability           = merged,
                            availableDatesForPicker = availDates,
                            isLoadingCalendar      = false
                        )
                    }
                }
                is NetworkResult.Error -> _uiState.update { it.copy(isLoadingCalendar = false) }
                NetworkResult.Loading -> Unit
            }
        }
    }

    private fun computeEarliest(leadTime: java.time.Duration?): LocalDateTime =
        LocalDateTime.now().let { now ->
            if (leadTime != null) now.plus(leadTime) else now
        }
}