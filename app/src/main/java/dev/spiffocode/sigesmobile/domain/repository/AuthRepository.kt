package dev.spiffocode.sigesmobile.domain.repository

import com.spiffocode.siges.data.local.SessionManager
import com.spiffocode.siges.data.remote.api.AuthApiService
import com.spiffocode.siges.data.remote.dto.auth.LoginRequest
import com.spiffocode.siges.data.remote.dto.auth.LogoutRequest
import com.spiffocode.siges.data.remote.dto.auth.AuthenticatedResponse
import com.spiffocode.siges.utils.NetworkResult
import com.spiffocode.siges.utils.safeApiCall
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.api.AuthApiService
import dev.spiffocode.sigesmobile.data.remote.dto.AuthenticatedResponse
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
                role         = result.data.role
            )
        }
        return result
    }

    suspend fun logout(): NetworkResult<Unit> {
        val accessToken  = session.accessToken  ?: return NetworkResult.Error(401, "Not logged in")
        val refreshToken = session.refreshToken ?: return NetworkResult.Error(401, "Not logged in")

        val result = safeApiCall {
            api.logout(
                authHeader = "Bearer $accessToken",
                request    = LogoutRequest(refreshToken)
            )
        }
        // Clear session regardless of server response
        session.clearSession()
        return result
    }

    fun isLoggedIn() = session.isLoggedIn
    fun currentRole() = session.role
}