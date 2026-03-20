package dev.spiffocode.sigesmobile.domain.repository

import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.api.AuthApiService
import dev.spiffocode.sigesmobile.data.remote.dto.AuthenticatedResponse
import dev.spiffocode.sigesmobile.data.remote.dto.LoginRequest
import dev.spiffocode.sigesmobile.data.remote.dto.LogoutRequest
import dev.spiffocode.sigesmobile.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApiService,
    private val session: SessionManager
) {
    suspend fun login(identifier: String, password: String): NetworkResult<AuthenticatedResponse> {
        val result = safeApiCall { api.login(LoginRequest(identifier, password)) }
        if (result is NetworkResult.Success) {
            session.saveSession(
                accessToken  = result.data.accessToken,
                refreshToken = result.data.refreshToken,
                role         = result.data.role.name
            )
        }
        return result
    }

    suspend fun logout(): NetworkResult<Unit> {
        val accessToken  = session.accessToken  ?: return NetworkResult.Error(401, "No hay sesión activa")
        val refreshToken = session.refreshToken ?: return NetworkResult.Error(401, "No hay sesión activa")

        val result = safeApiCall {
            api.logout(
                authHeader = "Bearer $accessToken",
                request    = LogoutRequest(refreshToken)
            )
        }
        session.clearSession()
        return result
    }

    fun isLoggedIn() = session.isLoggedIn
    fun currentRole() = session.role
}