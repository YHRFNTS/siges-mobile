package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.UserInfoUpdateRequest
import dev.spiffocode.sigesmobile.data.remote.dto.UserResponse
import dev.spiffocode.sigesmobile.data.remote.dto.UserRole
import dev.spiffocode.sigesmobile.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val birthDate: LocalDate? = LocalDate.now(),
    val email: String = "",
    val role: UserRole? = UserRole.STUDENT,
    val employeeNumber: String? = null,
    val registrationNumber: String? = null,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private var userId: Long = -1L

    fun loadUser(user: UserResponse) {
        userId = user.id
        _uiState.update {
            it.copy(
                firstName          = user.firstName,
                lastName           = user.lastName,
                phoneNumber        = user.phoneNumber ?: "",
                birthDate          = user.birthDate,
                email              = user.email,
                role               = user.role,
                employeeNumber     = user.employeeNumber,
                registrationNumber = user.registrationNumber
            )
        }
    }

    fun onFirstNameChange(value: String)    = _uiState.update { it.copy(firstName = value, error = null) }
    fun onLastNameChange(value: String)     = _uiState.update { it.copy(lastName = value, error = null) }
    fun onPhoneNumberChange(value: String)  = _uiState.update { it.copy(phoneNumber = value, error = null) }
    fun onBirthDateChange(value: LocalDate)    = _uiState.update { it.copy(birthDate = value, error = null) }

    fun saveChanges() {
        val state = _uiState.value
        when {
            state.firstName.isBlank() ->
                _uiState.update { it.copy(error = "El nombre no puede estar vacío.") }
            state.lastName.isBlank() ->
                _uiState.update { it.copy(error = "El apellido no puede estar vacío.") }
            state.phoneNumber.isBlank() ->
                _uiState.update { it.copy(error = "El teléfono no puede estar vacío.") }
            state.birthDate == null ->
                _uiState.update { it.copy(error = "La fecha de nacimiento no puede estar vacía.") }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                when (val result = repository.updateCommonInfo(
                    id      = userId,
                    request = UserInfoUpdateRequest(
                        phoneNumber = state.phoneNumber.trim(),
                        firstName = state.firstName.trim(),
                        lastName = state.lastName.trim(),
                        birthDate = state.birthDate
                    )
                )) {
                    is NetworkResult.Success -> _uiState.update {
                        it.copy(isLoading = false, isSaved = true)
                    }
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = when (result.code) {
                                409  -> "Este número de teléfono ya está en uso."
                                else -> result.message ?: "Error al guardar."
                            }
                        )
                    }
                    NetworkResult.Loading -> Unit
                }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, isSaved = false) }
}
