package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.BuildingDto
import dev.spiffocode.sigesmobile.data.remote.dto.BuildingRegisterDto
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentTypeDto
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentTypeRegisterDto
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceTypeDto
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceTypeRegisterDto
import dev.spiffocode.sigesmobile.domain.repository.BuildingRepository
import dev.spiffocode.sigesmobile.domain.repository.EquipmentRepository
import dev.spiffocode.sigesmobile.domain.repository.SpaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class CatalogTab { BUILDINGS, SPACE_TYPES, EQUIPMENT_TYPES }

data class CatalogsUiState(
    val isLoading: Boolean = false,
    val selectedTab: CatalogTab = CatalogTab.BUILDINGS,
    val buildings: List<BuildingDto> = emptyList(),
    val spaceTypes: List<SpaceTypeDto> = emptyList(),
    val equipmentTypes: List<EquipmentTypeDto> = emptyList(),
    val error: String? = null,
    val showDialog: Boolean = false,
    val editingId: Long? = null,
    val editingName: String = "",
    val editingDescription: String = ""
)

@HiltViewModel
class CatalogsViewModel @Inject constructor(
    private val buildingRepository: BuildingRepository,
    private val spaceRepository: SpaceRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogsUiState())
    val uiState: StateFlow<CatalogsUiState> = _uiState.asStateFlow()

    init {
        loadCatalogs()
    }

    fun selectTab(tab: CatalogTab) {
        _uiState.update { it.copy(selectedTab = tab, showDialog = false) }
        loadCatalogs()
    }

    fun loadCatalogs() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (state.selectedTab) {
                CatalogTab.BUILDINGS -> {
                    when (val result = buildingRepository.getAllBuildings(ShowMode.ACTIVE)) {
                        is NetworkResult.Success -> _uiState.update { it.copy(buildings = result.data, isLoading = false) }
                        is NetworkResult.Error -> _uiState.update { it.copy(error = result.message, isLoading = false) }
                        else -> Unit
                    }
                }
                CatalogTab.SPACE_TYPES -> {
                    when (val result = spaceRepository.getAllSpaceTypes(showMode = ShowMode.ACTIVE.name)) {
                        is NetworkResult.Success -> _uiState.update { it.copy(spaceTypes = result.data, isLoading = false) }
                        is NetworkResult.Error -> _uiState.update { it.copy(error = result.message, isLoading = false) }
                        else -> Unit
                    }
                }
                CatalogTab.EQUIPMENT_TYPES -> {
                    when (val result = equipmentRepository.getAllEquipmentTypes(showMode = ShowMode.ACTIVE)) {
                        is NetworkResult.Success -> _uiState.update { it.copy(equipmentTypes = result.data, isLoading = false) }
                        is NetworkResult.Error -> _uiState.update { it.copy(error = result.message, isLoading = false) }
                        else -> Unit
                    }
                }
            }
        }
    }

    fun openAddDialog() {
        _uiState.update { it.copy(showDialog = true, editingId = null, editingName = "", editingDescription = "") }
    }

    fun openEditDialog(id: Long, name: String, description: String?) {
        _uiState.update { it.copy(showDialog = true, editingId = id, editingName = name, editingDescription = description ?: "") }
    }

    fun closeDialog() {
        _uiState.update { it.copy(showDialog = false) }
    }

    fun onEditNameChange(name: String) = _uiState.update { it.copy(editingName = name) }
    fun onEditDescriptionChange(desc: String) = _uiState.update { it.copy(editingDescription = desc) }

    fun saveRecord() {
        val state = _uiState.value
        val name = state.editingName.trim()
        val desc = state.editingDescription.trim().takeIf { it.isNotEmpty() }
        
        if (name.isEmpty()) {
            _uiState.update { it.copy(error = "El nombre no puede estar vacío") }
            return
        }

        val id = state.editingId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val isSuccess = when (state.selectedTab) {
                CatalogTab.BUILDINGS -> {
                    val req = BuildingRegisterDto(name = name)
                    val res = if (id == null) buildingRepository.createBuilding(req) else buildingRepository.updateBuilding(id, req)
                    handleResult(res)
                }
                CatalogTab.SPACE_TYPES -> {
                    val req = SpaceTypeRegisterDto(name = name, description = desc)
                    val res = if (id == null) spaceRepository.createSpaceType(req) else spaceRepository.updateSpaceType(id, req)
                    handleResult(res)
                }
                CatalogTab.EQUIPMENT_TYPES -> {
                    val req = EquipmentTypeRegisterDto(name = name, description = desc)
                    val res = if (id == null) equipmentRepository.createEquipmentType(req) else equipmentRepository.updateEquipmentType(id, req)
                    handleResult(res)
                }
            }
            if (isSuccess) {
                closeDialog()
                loadCatalogs()
            }
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value
            val isSuccess = when (state.selectedTab) {
                CatalogTab.BUILDINGS -> handleResult(buildingRepository.deactivateBuilding(id))
                CatalogTab.SPACE_TYPES -> handleResult(spaceRepository.deactivateSpaceType(id))
                CatalogTab.EQUIPMENT_TYPES -> handleResult(equipmentRepository.deactivateEquipmentType(id))
            }
            if (isSuccess) loadCatalogs()
        }
    }

    private fun <T> handleResult(result: NetworkResult<T>): Boolean {
        return when (result) {
            is NetworkResult.Success -> true
            is NetworkResult.Error -> {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
                false
            }
            else -> false
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
