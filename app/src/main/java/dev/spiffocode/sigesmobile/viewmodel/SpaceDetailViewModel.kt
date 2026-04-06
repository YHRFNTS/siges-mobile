package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.domain.repository.SpaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SpaceDetailUiState(
    val space: SpaceDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SpaceDetailViewModel @Inject constructor(
    private val spaceRepository: SpaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpaceDetailUiState())
    val uiState: StateFlow<SpaceDetailUiState> = _uiState.asStateFlow()

    fun loadSpace(spaceId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = spaceRepository.getSpace(spaceId)
            if(result is NetworkResult.Success){
                _uiState.update {
                    it.copy(
                        space = result.data,
                        isLoading = false
                    )
                }
            } else if(result is NetworkResult.Error){
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.message ?: "Error al cargar los detalles del espacio."
                    )
                }
            }
        }
    }
}
