package dev.spiffocode.sigesmobile.domain.model

data class LoginUiState(
    val email: String = "",
    val contrasena: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)