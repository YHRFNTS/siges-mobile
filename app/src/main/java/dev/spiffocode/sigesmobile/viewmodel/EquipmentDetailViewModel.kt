package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentDto
import dev.spiffocode.sigesmobile.domain.repository.EquipmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EquipmentDetailUiState(
    val equipment: EquipmentDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EquipmentDetailViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EquipmentDetailUiState())
    val uiState: StateFlow<EquipmentDetailUiState> = _uiState.asStateFlow()

    fun loadEquipment(equipmentId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = equipmentRepository.getEquipment(equipmentId)

            if(result is NetworkResult.Success){
                _uiState.update {
                    it.copy(
                        equipment = result.data,
                        isLoading = false
                    )
                }
            } else if(result is NetworkResult.Error){
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.message ?: "Error al cargar los detalles del equipo."
                    )
                }
            }
        }
    }
}
