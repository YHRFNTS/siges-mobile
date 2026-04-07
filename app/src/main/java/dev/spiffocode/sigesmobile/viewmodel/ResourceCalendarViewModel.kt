package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.DayAvailabilityItem
import dev.spiffocode.sigesmobile.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

data class ResourceCalendarUiState(
    val reservableId: Long? = null,
    val type: String = "SPACE",
    val reservableName: String = "",
    val weekStart: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY),
    val availability: List<DayAvailabilityItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ResourceCalendarViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResourceCalendarUiState())
    val uiState: StateFlow<ResourceCalendarUiState> = _uiState.asStateFlow()

    fun init(reservableId: Long, type: String, reservableName: String) {
        val today = LocalDate.now()
        _uiState.update {
            it.copy(
                reservableId   = reservableId,
                type           = type,
                reservableName = reservableName,
                weekStart      = today.with(DayOfWeek.MONDAY)
            )
        }
        loadWeek(_uiState.value.weekStart)
    }

    fun previousWeek() {
        val newStart = _uiState.value.weekStart.minusWeeks(1)
        _uiState.update { it.copy(weekStart = newStart) }
        loadWeek(newStart)
    }

    fun nextWeek() {
        val newStart = _uiState.value.weekStart.plusWeeks(1)
        _uiState.update { it.copy(weekStart = newStart) }
        loadWeek(newStart)
    }

    private fun loadWeek(weekStart: LocalDate) {
        val reservableId = _uiState.value.reservableId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val from = weekStart
            val to   = weekStart.plusDays(6)
            when (val result = reservationRepository.getCalendar(reservableId, from, to)) {
                is NetworkResult.Success -> {
                    _uiState.update { state ->
                        // Merge: replace old entries in this week's range, keep others
                        val existing = state.availability.filterNot { d -> d.date >= from && d.date <= to }
                        val merged   = (existing + result.data).sortedBy { it.date }
                        state.copy(isLoading = false, availability = merged)
                    }
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = "No se pudo cargar el calendario: ${result.message}")
                }
                NetworkResult.Loading -> Unit
            }
        }
    }
}
