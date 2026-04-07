package dev.spiffocode.sigesmobile.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class LoginUiState(
    val identifier: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val retryAfterSeconds: Int? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    public sealed class UiEvent{
        object LoginSuccess: UiEvent();
        object LoginError: UiEvent();
    }

    private val _uiEvent = Channel<UiEvent>();
    val uiEvent = _uiEvent.receiveAsFlow();

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onIdentifierChange(value: String) = _uiState.update { it.copy(identifier = value, errorMessage = null) }
    fun onPasswordChange(value: String)   = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun togglePasswordVisibility()        = _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    fun toggleRememberMe(checked: Boolean)= _uiState.update { it.copy(rememberMe = checked) }

    init {
        _uiState.update { it.copy(rememberMe = sessionManager.rememberMe) }
    }

    fun login() {
        val state = _uiState.value
        if (state.identifier.isBlank()) { _uiState.update { it.copy(errorMessage = "Ingresa tu usuario o correo.") }; return }
        if (state.password.isBlank())   { _uiState.update { it.copy(errorMessage = "Ingresa tu contraseña.") }; return }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.login(state.identifier.trim(), state.password, state.rememberMe)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) };
                    _uiEvent.send(UiEvent.LoginSuccess);
                }
                is NetworkResult.Error   -> {
                    _uiState.update {
                        it.copy(
                            isLoading         = false,
                            errorMessage      = when (result.code) {
                                401  -> "Credenciales incorrectas."
                                429  -> "Demasiados intentos. Espera e intenta de nuevo."
                                500  -> "Error del servidor. Intenta más tarde."
                                -1   -> "Sin conexión. Verifica tu internet."
                                else -> "Error inesperado."
                            },
                            retryAfterSeconds = if (result.code == 429) 30 else null
                        )
                    }
                    _uiEvent.send(UiEvent.LoginError);
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}