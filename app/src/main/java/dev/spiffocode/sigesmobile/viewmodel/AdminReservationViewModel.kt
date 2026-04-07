package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableDto
import dev.spiffocode.sigesmobile.domain.repository.ReservableRepository
import dev.spiffocode.sigesmobile.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Admin Review (approve / reject individual reservation) ───────────────────

data class AdminReviewUiState(
    val isLoading: Boolean = false,
    val reservation: ReservationResponse? = null,
    val currentUserId: Long? = null,
    val observation: String = "",
    val rejectReason: String = "",
    val showRejectDialog: Boolean = false,
    val error: String? = null,
    val actionSuccess: String? = null
)

@HiltViewModel
class AdminReviewViewModel @Inject constructor(
    private val repository: ReservationRepository,
    private val sessionManager: dev.spiffocode.sigesmobile.data.local.SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminReviewUiState())
    val uiState: StateFlow<AdminReviewUiState> = _uiState.asStateFlow()

    fun loadReservation(id: Long) {
        viewModelScope.launch {
            val currentId = sessionManager.id?.toLongOrNull()
            _uiState.update { it.copy(isLoading = true, error = null, currentUserId = currentId) }
            when (val result = repository.getReservation(id)) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isLoading = false, reservation = result.data)
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun setObservation(text: String) = _uiState.update { it.copy(observation = text) }
    fun onRejectReasonChange(text: String) = _uiState.update { it.copy(rejectReason = text) }

    fun showRejectDialog() = _uiState.update { it.copy(showRejectDialog = true, rejectReason = "") }
    fun hideRejectDialog() = _uiState.update { it.copy(showRejectDialog = false) }

    fun approve(id: Long) {
        val observation = _uiState.value.observation.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Pass observation directly to approveReservation as approveReason
            when (val result = repository.approveReservation(id, observation.ifEmpty { null })) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isLoading = false, reservation = result.data, actionSuccess = "Solicitud aprobada")
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun reject(id: Long) {
        val reason = _uiState.value.rejectReason.trim()
        if (reason.isEmpty()) {
            _uiState.update { it.copy(error = "El motivo de rechazo es obligatorio") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, showRejectDialog = false) }
            when (val result = repository.rejectReservation(id, reason)) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isLoading = false, reservation = result.data, actionSuccess = "Solicitud denegada")
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun addNote(id: Long, comment: String) {
        if (comment.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.addNote(id, comment)) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(
                        isLoading     = false,
                        reservation   = result.data,
                        actionSuccess = "Observación agregada"
                    )
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun editNote(reservationId: Long, noteId: Long, comment: String) {
        if (comment.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.editNote(reservationId, noteId, comment)) {
                is NetworkResult.Success -> {
                    loadReservation(reservationId)
                    _uiState.update { it.copy(actionSuccess = "Observación editada") }
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun cancel(id: Long, reason: String) {
        if (reason.isBlank()) {
            _uiState.update { it.copy(error = "El motivo de cancelación es obligatorio") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, showRejectDialog = false) }
            when (val result = repository.cancelReservation(id, reason)) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isLoading = false, reservation = result.data, actionSuccess = "Solicitud cancelada")
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = when (result.code) {
                            409  -> "La solicitud ya no está en el estado requerido."
                            403  -> "No tienes permiso para realizar esta acción."
                            else -> result.message
                        }
                    )
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, actionSuccess = null) }
}

enum class AdminReservationTab { ALL, PENDING, RESOLVED }

data class AdminReservationListUiState(
    val isLoading: Boolean = false,
    val reservations: List<ReservationResponse> = emptyList(),
    val selectedTab: AdminReservationTab = AdminReservationTab.PENDING,
    val selectedReservableId: Long? = null,
    val dateFrom: java.time.LocalDate? = null,
    val dateTo: java.time.LocalDate? = null,
    val sort: String = "createdAt,desc",
    val reservables: List<ReservableDto> = emptyList(),
    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val error: String? = null
)

@HiltViewModel
class AdminReservationListViewModel @Inject constructor(
    private val repository: ReservationRepository,
    private val reservableRepository: ReservableRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminReservationListUiState())
    val uiState: StateFlow<AdminReservationListUiState> = _uiState.asStateFlow()

    init {
        load()
        loadReservables()
    }

    fun selectTab(tab: AdminReservationTab) {
        _uiState.update { it.copy(selectedTab = tab, currentPage = 0) }
        load()
    }

    fun filterByReservable(reservableId: Long?) {
        _uiState.update { it.copy(selectedReservableId = reservableId, currentPage = 0) }
        load()
    }

    fun setDateRange(from: java.time.LocalDate?, to: java.time.LocalDate?) {
        _uiState.update { it.copy(dateFrom = from, dateTo = to, currentPage = 0) }
        load()
    }

    fun setSort(sortField: String, direction: String) {
        _uiState.update { it.copy(sort = "$sortField,$direction", currentPage = 0) }
        load()
    }

    fun loadPage(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
        load()
    }

    fun refresh() = load()

    private fun load() {
        val state = _uiState.value
        val statuses: List<ReservationStatus>? = when (state.selectedTab) {
            AdminReservationTab.ALL      -> null
            AdminReservationTab.PENDING  -> listOf(ReservationStatus.PENDING)
            AdminReservationTab.RESOLVED -> listOf(ReservationStatus.CANCELLED, ReservationStatus.APPROVED, ReservationStatus.REJECTED)
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getReservations(
                page         = state.currentPage,
                size         = 20,
                sort         = state.sort,
                statuses       = statuses,
                reservableId = state.selectedReservableId,
                dateFrom     = state.dateFrom,
                dateTo       = state.dateTo
            )) {
                is NetworkResult.Success -> {
                    val content = if (state.selectedTab == AdminReservationTab.RESOLVED) {
                        result.data.content.filter {
                            it.status != ReservationStatus.PENDING
                        }
                    } else result.data.content

                    _uiState.update {
                        it.copy(
                            isLoading    = false,
                            reservations = content,
                            totalPages   = result.data.totalPages
                        )
                    }
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    private fun loadReservables() {
        viewModelScope.launch {
            when (val result = reservableRepository.searchSpaces(size = 100)) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(reservables = result.data.content)
                }
                else -> Unit
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}