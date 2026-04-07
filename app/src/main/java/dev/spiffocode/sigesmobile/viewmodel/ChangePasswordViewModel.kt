package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangePasswordUiState(
    val email: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isCurrentPasswordError: Boolean = false,
    val isNewPasswordError: Boolean = false,
    val isConfirmPasswordError: Boolean = false
)

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState(email = sessionManager.email ?: ""))
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun updateCurrentPassword(password: String) {
        _uiState.update { it.copy(currentPassword = password, error = null, successMessage = null, isCurrentPasswordError = false) }
    }

    fun updateNewPassword(password: String) {
        _uiState.update { it.copy(newPassword = password, error = null, successMessage = null, isNewPasswordError = false) }
    }

    fun updateConfirmPassword(password: String) {
        _uiState.update { it.copy(confirmPassword = password, error = null, successMessage = null, isConfirmPasswordError = false) }
    }

    fun submit() {
        val state = _uiState.value
        if (state.currentPassword.isBlank() || state.newPassword.isBlank() || state.confirmPassword.isBlank()) {
            _uiState.update { it.copy(
                error = "Todos los campos obligatorios deben estar llenos.",
                isCurrentPasswordError = it.currentPassword.isBlank(),
                isNewPasswordError = it.newPassword.isBlank(),
                isConfirmPasswordError = it.confirmPassword.isBlank()
            ) }
            return
        }

        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden.", isConfirmPasswordError = true, isNewPasswordError = true) }
            return
        }

        if (state.newPassword.length < 8) {
            _uiState.update { it.copy(error = "La nueva contraseña debe tener al menos 8 caracteres.", isNewPasswordError = true) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }

        viewModelScope.launch {
            val result = repository.updatePassword(state.currentPassword, state.newPassword)
            when (result) {
                is NetworkResult.Success -> {
                    val authResponse = result.data
                    sessionManager.updateTokens(
                        accessToken = authResponse.accessToken,
                        refreshToken = authResponse.refreshToken
                    )
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Contraseña actualizada exitosamente.",
                            currentPassword = "",
                            newPassword = "",
                            confirmPassword = ""
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}