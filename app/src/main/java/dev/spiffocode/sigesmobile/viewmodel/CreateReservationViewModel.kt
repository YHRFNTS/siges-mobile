package dev.spiffocode.sigesmobile.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationType
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.domain.repository.EquipmentRepository
import dev.spiffocode.sigesmobile.domain.repository.ReservationRepository
import dev.spiffocode.sigesmobile.domain.repository.SpaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

enum class ResourceType { SPACE, EQUIPMENT }

data class CreateReservationUiState(
    val resourceType: ResourceType = ResourceType.SPACE,
    val searchQuery: String = "",
    val searchResults: List<Any> = emptyList(),
    val isSearching: Boolean = false,
    val selectedSpace: SpaceDto? = null,
    val selectedEquipment: EquipmentDto? = null,

    val date: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val companions: String = "",
    val purpose: String = "",

    val isLoading: Boolean = false,
    val createdReservation: ReservationResponse? = null,
    val error: String? = null
)

@HiltViewModel
class CreateReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val spaceRepository: SpaceRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateReservationUiState())
    val uiState: StateFlow<CreateReservationUiState> = _uiState.asStateFlow()

    fun selectResourceType(type: ResourceType) {
        _uiState.update {
            it.copy(
                resourceType       = type,
                searchQuery        = "",
                searchResults      = emptyList(),
                selectedSpace      = null,
                selectedEquipment  = null
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.length >= 2) searchResources(query)
        else _uiState.update { it.copy(searchResults = emptyList()) }
    }

    fun selectSpace(space: SpaceDto) =
        _uiState.update { it.copy(selectedSpace = space, searchResults = emptyList(), searchQuery = space.name) }

    fun selectEquipment(equipment: EquipmentDto) =
        _uiState.update { it.copy(selectedEquipment = equipment, searchResults = emptyList(), searchQuery = equipment.name) }


    fun onDateChange(value: LocalDate)       = _uiState.update { it.copy(date = value, error = null) }
    fun onStartTimeChange(value: LocalTime)  = _uiState.update { it.copy(startTime = value, error = null) }
    fun onEndTimeChange(value: LocalTime)    = _uiState.update { it.copy(endTime = value, error = null) }
    fun onCompanionsChange(value: String) = _uiState.update { it.copy(companions = value, error = null) }
    fun onPurposeChange(value: String)    = _uiState.update { it.copy(purpose = value, error = null) }

    fun submit() {
        val state = _uiState.value
        val reservableId = state.selectedSpace?.id ?: state.selectedEquipment?.id

        when {
            reservableId == null ->
                _uiState.update { it.copy(error = "Selecciona un recurso.") }

            state.date == null ->
                _uiState.update { it.copy(error = "Selecciona una fecha.") }

            state.startTime == null || state.endTime == null ->
                _uiState.update { it.copy(error = "Ingresa el horario de inicio y fin.") }

            !state.endTime.isAfter(state.startTime) ->
                _uiState.update { it.copy(error = "La hora de fin debe ser después de la hora de inicio.") }

            state.date.isBefore(LocalDate.now()) ->
                _uiState.update { it.copy(error = "La fecha no puede ser en el pasado.") }

            state.companions.isBlank() ->
                _uiState.update { it.copy(error = "Ingresa el número de asistentes.") }

            state.purpose.isBlank() ->
                _uiState.update { it.copy(error = "Describe el propósito de la reserva.") }

            else -> viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val companions = state.companions.trim().toIntOrNull() ?: 1
                val type = if (companions > 1) ReservationType.GROUP else ReservationType.SINGLE

                when (val result = reservationRepository.createReservation(
                    reservableId = reservableId,
                    date         = state.date,
                    startTime    = state.startTime,
                    endTime      = state.endTime,
                    type         = type,
                    companions   = if (type == ReservationType.GROUP) companions else null
                )) {
                    is NetworkResult.Success -> {
                        val reservationId = result.data.id
                        if (state.purpose.isNotBlank()) {
                            reservationRepository.addNote(reservationId, state.purpose)
                        }
                        _uiState.update {
                            it.copy(isLoading = false, createdReservation = result.data)
                        }
                    }
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = when (result.code) {
                                409  -> "Este horario ya está ocupado. Elige otro."
                                422  -> "Debes reservar este espacio con más anticipación."
                                404  -> "El recurso ya no está disponible."
                                else -> result.message
                            }
                        )
                    }
                    NetworkResult.Loading -> Unit
                }
            }
        }
    }

    private fun searchResources(query: String) {
        val type = _uiState.value.resourceType
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            if (type == ResourceType.SPACE) {
                val result = spaceRepository.searchSpaces(searchQuery = query, size = 10, studentsAvailable = true)
                if (result is NetworkResult.Success) {
                    _uiState.update { it.copy(isSearching = false, searchResults = result.data.content) }
                } else {
                    _uiState.update { it.copy(isSearching = false) }
                }
            } else {
                val result = equipmentRepository.searchEquipments(searchQuery = query, size = 10, studentsAvailable = true)
                if (result is NetworkResult.Success) {
                    _uiState.update { it.copy(isSearching = false, searchResults = result.data.content) }
                } else {
                    _uiState.update { it.copy(isSearching = false) }
                }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
    fun resetForm()  = _uiState.update { CreateReservationUiState() }
}