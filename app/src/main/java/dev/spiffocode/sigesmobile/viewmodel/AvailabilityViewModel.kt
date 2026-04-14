package dev.spiffocode.sigesmobile.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentDto
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentTypeDto
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceTypeDto
import dev.spiffocode.sigesmobile.domain.repository.EquipmentRepository
import dev.spiffocode.sigesmobile.domain.repository.SpaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

enum class AvailabilityTab { SPACES, EQUIPMENTS }

data class AvailabilityUiState(
    val isLoading: Boolean = false,
    val selectedTab: AvailabilityTab = AvailabilityTab.SPACES,
    val searchQuery: String = "",

    val spaces: List<SpaceDto> = emptyList(),
    val spaceTypes: List<SpaceTypeDto> = emptyList(),
    val selectedSpaceTypeId: Long? = null,

    val equipments: List<EquipmentDto> = emptyList(),
    val equipmentTypes: List<EquipmentTypeDto> = emptyList(),
    val selectedEquipmentTypeId: Long? = null,
    
    val selectedDate: LocalDate? = null,
    val selectedStartTime: LocalTime? = null,
    val selectedEndTime: LocalTime? = null,
    val isFilterExpanded: Boolean = false,

    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val sortBy: String? = null,
    val showHiddenItems: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AvailabilityViewModel @Inject constructor(
    private val spaceRepository: SpaceRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AvailabilityUiState())
    val uiState: StateFlow<AvailabilityUiState> = _uiState.asStateFlow()

    init {
        loadSpaceTypes()
        loadEquipmentTypes()
        load()
    }

    fun selectTab(tab: AvailabilityTab) {
        _uiState.update { it.copy(selectedTab = tab, currentPage = 0, searchQuery = "") }
        load()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, currentPage = 0) }
        load()
    }

    fun filterBySpaceType(typeId: Long?) {
        _uiState.update { it.copy(selectedSpaceTypeId = typeId, currentPage = 0) }
        load()
    }

    fun filterByEquipmentType(typeId: Long?) {
        _uiState.update { it.copy(selectedEquipmentTypeId = typeId, currentPage = 0) }
        load()
    }

    fun sortBy(sort: String?) {
        _uiState.update { it.copy(sortBy = sort, currentPage = 0) }
        load()
    }

    fun loadPage(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
        load()
    }

    fun onDateChange(date: LocalDate?) {
        _uiState.update { it.copy(selectedDate = date, currentPage = 0) }
        load()
    }

    fun onStartTimeChange(time: LocalTime?) {
        _uiState.update { it.copy(selectedStartTime = time, currentPage = 0) }
        load()
    }

    fun onEndTimeChange(time: LocalTime?) {
        _uiState.update { it.copy(selectedEndTime = time, currentPage = 0) }
        load()
    }

    fun refresh() = load()

    private fun load() {
        val state = _uiState.value
        if (state.selectedTab == AvailabilityTab.SPACES) loadSpaces()
        else loadEquipments()
    }

    private fun loadSpaces() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = spaceRepository.searchSpaces(
                page              = state.currentPage,
                size              = 20,
                sort              = state.sortBy,
                searchQuery       = state.searchQuery.ifBlank { null },
                spaceTypeIdFilter = state.selectedSpaceTypeId,
                showMode          = ShowMode.ACTIVE,
                requestStart      = state.selectedDate?.let { date ->
                    state.selectedStartTime?.let { time ->
                        LocalDateTime.of(date, time)
                    }
                },
                requestEnd        = state.selectedDate?.let { date ->
                    state.selectedEndTime?.let { time ->
                        LocalDateTime.of(date, time)
                    }
                }
            )) {
                is NetworkResult.Success -> _uiState.update {
                    val newSpaces = result.data.content.filter { space -> space.availabilitySlots?.isNotEmpty() == true }
                    it.copy(
                        isLoading = false,
                        spaces = if (state.currentPage == 0) newSpaces else state.spaces + newSpaces,
                        totalPages = result.data.totalPages
                    )
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    private fun loadEquipments() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = equipmentRepository.searchEquipments(
                page              = state.currentPage,
                size              = 20,
                sort              = state.sortBy,
                searchQuery       = state.searchQuery.ifBlank { null },
                equipmentTypeId   = state.selectedEquipmentTypeId,
                showMode          = ShowMode.ACTIVE,
                requestStart      = state.selectedDate?.let { date ->
                    state.selectedStartTime?.let { time ->
                        LocalDateTime.of(date, time)
                    }
                },
                requestEnd        = state.selectedDate?.let { date ->
                    state.selectedEndTime?.let { time ->
                        LocalDateTime.of(date, time)
                    }
                }
            )) {
                is NetworkResult.Success -> _uiState.update {
                    val newEquipments = result.data.content.filter { equip -> equip.availabilitySlots?.isNotEmpty() == true }
                    it.copy(
                        isLoading = false,
                        equipments = if (state.currentPage == 0) newEquipments else state.equipments + newEquipments,
                        totalPages = result.data.totalPages
                    )
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    private fun loadSpaceTypes() {
        viewModelScope.launch {
            when (val result = spaceRepository.getAllSpaceTypes()) {
                is NetworkResult.Success -> _uiState.update { it.copy(spaceTypes = result.data) }
                else -> {}
            }
        }
    }

    private fun loadEquipmentTypes() {
        viewModelScope.launch {
            when (val result = equipmentRepository.getAllEquipmentTypes()) {
                is NetworkResult.Success -> _uiState.update { it.copy(equipmentTypes = result.data) }
                else -> {}
            }
        }
    }

    fun toggleFilters() {
        _uiState.update { it.copy(isFilterExpanded = !it.isFilterExpanded) }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    fun toggleHiddenItems() = _uiState.update { it.copy(showHiddenItems = !it.showHiddenItems) }
}