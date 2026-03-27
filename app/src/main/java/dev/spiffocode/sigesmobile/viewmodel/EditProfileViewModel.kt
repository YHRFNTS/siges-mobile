package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.local.SessionManager
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
    val isUploadingPicture: Boolean = false,
    val profilePictureUrl: String? = null,
    val error: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private var userId: Long = -1L

    init {
        _uiState.update { it.copy(profilePictureUrl = session.profilePictureUrl) }
        _uiState.update {
            it.copy(
                firstName         = session.firstName ?: "",
                lastName          = session.lastName ?: "",
                email             = session.email ?: "",
                role              = session.role?.let {role -> UserRole.valueOf(role)} ?: UserRole.STUDENT,
                employeeNumber    = session.employeeNumber,
                registrationNumber = session.registrationNumber,
                profilePictureUrl = session.profilePictureUrl
            )
        }
    }

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
                registrationNumber = user.registrationNumber,
                profilePictureUrl  = user.profilePictureUrl ?: session.profilePictureUrl
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

    fun uploadProfilePicture(imageBytes: ByteArray, mimeType: String = "image/jpeg") {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPicture = true, error = null) }
            val requestBody = imageBytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", "profile_pic.jpg", requestBody)

            when (val result = repository.updateProfilePicture(filePart)) {
                is NetworkResult.Success -> {
                    val newUrl = result.data.profilePictureUrl
                    session.updateProfilePictureUrl(newUrl)
                    _uiState.update { it.copy(isUploadingPicture = false, profilePictureUrl = newUrl) }
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isUploadingPicture = false, error = result.message ?: "Error al subir imagen.")
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, isSaved = false) }
}
