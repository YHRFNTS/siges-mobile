package dev.spiffocode.sigesmobile.domain.repository

import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.api.UserApiService
import dev.spiffocode.sigesmobile.data.remote.dto.AuthenticatedResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationPreferenceResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationPreferenceUpdateRequest
import dev.spiffocode.sigesmobile.data.remote.dto.PageUserResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ProfilePictureResponse
import dev.spiffocode.sigesmobile.data.remote.dto.PushTokenRequest
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import dev.spiffocode.sigesmobile.data.remote.dto.UpdatePasswordDto
import dev.spiffocode.sigesmobile.data.remote.dto.UserInfoUpdateRequest
import dev.spiffocode.sigesmobile.data.remote.dto.UserResponse
import dev.spiffocode.sigesmobile.data.remote.dto.UserRole
import dev.spiffocode.sigesmobile.data.remote.safeApiCall
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: UserApiService
) {
    suspend fun searchUsers(
        page: Int = 0,
        size: Int = 20,
        sort: String? = null,
        q: String? = null,
        createdBy: String? = null,
        showMode: ShowMode? = null,
        userTypes: List<UserRole>? = null
    ): NetworkResult<PageUserResponse> = safeApiCall {
        api.searchUsers(
            page      = page,
            size      = size,
            sort      = sort,
            q         = q,
            createdBy = createdBy,
            showMode  = showMode,
            userTypes = userTypes
        )
    }

    suspend fun getUserById(id: Long): NetworkResult<UserResponse> =
        safeApiCall { api.getUserById(id) }

    suspend fun lookupByIdentifier(identifier: String): NetworkResult<UserResponse> =
        safeApiCall { api.lookupByIdentifier(identifier) }


    suspend fun updateCommonInfo(id: Long, request: UserInfoUpdateRequest): NetworkResult<UserResponse> =
        safeApiCall { api.updateCommonInfo(id, request) }

    suspend fun updateProfilePicture(filePart: MultipartBody.Part): NetworkResult<ProfilePictureResponse> =
        safeApiCall { api.updateProfilePicture(filePart) }

    suspend fun registerPushToken(token: String, deviceId: String? = null): NetworkResult<Unit> =
        safeApiCall { api.registerPushToken(
            PushTokenRequest(
                token = token,
                deviceId = deviceId
            )
        ) }

    suspend fun unregisterPushToken(token: String): NetworkResult<Unit> =
        safeApiCall { api.unregisterPushToken(token) }

    suspend fun getNotificationPreferences(): NetworkResult<List<NotificationPreferenceResponse>> =
        safeApiCall { api.getNotificationPreferences() }

    suspend fun updateNotificationPreferences(
        preferences: List<NotificationPreferenceUpdateRequest>
    ): NetworkResult<List<NotificationPreferenceResponse>> =
        safeApiCall { api.updateNotificationPreferences(preferences) }

    suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    ) : NetworkResult<AuthenticatedResponse> =
        safeApiCall { api.updatePassword(UpdatePasswordDto(currentPassword, newPassword)) }
}