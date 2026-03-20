package dev.spiffocode.sigesmobile.data.remote.api

import dev.spiffocode.sigesmobile.data.remote.dto.AuthenticatedResponse
import dev.spiffocode.sigesmobile.data.remote.dto.LoginRequest
import dev.spiffocode.sigesmobile.data.remote.dto.LogoutRequest
import dev.spiffocode.sigesmobile.data.remote.dto.RefreshRequest
import dev.spiffocode.sigesmobile.data.remote.dto.RefreshResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthenticatedResponse>

    @POST("auth/refresh")
    suspend fun refresh(
        @Body request: RefreshRequest
    ): Response<RefreshResponse>

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") authHeader: String,
        @Body request: LogoutRequest
    ): Response<Unit>
}