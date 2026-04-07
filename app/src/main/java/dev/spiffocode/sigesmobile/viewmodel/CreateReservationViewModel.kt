package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.DayAvailabilityItem
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationType
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.data.remote.dto.TimeBlockItem
import dev.spiffocode.sigesmobile.domain.repository.EquipmentRepository
import dev.spiffocode.sigesmobile.domain.repository.ReservationRepository
import dev.spiffocode.sigesmobile.domain.repository.SpaceRepository
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

enum class ResourceType { SPACE, EQUIPMENT }
enum class InputMode { CALENDAR, PICKERS }
enum class CalendarMode { MONTHLY, WEEKLY }

data class CreateReservationUiState(
    // Resource selection
    val resourceType: ResourceType = ResourceType.SPACE,
    val searchQuery: String = "",
    val searchResults: List<Any> = emptyList(),
    val isSearching: Boolean = false,
    val selectedSpace: SpaceDto? = null,
    val selectedEquipment: EquipmentDto? = null,

    // Date / time input mode
    val inputMode: InputMode = InputMode.CALENDAR,
    val date: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,

    // Calendar availability
    val calendarMode: CalendarMode = CalendarMode.MONTHLY,
    val calendarMonth: YearMonth = YearMonth.now(),
    val calendarWeekStart: LocalDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY),
    val availability: List<DayAvailabilityItem> = emptyList(),
    val isLoadingCalendar: Boolean = false,
    val selectedBlock: TimeBlockItem? = null,

    // ── Date/time selection restrictions ─────────────────────────────────────
    /** Lead-time from the selected resource (null = no restriction). */
    val bookInAdvanceDuration: java.time.Duration? = null,
    /** Earliest date+time the user may book (now + leadTime). Recomputed whenever the resource changes. */
    val earliestSelectableDateTime: LocalDateTime = LocalDateTime.now(),
    /** Dates that have at least one available block — used to restrict the manual date picker. */
    val availableDatesForPicker: Set<LocalDate> = emptySet(),
    /** Available time ranges for the currently selected date — used to restrict manual time pickers. */
    val allowedTimeRangesForDate: List<Pair<LocalTime, LocalTime>> = emptyList(),

    // Group info & purpose
    val companions: String = "",
    val purpose: String = "",

    // Submission
    val isLoading: Boolean = false,
    val createdReservation: ReservationResponse? = null,
    val error: String? = null
)

