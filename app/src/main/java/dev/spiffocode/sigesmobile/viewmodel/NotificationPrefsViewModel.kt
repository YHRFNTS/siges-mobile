package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationPreferenceResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationPreferenceUpdateRequest
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationType
import dev.spiffocode.sigesmobile.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map

data class NotificationPrefsUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val preferences: List<NotificationPreferenceResponse> = emptyList(),
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NotificationPrefsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationPrefsUiState())
    val uiState: StateFlow<NotificationPrefsUiState> = _uiState.asStateFlow()

    init { loadPreferences() }

    fun loadPreferences() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getNotificationPreferences()) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isLoading = false, preferences = result.data)
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun toggleEmail(type: NotificationType, enabled: Boolean) {
        _uiState.update { state ->
            state.copy(
                preferences = state.preferences.map { pref ->
                    if (pref.type == type) pref.copy(emailEnabled = enabled) else pref
                }
            )
        }
    }

    fun toggleInApp(type: NotificationType, enabled: Boolean) {
        _uiState.update { state ->
            state.copy(
                preferences = state.preferences.map { pref ->
                    if (pref.type == type) pref.copy(inAppEnabled = enabled) else pref
                }
            )
        }
    }

    fun savePreferences() {
        val prefs = _uiState.value.preferences
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val requests = prefs.map { pref ->
                NotificationPreferenceUpdateRequest(
                    type = pref.type,
                    emailEnabled = pref.emailEnabled,
                    inAppEnabled = pref.inAppEnabled
                )
            }
            when (val result = repository.updateNotificationPreferences(requests)) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isSaving = false, preferences = result.data, isSaved = true)
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isSaving = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, isSaved = false) }
}