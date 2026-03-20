package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiffocode.siges.data.remote.dto.reservation.*
import com.spiffocode.siges.data.repository.ReservationRepository
import com.spiffocode.siges.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── UI States ────────────────────────────────────────────────────────────────

data class ReservationListUiState(
    val isLoading: Boolean = false,
    val reservations: List<ReservationResponse> = emptyList(),
    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val error: String? = null
)

data class ReservationDetailUiState(
    val isLoading: Boolean = false,
    val reservation: ReservationResponse? = null,
    val error: String? = null,
    /** Transient success message after an action (approve, cancel, etc.) */
    val actionSuccess: String? = null
)

data class CalendarUiState(
    val isLoading: Boolean = false,
    val days: List<DayAvailabilityItem> = emptyList(),
    val error: String? = null
)

// ─── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val repository: ReservationRepository
) : ViewModel() {

    private val _listState   = MutableStateFlow(ReservationListUiState())
    val listState: StateFlow<ReservationListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(ReservationDetailUiState())
    val detailState: StateFlow<ReservationDetailUiState> = _detailState.asStateFlow()

    private val _calendarState = MutableStateFlow(CalendarUiState())
    val calendarState: StateFlow<CalendarUiState> = _calendarState.asStateFlow()

    // ── List ─────────────────────────────────────────────────────────────────

    fun loadReservations(
        page: Int = 0,
        size: Int = 20,
        status: ReservationStatus? = null,
        petitionerId: Long? = null,
        reservableId: Long? = null,
        dateFrom: String? = null,
        dateTo: String? = null,
        type: ReservationType? = null
    ) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            when (val r = repository.getReservations(
                page = page, size = size,
                status = status, petitionerId = petitionerId,
                reservableId = reservableId, dateFrom = dateFrom,
                dateTo = dateTo, type = type
            )) {
                is NetworkResult.Success -> _listState.update {
                    it.copy(
                        isLoading    = false,
                        reservations = r.data.content,
                        totalPages   = r.data.totalPages,
                        currentPage  = r.data.number
                    )
                }
                is NetworkResult.Error -> _listState.update {
                    it.copy(isLoading = false, error = r.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    // ── Detail ───────────────────────────────────────────────────────────────

    fun loadReservation(id: Long) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            when (val r = repository.getReservation(id)) {
                is NetworkResult.Success -> _detailState.update {
                    it.copy(isLoading = false, reservation = r.data)
                }
                is NetworkResult.Error -> _detailState.update {
                    it.copy(isLoading = false, error = r.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    // ── Create ───────────────────────────────────────────────────────────────

    fun createReservation(
        reservableId: Long,
        date: String,
        startTime: String,
        endTime: String,
        type: ReservationType,
        companions: Int? = null
    ) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null, actionSuccess = null) }
            val request = CreateReservationRequest(reservableId, date, startTime, endTime, type, companions)
            when (val r = repository.createReservation(request)) {
                is NetworkResult.Success -> _detailState.update {
                    it.copy(isLoading = false, reservation = r.data, actionSuccess = "Reservación creada")
                }
                is NetworkResult.Error -> _detailState.update {
                    it.copy(isLoading = false, error = mapCreateError(r.code, r.message))
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    fun approveReservation(id: Long) = launchAction(id, "Reservación aprobada") {
        repository.approveReservation(id)
    }

    fun rejectReservation(id: Long, reason: String) = launchAction(id, "Reservación rechazada") {
        repository.rejectReservation(id, reason)
    }

    fun startReservation(id: Long) = launchAction(id, "Reservación iniciada") {
        repository.startReservation(id)
    }

    fun finishReservation(id: Long) = launchAction(id, "Reservación finalizada") {
        repository.finishReservation(id)
    }

    fun cancelReservation(id: Long, reason: String) = launchAction(id, "Reservación cancelada") {
        repository.cancelReservation(id, reason)
    }

    fun rescheduleReservation(id: Long, date: String, startTime: String, endTime: String) =
        launchAction(id, "Reservación reprogramada") {
            repository.rescheduleReservation(id, RescheduleReservationRequest(date, startTime, endTime))
        }

    // ── Notes ─────────────────────────────────────────────────────────────────

    fun addNote(reservationId: Long, comment: String) = launchAction(reservationId, "Nota agregada") {
        repository.addNote(reservationId, comment)
    }

    // ── Calendar ─────────────────────────────────────────────────────────────

    fun loadCalendar(reservableId: Long, from: String? = null, to: String? = null) {
        viewModelScope.launch {
            _calendarState.update { it.copy(isLoading = true, error = null) }
            when (val r = repository.getCalendar(reservableId, from, to)) {
                is NetworkResult.Success -> _calendarState.update {
                    it.copy(isLoading = false, days = r.data)
                }
                is NetworkResult.Error -> _calendarState.update {
                    it.copy(isLoading = false, error = r.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    fun clearDetailError()   = _detailState.update { it.copy(error = null, actionSuccess = null) }
    fun clearListError()     = _listState.update { it.copy(error = null) }

    private fun launchAction(
        id: Long,
        successMsg: String,
        action: suspend () -> NetworkResult<ReservationResponse>
    ) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null, actionSuccess = null) }
            when (val r = action()) {
                is NetworkResult.Success -> _detailState.update {
                    it.copy(isLoading = false, reservation = r.data, actionSuccess = successMsg)
                }
                is NetworkResult.Error -> _detailState.update {
                    it.copy(isLoading = false, error = r.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    private fun mapCreateError(code: Int, message: String?): String = when (code) {
        404  -> "El recurso o usuario no existe"
        409  -> "El horario ya está ocupado"
        422  -> "La reservación debe hacerse con más anticipación"
        else -> message ?: "Error desconocido"
    }
}