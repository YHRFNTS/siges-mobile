package dev.spiffocode.sigesmobile.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentDto
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
    val selectedEquipmentTypeId: Long? = null,

    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val sortBy: String? = null,
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
                studentsAvailable = true,
                showMode          = ShowMode.ACTIVE
            )) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isLoading = false, spaces = result.data.content, totalPages = result.data.totalPages)
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
                studentsAvailable = true,
                showMode          = ShowMode.ACTIVE
            )) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isLoading = false, equipments = result.data.content, totalPages = result.data.totalPages)
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
            if (spaceRepository.getAllSpaceTypes() is NetworkResult.Success) {
                val result = spaceRepository.getAllSpaceTypes()
                if (result is NetworkResult.Success) {
                    _uiState.update { it.copy(spaceTypes = result.data) }
                }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}