@HiltViewModel
class CreateReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val spaceRepository: SpaceRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateReservationUiState())
    val uiState: StateFlow<CreateReservationUiState> = _uiState.asStateFlow()

    private var calendarJob: Job? = null

    // ── Resource selection ────────────────────────────────────────────────────

    fun selectResourceType(type: ResourceType) {
        _uiState.update {
            it.copy(
                resourceType      = type,
                searchQuery       = "",
                searchResults     = emptyList(),
                selectedSpace     = null,
                selectedEquipment = null,
                availability      = emptyList(),
                selectedBlock     = null,
                date              = null,
                startTime         = null,
                endTime           = null
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchResources(query)
    }

    /** Called when the search field gains focus to preload results. */
    fun onSearchFocused() {
        val state = _uiState.value
        if (state.searchResults.isEmpty()) {
            searchResources(state.searchQuery)
        }
    }

    fun selectSpace(space: SpaceDto) {
        val leadTime = space.bookInAdvanceDuration
        _uiState.update {
            it.copy(
                selectedSpace          = space,
                searchResults          = emptyList(),
                searchQuery            = space.name,
                bookInAdvanceDuration  = leadTime,
                earliestSelectableDateTime = computeEarliest(leadTime),
                date                   = null,
                startTime              = null,
                endTime                = null
            )
        }
        loadAvailability(space.id)
    }

    fun selectEquipment(equipment: EquipmentDto) {
        val leadTime = equipment.spaceAttached?.bookInAdvanceDuration
        _uiState.update {
            it.copy(
                selectedEquipment      = equipment,
                searchResults          = emptyList(),
                searchQuery            = equipment.name,
                bookInAdvanceDuration  = leadTime,
                earliestSelectableDateTime = computeEarliest(leadTime),
                date                   = null,
                startTime              = null,
                endTime                = null
            )
        }
        loadAvailability(equipment.id)
    }

    // ── Input mode ────────────────────────────────────────────────────────────

    fun onInputModeChanged(mode: InputMode) = _uiState.update { it.copy(inputMode = mode) }

    // ── Calendar navigation ───────────────────────────────────────────────────

    fun onCalendarModeChanged(mode: CalendarMode) = _uiState.update { it.copy(calendarMode = mode) }

    fun onMonthChanged(month: YearMonth) {
        _uiState.update { it.copy(calendarMonth = month) }
        val state = _uiState.value
        val reservableId = state.selectedSpace?.id ?: state.selectedEquipment?.id ?: return
        loadCalendarRange(
            reservableId = reservableId,
            from = month.atDay(1),
            to   = month.atEndOfMonth()
        )
    }

    fun onWeekChanged(weekStart: LocalDate) {
        _uiState.update { it.copy(calendarWeekStart = weekStart) }
        val state = _uiState.value
        val reservableId = state.selectedSpace?.id ?: state.selectedEquipment?.id ?: return
        loadCalendarRange(reservableId = reservableId, from = weekStart, to = weekStart.plusDays(6))
    }

    /** Called when a day is tapped in monthly view: switches to weekly view centred on that day. */
    fun onDayTappedInMonthly(date: LocalDate) {
        val weekStart = date.with(java.time.DayOfWeek.MONDAY)
        _uiState.update { it.copy(calendarMode = CalendarMode.WEEKLY, calendarWeekStart = weekStart, date = date) }
        val state = _uiState.value
        val reservableId = state.selectedSpace?.id ?: state.selectedEquipment?.id ?: return
        loadCalendarRange(reservableId = reservableId, from = weekStart, to = weekStart.plusDays(6))
    }

    // ── Time block selection ──────────────────────────────────────────────────

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

    // ── Manual pickers ────────────────────────────────────────────────────────

    fun onDateChange(value: LocalDate) {
        _uiState.update { state ->
            val dayItem = state.availability.find { it.date == value }
            val ranges  = dayItem?.availableBlocks
                ?.map { it.start to it.end }
                ?: emptyList()
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
    fun onCompanionsChange(value: String)   = _uiState.update { it.copy(companions = value, error = null) }
    fun onPurposeChange(value: String)      = _uiState.update { it.copy(purpose = value, error = null) }

    // ── Submission ────────────────────────────────────────────────────────────

    fun submit() {
        val state = _uiState.value
        val reservableId = state.selectedSpace?.id ?: state.selectedEquipment?.id

        when {
            reservableId == null ->
                _uiState.update { it.copy(error = "Selecciona un recurso.") }

            state.date == null ->
                _uiState.update { it.copy(error = "Selecciona una fecha.") }

            state.startTime == null || state.endTime == null ->
                _uiState.update { it.copy(error = "Selecciona el horario de inicio y fin.") }

            !state.endTime.isAfter(state.startTime) ->
                _uiState.update { it.copy(error = "La hora de fin debe ser después de la hora de inicio.") }

            state.date.isBefore(LocalDate.now()) ->
                _uiState.update { it.copy(error = "La fecha no puede ser en el pasado.") }

            state.companions.isBlank() ->
                _uiState.update { it.copy(error = "Ingresa el número de asistentes.") }

            state.purpose.isBlank() ->
                _uiState.update { it.copy(error = "Describe el propósito de la reserva.") }

            else -> viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val companions = state.companions.trim().toIntOrNull() ?: 1
                val type = if (companions > 1) ReservationType.GROUP else ReservationType.SINGLE

                when (val result = reservationRepository.createReservation(
                    reservableId = reservableId,
                    date         = state.date,
                    startTime    = state.startTime,
                    endTime      = state.endTime,
                    type         = type,
                    companions   = if (type == ReservationType.GROUP) companions else null,
                    reason       = state.purpose.ifBlank { null }
                )) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, createdReservation = result.data)
                        }
                    }
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = when (result.code) {
                                409  -> "Este horario ya está ocupado. Elige otro."
                                422  -> "Debes reservar este espacio con más anticipación."
                                404  -> "El recurso ya no está disponible."
                                else -> result.message
                            }
                        )
                    }
                    NetworkResult.Loading -> Unit
                }
            }
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun loadAvailability(reservableId: Long) {
        val today = LocalDate.now()
        _uiState.update { it.copy(isLoadingCalendar = true, availability = emptyList()) }
        calendarJob?.cancel()
        calendarJob = viewModelScope.launch {
            // Fetch current month + next
            val from = today.withDayOfMonth(1)
            val to   = today.plusMonths(2).withDayOfMonth(1).minusDays(1)
            loadCalendarRange(reservableId, from, to)
        }
    }

    private fun loadCalendarRange(reservableId: Long, from: LocalDate, to: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCalendar = true) }
            when (val result = reservationRepository.getCalendar(reservableId, from, to)) {
                is NetworkResult.Success -> _uiState.update {
                    val merged = (it.availability.filterNot { d -> d.date >= from && d.date <= to } + result.data)
                        .sortedBy { d -> d.date }
                    val earliestDate = it.earliestSelectableDateTime.toLocalDate()
                    val earliestTime = it.earliestSelectableDateTime.toLocalTime()
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
                    it.copy(
                        availability          = merged,
                        availableDatesForPicker = availDates,
                        isLoadingCalendar     = false
                    )
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoadingCalendar = false)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun searchResources(query: String) {
        val type = _uiState.value.resourceType
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            if (type == ResourceType.SPACE) {
                val result = spaceRepository.searchSpaces(searchQuery = query.ifBlank { null }, size = 15, studentsAvailable = true)
                if (result is NetworkResult.Success) {
                    _uiState.update { it.copy(isSearching = false, searchResults = result.data.content) }
                } else {
                    _uiState.update { it.copy(isSearching = false) }
                }
            } else {
                val result = equipmentRepository.searchEquipments(searchQuery = query.ifBlank { null }, size = 15, studentsAvailable = true)
                if (result is NetworkResult.Success) {
                    _uiState.update { it.copy(isSearching = false, searchResults = result.data.content) }
                } else {
                    _uiState.update { it.copy(isSearching = false) }
                }
            }
        }
    }

    fun prefillResource(idStr: String, typeStr: String) {
        val id = idStr.toLongOrNull() ?: return
        val type = when (typeStr.uppercase()) {
            "SPACE" -> ResourceType.SPACE
            "EQUIPMENT" -> ResourceType.EQUIPMENT
            else -> return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = when (type) {
                ResourceType.SPACE     -> spaceRepository.getSpace(id)
                ResourceType.EQUIPMENT -> equipmentRepository.getEquipment(id)
            }

            when (result) {
                is NetworkResult.Success -> {
                    val data = result.data
                    if (data is SpaceDto) {
                        selectSpace(data)
                    } else if (data is EquipmentDto) {
                        selectEquipment(data)
                    }
                    _uiState.update { it.copy(isLoading = false) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = "No se pudo cargar el recurso: ${result.message}") }
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun prefillFrom(dateStr: String, startTimeStr: String, endTimeStr: String) {
        val date  = runCatching { LocalDate.parse(dateStr) }.getOrNull()
        val start = runCatching { LocalTime.parse(startTimeStr) }.getOrNull()
        val end   = runCatching { LocalTime.parse(endTimeStr) }.getOrNull()

        _uiState.update { state ->
            state.copy(
                date      = date  ?: state.date,
                startTime = start ?: state.startTime,
                endTime   = end   ?: state.endTime,
                // Switch to pickers if we have a full date/time pre-selection (e.g. from calendar)
                inputMode = if (date != null && start != null && end != null) InputMode.PICKERS else state.inputMode
            )
        }
    }

    fun onTimeRangeSelected(start: LocalTime, end: LocalTime) =
        _uiState.update { it.copy(startTime = start, endTime = end) }

    fun clearError() = _uiState.update { it.copy(error = null) }
    fun resetForm()  = _uiState.update { CreateReservationUiState() }

    /** Returns `now + leadTime` (or `now` if no lead time). */
    fun computeEarliest(leadTime: java.time.Duration?): LocalDateTime =
        LocalDateTime.now().let { now ->
            if (leadTime != null) now.plus(leadTime) else now
        }
}