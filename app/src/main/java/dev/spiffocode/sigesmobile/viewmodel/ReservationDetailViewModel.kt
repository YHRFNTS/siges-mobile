package dev.spiffocode.sigesmobile.viewmodel

import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.domain.repository.ReservationRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReservationDetailUiState(
    val isLoading: Boolean = false,
    val reservation: ReservationResponse? = null,
    val adminNote: String = "",
    val rejectReason: String = "",
    val actionSuccess: String? = null,
    val error: String? = null
)

@HiltViewModel
class ReservationDetailViewModel @Inject constructor(
    private val repository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationDetailUiState())
    val uiState: StateFlow<ReservationDetailUiState> = _uiState.asStateFlow()

    fun onAdminNoteChange(value: String) = _uiState.update { it.copy(adminNote = value) }
    fun onRejectReasonChange(value: String) = _uiState.update { it.copy(rejectReason = value) }

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

    fun approve(id: Long) = launchAction("Solicitud aprobada") {
        repository.approveReservation(id)
    }

    fun reject(id: Long) {
        val reason = _uiState.value.rejectReason.trim()
        if (reason.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa un motivo para denegar la solicitud.") }
            return
        }
        launchAction("Solicitud denegada") { repository.rejectReservation(id, reason) }
    }

    fun addNote(id: Long) {
        val note = _uiState.value.adminNote.trim()
        if (note.isBlank()) {
            _uiState.update { it.copy(error = "La observación no puede estar vacía.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.addNote(id, note)) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(
                        isLoading     = false,
                        reservation   = result.data,
                        adminNote     = "",
                        actionSuccess = "Observación agregada"
                    )
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }


    fun cancel(id: Long, reason: String) {
        if (reason.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa un motivo para cancelar.") }
            return
        }
        launchAction("Solicitud cancelada") { repository.cancelReservation(id, reason) }
    }

    private fun launchAction(
        successMsg: String,
        action: suspend () -> NetworkResult<ReservationResponse>
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, actionSuccess = null) }
            when (val result = action()) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(
                        isLoading     = false,
                        reservation   = result.data,
                        actionSuccess = successMsg
                    )
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        error     = when (result.code) {
                            409  -> "La solicitud ya no está en el estado requerido."
                            403  -> "No tienes permiso para realizar esta acción."
                            else -> result.message
                        }
                    )
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, actionSuccess = null) }
}