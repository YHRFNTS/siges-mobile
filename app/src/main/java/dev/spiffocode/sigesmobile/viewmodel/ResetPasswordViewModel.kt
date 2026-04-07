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


enum class ResetPasswordError { EXPIRED_TOKEN, ALREADY_USED_TOKEN, GENERIC }

data class ResetPasswordUiState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val tokenError: ResetPasswordError? = null,
    val isNewPasswordError: Boolean = false,
    val isConfirmPasswordError: Boolean = false
)

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repository: PasswordRecoveryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onNewPasswordChange(value: String)     = _uiState.update { it.copy(newPassword = value, errorMessage = null, isNewPasswordError = false) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, errorMessage = null, isConfirmPasswordError = false) }
    fun toggleNewPasswordVisibility()          = _uiState.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
    fun toggleConfirmPasswordVisibility()      = _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }

    fun resetPassword(token: String) {
        val state = _uiState.value
        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$")
        when {
            state.newPassword.length < 8       -> _uiState.update { it.copy(errorMessage = "Mínimo 8 caracteres.", isNewPasswordError = true) }
            !passwordRegex.matches(state.newPassword) -> _uiState.update { it.copy(errorMessage = "Debe incluir mayúscula, minúscula, número y carácter especial.", isNewPasswordError = true) }
            state.newPassword != state.confirmPassword -> _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden.", isConfirmPasswordError = true, isNewPasswordError = true) }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, tokenError = null) }
                when (val result = repository.resetPassword(token, state.newPassword)) {
                    is NetworkResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    is NetworkResult.Error   -> {
                        val tokenError = if (result.code == 410) {
                            when {
                                result.message.contains("token_expired", ignoreCase = true) -> ResetPasswordError.EXPIRED_TOKEN
                                result.message.contains("token_used", ignoreCase = true) -> ResetPasswordError.ALREADY_USED_TOKEN
                                else -> ResetPasswordError.EXPIRED_TOKEN
                            }
                        } else null
                        _uiState.update {
                            it.copy(
                                isLoading    = false,
                                tokenError   = tokenError,
                                errorMessage = if (tokenError != null) null else when (result.code) {
                                    -1   -> "Sin conexión."
                                    else -> "Error inesperado."
                                }
                            )
                        }
                    }
                    NetworkResult.Loading -> Unit
                }
            }
        }
    }
}