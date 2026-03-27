package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.domain.repository.ReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val observation: String = "",
    val error: String? = null,
    val actionSuccess: String? = null
)

@HiltViewModel
class AdminReviewViewModel @Inject constructor(
    private val repository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminReviewUiState())
    val uiState: StateFlow<AdminReviewUiState> = _uiState.asStateFlow()

    fun loadReservation(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
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

    fun approve(id: Long) {
        val observation = _uiState.value.observation.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            // Optionally publish observation note before approving
            if (observation.isNotEmpty()) {
                repository.addNote(id, observation)
            }
            when (val result = repository.approveReservation(id)) {
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
        val observation = _uiState.value.observation.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.rejectReservation(id, observation)) {
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

    fun clearMessages() = _uiState.update { it.copy(error = null, actionSuccess = null) }
}

enum class AdminReservationTab { ALL, PENDING, RESOLVED }

data class AdminReservationListUiState(
    val isLoading: Boolean = false,
    val reservations: List<ReservationResponse> = emptyList(),
    val selectedTab: AdminReservationTab = AdminReservationTab.ALL,
    val selectedReservableId: Long? = null,
    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val error: String? = null
)

@HiltViewModel
class AdminReservationListViewModel @Inject constructor(
    private val repository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminReservationListUiState())
    val uiState: StateFlow<AdminReservationListUiState> = _uiState.asStateFlow()

    init { load() }

    fun selectTab(tab: AdminReservationTab) {
        _uiState.update { it.copy(selectedTab = tab, currentPage = 0) }
        load()
    }

    fun filterByReservable(reservableId: Long?) {
        _uiState.update { it.copy(selectedReservableId = reservableId, currentPage = 0) }
        load()
    }

    fun loadPage(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
        load()
    }

    fun refresh() = load()

    private fun load() {
        val state = _uiState.value
        val status: ReservationStatus? = when (state.selectedTab) {
            AdminReservationTab.ALL      -> null
            AdminReservationTab.PENDING  -> ReservationStatus.PENDING
            AdminReservationTab.RESOLVED -> null
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getReservations(
                page         = state.currentPage,
                size         = 20,
                sort         = "date,desc",
                status       = status,
                reservableId = state.selectedReservableId
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

    fun clearError() = _uiState.update { it.copy(error = null) }
}