package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import dev.spiffocode.sigesmobile.data.remote.dto.UserRole
import dev.spiffocode.sigesmobile.domain.repository.ReportRepository
import dev.spiffocode.sigesmobile.domain.repository.ReservableRepository
import dev.spiffocode.sigesmobile.domain.repository.ReservationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class AvailableResourceUIItem(
    val title: String,
    val meta: String,
    val status: ReservableStatus,
    val reservableType: ReservableType,
    val category: String
)

data class ReservationUIItem(
    val id: Long,
    val petitionerName: String,
    val petitionerRole: UserRole,
    val title: String,
    val date: String,
    val status: ReservationStatus,
    val meta1: String,
    val meta2: String
)

data class HomeUiState(
    val isLoading: Boolean = false,

    val userName: String = "",
    val userRole: UserRole = UserRole.STUDENT,

    val pendingCount: Int = 0,
    val thisMonthCount: Int = 0,
    val pendingReservations: List<ReservationUIItem> = emptyList(),

    val myRecentReservations: List<ReservationUIItem> = emptyList(),
    val availableResources: List<AvailableResourceUIItem> = emptyList(),

    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val reportRepository: ReportRepository,
    private val resourceRepository: ReservableRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadHome() }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error     = null,
                    userName  = session.firstName ?: " - ",
                    userRole  = session.role?.let { role -> UserRole.valueOf(role) } ?: UserRole.STUDENT
                )
            }

            if (session.role == "ADMIN") loadAdminHome() else loadApplicantHome()
        }
    }

    private suspend fun loadAdminHome() {
        val pendingJob = viewModelScope.async {
            reservationRepository.getReservations(
                size   = 20,
                status = ReservationStatus.PENDING,
                sort   = "date,asc"
            )
        }
        val monthJob = viewModelScope.async {
            reportRepository.getDashboardStats()
        }

        val pendingResult = pendingJob.await()
        val monthResult   = monthJob.await()

        _uiState.update {
            it.copy(
                isLoading           = false,
                pendingCount        = if (pendingResult is NetworkResult.Success) pendingResult.data.totalElements.toInt() else 0,
                thisMonthCount      = if (monthResult is NetworkResult.Success) monthResult.data.pendingRequests else 0,
                pendingReservations = if (pendingResult is NetworkResult.Success) pendingResult.data.content.map { item -> item.toUiItem() } else emptyList(),
                error               = if (pendingResult is NetworkResult.Error) pendingResult.message else null
            )
        }
    }

    private suspend fun loadApplicantHome() {
        val myReservationsJob = viewModelScope.async {
            reservationRepository.getReservations(size = 3, sort = "date,desc")
        }
        val spacesJob = viewModelScope.async {
            resourceRepository.searchSpaces(size = 5, studentsAvailable = true, showMode = ShowMode.ACTIVE)
        }

        val reservationsResult = myReservationsJob.await()
        val spacesResult       = spacesJob.await()

        _uiState.update {
            it.copy(
                isLoading            = false,
                myRecentReservations = if (reservationsResult is NetworkResult.Success) reservationsResult.data.content.map {item -> item.toUiItem() } else emptyList(),
                availableResources      = if (spacesResult is NetworkResult.Success) spacesResult.data.content.map { item -> item.toUiItem() } else emptyList(),
                error                = if (reservationsResult is NetworkResult.Error) reservationsResult.message else null
            )
        }
    }

    fun setUserName(name: String) = _uiState.update { it.copy(userName = name) }

    fun clearError() = _uiState.update { it.copy(error = null) }


    private fun formatReservationDate(reservation: ReservationResponse): String {
        val date  = reservation.date
        val start = reservation.startTime.truncatedTo(ChronoUnit.MINUTES)
        val end   = reservation.endTime.truncatedTo(ChronoUnit.MINUTES)
        return "$date · $start – $end"
    }

    private fun formatRole(role: UserRole): String = when (role) {
        UserRole.INSTITUTIONAL_STAFF -> "Personal Institucional"
        UserRole.STUDENT             -> "Estudiante"
        UserRole.ADMIN               -> "Administrador"
    }

    private fun ReservationResponse.toUiItem() = ReservationUIItem(
        id          = id,
        title       = reservable?.name ?: "—",
        petitionerName = "$petitioner.firstName $petitioner.lastName",
        petitionerRole = petitioner?.role ?: UserRole.STUDENT,
        date        = formatReservationDate(this),
        status      = status,
        meta1       = reservable?.building?.name ?: "",
        meta2       = "${ChronoUnit.MINUTES.between(startTime, endTime)} min"
    )

    private fun ReservableDto.toUiItem() = AvailableResourceUIItem(
        title  = name,
        meta   = capacity?.let { "Capacidad para $it personas" } ?: inventoryIdNum ?: "",
        status = status,
        reservableType = reservableType,
        category = type?.name ?: spaceType?.name ?: "",
    )
}