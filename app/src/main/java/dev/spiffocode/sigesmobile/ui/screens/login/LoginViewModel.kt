package dev.spiffocode.sigesmobile.ui.screens.login

import androidx.lifecycle.ViewModel
import dev.spiffocode.sigesmobile.domain.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleRememberMe(checked: Boolean) {
        _uiState.update { it.copy(rememberMe = checked) }
    }

    fun login() {
        val currentState = _uiState.value

        val emailLimpio = currentState.email.trim()
        val contrasena = currentState.password

        if (emailLimpio.isBlank() || contrasena.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor, completa todos los campos.") }
            return
        }

        val tieneUnSoloArroba = emailLimpio.count { it == '@' } == 1
        val terminaConDominio = emailLimpio.endsWith("@utez.edu.mx")

        if (!tieneUnSoloArroba || !terminaConDominio) {
            _uiState.update { it.copy(errorMessage = "Ingresa un correo institucional válido.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    }

}