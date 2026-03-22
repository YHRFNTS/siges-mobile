package dev.spiffocode.sigesmobile.domain.repository

import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.api.PasswordRecoveryApiService
import dev.spiffocode.sigesmobile.data.remote.dto.PasswordUpdateRequest
import dev.spiffocode.sigesmobile.data.remote.dto.RequestAccountRecovery
import dev.spiffocode.sigesmobile.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordRecoveryRepository @Inject constructor(
    private val api: PasswordRecoveryApiService
) {
    suspend fun requestRecovery(email: String): NetworkResult<Unit> =
        safeApiCall { api.requestRecovery(RequestAccountRecovery(email)) }

    suspend fun resetPassword(token: String, newPassword: String): NetworkResult<Unit> =
        safeApiCall { api.resetPassword(PasswordUpdateRequest(token, newPassword)) }
}