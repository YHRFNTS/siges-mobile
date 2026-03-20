package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.domain.repository.ReportRepository
import dev.spiffocode.sigesmobile.domain.repository.ReservationRepository
import dev.spiffocode.sigesmobile.domain.repository.SpaceRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,

    val pendingCount: Int = 0,
    val thisMonthCount: Int = 0,
    val pendingReservations: List<ReservationResponse> = emptyList(),

    val myRecentReservations: List<ReservationResponse> = emptyList(),
    val availableSpaces: List<SpaceDto> = emptyList(),

    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val reportsRepository: ReportRepository,
    private val spaceRepository: SpaceRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadHome() }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            if (session.role == "ADMIN") loadAdminHome() else loadApplicantHome()
        }
    }

    private suspend fun loadAdminHome() {
        val pendingJob = viewModelScope.async {
            reservationRepository.getReservations(size = 20, status = ReservationStatus.PENDING, sort = "date,asc")
        }

        val monthJob = viewModelScope.async {
            reportsRepository.getDashboardStats()
        }

        val pendingResult = pendingJob.await()
        val monthResult   = monthJob.await()

        _uiState.update {
            it.copy(
                isLoading           = false,
                pendingCount        = if (pendingResult is NetworkResult.Success) pendingResult.data.totalElements.toInt() else 0,
                thisMonthCount      = if (monthResult is NetworkResult.Success) monthResult.data.reservationsThisMonth else 0,
                pendingReservations = if (pendingResult is NetworkResult.Success) pendingResult.data.content else emptyList(),
                error               = if (pendingResult is NetworkResult.Error) pendingResult.message else null
            )
        }
    }

    private suspend fun loadApplicantHome() {
        val myReservationsJob = viewModelScope.async {
            reservationRepository.getReservations(size = 3, sort = "date,desc")
        }
        val spacesJob = viewModelScope.async {
            spaceRepository.searchSpaces(
                size              = 5,
                studentsAvailable = true,
                showMode          = ShowMode.ACTIVE
            )
        }

        val reservationsResult = myReservationsJob.await()
        val spacesResult       = spacesJob.await()

        _uiState.update {
            it.copy(
                isLoading            = false,
                myRecentReservations = if (reservationsResult is NetworkResult.Success) reservationsResult.data.content else emptyList(),
                availableSpaces      = if (spacesResult is NetworkResult.Success) spacesResult.data.content else emptyList(),
                error                = if (reservationsResult is NetworkResult.Error) reservationsResult.message else null
            )
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}