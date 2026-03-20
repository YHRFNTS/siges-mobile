package dev.spiffocode.sigesmobile.data.remote.api

import dev.spiffocode.sigesmobile.data.remote.dto.PasswordUpdateRequest
import dev.spiffocode.sigesmobile.data.remote.dto.RequestAccountRecovery
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface PasswordRecoveryApiService {
    @POST("password-recovery/request")
    suspend fun requestRecovery(
        @Body request: RequestAccountRecovery
    ): Response<Unit>

    @PATCH("password-recovery/reset")
    suspend fun resetPassword(
        @Body request: PasswordUpdateRequest
    ): Response<Unit>
}