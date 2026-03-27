package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class EditReservationUiState(
    val date: LocalDate? = LocalDate.now(),
    val startTime: LocalTime? = LocalTime.now(),
    val endTime: LocalTime? = LocalTime.now(),
    val companions: String = "",
    val purpose: String = "",
    val resourceName: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditReservationViewModel @Inject constructor(
    private val repository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditReservationUiState())
    val uiState: StateFlow<EditReservationUiState> = _uiState.asStateFlow()

    private var reservationId: Long = -1L

    fun loadFromReservation(reservation: ReservationResponse) {
        reservationId = reservation.id
        _uiState.update {
            it.copy(
                resourceName = reservation.reservable?.name ?: "",
                date         = reservation.date,
                startTime    = reservation.startTime,
                endTime      = reservation.endTime,
                companions   = (reservation.companions ?: 1).toString(),
                purpose      = reservation.notes?.firstOrNull()?.comment ?: ""
            )
        }
    }

    fun loadReservation(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getReservation(id)) {
                is NetworkResult.Success -> {
                    loadFromReservation(result.data)
                    _uiState.update { it.copy(isLoading = false) }
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun onDateChange(value: LocalDate)       = _uiState.update { it.copy(date = value, error = null) }
    fun onStartTimeChange(value: LocalTime)  = _uiState.update { it.copy(startTime = value, error = null) }
    fun onEndTimeChange(value: LocalTime)    = _uiState.update { it.copy(endTime = value, error = null) }
    fun onCompanionsChange(value: String) = _uiState.update { it.copy(companions = value, error = null) }
    fun onPurposeChange(value: String)    = _uiState.update { it.copy(purpose = value, error = null) }

    fun saveChanges() {
        val state = _uiState.value
        when {
            state.date == null ->
                _uiState.update { it.copy(error = "Selecciona una fecha.") }
            state.startTime == null || state.endTime == null ->
                _uiState.update { it.copy(error = "Ingresa el horario de inicio y fin.") }
            !state.endTime.isAfter(state.startTime) ->
                _uiState.update { it.copy(error = "La hora de fin debe ser después de la hora de inicio.") }
            state.date.isBefore(LocalDate.now()) ->
                _uiState.update { it.copy(error = "La fecha no puede ser en el pasado.") }
            state.companions.isBlank() ->
                _uiState.update { it.copy(error = "Ingresa el número de asistentes.") }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                when (val result = repository.rescheduleReservation(
                    id        = reservationId,
                    date      = state.date,
                    startTime = state.startTime,
                    endTime   = state.endTime
                )) {
                    is NetworkResult.Success -> {
                        val firstNoteId = result.data.notes?.firstOrNull()?.id
                        if (firstNoteId != null && state.purpose.isNotBlank()) {
                            repository.editNote(reservationId, firstNoteId, state.purpose)
                        }
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
}