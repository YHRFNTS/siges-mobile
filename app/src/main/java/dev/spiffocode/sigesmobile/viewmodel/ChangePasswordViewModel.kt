package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.domain.repository.PasswordRecoveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangePasswordUiState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val userEmail: String = ""
)

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repository: PasswordRecoveryRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun onNewPasswordChange(value: String)     = _uiState.update { it.copy(newPassword = value, errorMessage = null) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    fun toggleNewPasswordVisibility()          = _uiState.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
    fun toggleConfirmPasswordVisibility()      = _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }

    fun updatePassword(token: String) {
        val state = _uiState.value
        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$")
        when {
            state.newPassword.length < 8 ->
                _uiState.update { it.copy(errorMessage = "Mínimo 8 caracteres.") }
            !passwordRegex.matches(state.newPassword) ->
                _uiState.update { it.copy(errorMessage = "Debe incluir mayúscula, minúscula, número y carácter especial.") }
            state.newPassword != state.confirmPassword ->
                _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden.") }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                when (val result = repository.resetPassword(token, state.newPassword)) {
                    is NetworkResult.Success -> _uiState.update {
                        it.copy(isLoading = false, isSuccess = true)
                    }
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(
                            isLoading    = false,
                            errorMessage = when (result.code) {
                                410  -> "El enlace expiró o ya fue usado. Solicita uno nuevo."
                                400  -> "Contraseña inválida."
                                -1   -> "Sin conexión. Verifica tu internet."
                                else -> "Error inesperado. Intenta de nuevo."
                            }
                        )
                    }
                    NetworkResult.Loading -> Unit
                }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}