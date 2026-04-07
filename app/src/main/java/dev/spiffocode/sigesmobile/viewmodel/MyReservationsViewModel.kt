package dev.spiffocode.sigesmobile.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.domain.repository.ReservableRepository
import dev.spiffocode.sigesmobile.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MyReservationsTab { ALL, PENDING, APPROVED }

data class MyReservationsUiState(
    val isLoading: Boolean = false,
    val reservations: List<ReservationResponse> = emptyList(),
    val selectedTab: MyReservationsTab = MyReservationsTab.ALL,
    val selectedReservable: ReservableDto? = null,
    val dateFrom: java.time.LocalDate? = null,
    val dateTo: java.time.LocalDate? = null,
    val sort: String = "createdAt,desc",
    val reservables: List<ReservableDto> = emptyList(),
    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val error: String? = null
)

@HiltViewModel
class MyReservationsViewModel @Inject constructor(
    private val repository: ReservationRepository,
    private val reservableRepository: ReservableRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyReservationsUiState())
    val uiState: StateFlow<MyReservationsUiState> = _uiState.asStateFlow()

    init {
        load()
        loadReservables()
    }

    fun selectTab(tab: MyReservationsTab) {
        _uiState.update { it.copy(selectedTab = tab, currentPage = 0) }
        load()
    }

    fun filterByReservable(reservable: ReservableDto?) {
        _uiState.update { it.copy(selectedReservable = reservable, currentPage = 0) }
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
            MyReservationsTab.ALL      -> null
            MyReservationsTab.PENDING  -> listOf(ReservationStatus.PENDING)
            MyReservationsTab.APPROVED -> listOf(ReservationStatus.APPROVED)
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getReservations(
                page         = state.currentPage,
                size         = 20,
                sort         = state.sort,
                statuses       = statuses,
                reservableId = state.selectedReservable?.id,
                dateFrom     = state.dateFrom,
                dateTo       = state.dateTo
            )) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(
                        isLoading    = false,
                        reservations = result.data.content,
                        totalPages   = result.data.totalPages
                    )
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