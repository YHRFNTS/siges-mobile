package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.domain.repository.PasswordRecoveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSent: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: PasswordRecoveryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }

    fun sendRecoveryEmail() {
        val email = _uiState.value.email.trim()
        if (email.isBlank()) { _uiState.update { it.copy(errorMessage = "Ingresa un correo.") }; return }
        if (!email.endsWith("@utez.edu.mx")) { _uiState.update { it.copy(errorMessage = "Usa tu correo institucional (@utez.edu.mx).") }; return }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.requestRecovery(email)) {
                is NetworkResult.Success -> _uiState.update { it.copy(isLoading = false, isSent = true) }
                is NetworkResult.Error   -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = if (result.code == -1) "Sin conexión." else "Error inesperado.")
                }
                NetworkResult.Loading -> Unit
            }
        }
    }
}
