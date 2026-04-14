package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileMenuUiState(
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileMenuViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: dev.spiffocode.sigesmobile.domain.repository.UserRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileMenuUiState())
    val uiState: StateFlow<ProfileMenuUiState> = _uiState.asStateFlow()

    init {
        loadSelfIfMissing()
    }

    private fun loadSelfIfMissing() {
        if (session.firstName.isNullOrBlank() || session.lastName.isNullOrBlank()) {
            viewModelScope.launch {
                val result = userRepository.getSelf()
                if (result is dev.spiffocode.sigesmobile.data.remote.NetworkResult.Success) {
                    val user = result.data
                    session.updateAllUserInfo(user)
                    _uiState.update { it.copy() }
                }
            }
        }
    }

    val fullName: String
        get() {
            val first = session.firstName ?: ""
            val last  = session.lastName  ?: ""
            return "$first $last".trim()
        }

    val initials: String
        get() {
            val first = session.firstName?.firstOrNull()?.uppercaseChar() ?: ""
            val last  = session.lastName?.firstOrNull()?.uppercaseChar()  ?: ""
            return "$first$last"
        }

    val roleLabel: String
        get() = when (session.role) {
            "INSTITUTIONAL_STAFF" -> "Personal Institucional"
            "STUDENT"             -> "Estudiante"
            "ADMIN"               -> "Administrador"
            else                  -> session.role ?: ""
        }

    val identifier: String
        get() = session.employeeNumber
            ?: session.registrationNumber
            ?: ""

    val profilePictureUrl: String
        get() = session.profilePictureUrl ?: "- "


    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.logout()
            _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}