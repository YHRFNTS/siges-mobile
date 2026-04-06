package dev.spiffocode.sigesmobile.domain.repository

import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.api.AuthApiService
import dev.spiffocode.sigesmobile.data.remote.api.UserApiService
import dev.spiffocode.sigesmobile.data.remote.dto.AuthenticatedResponse
import dev.spiffocode.sigesmobile.data.remote.dto.LoginRequest
import dev.spiffocode.sigesmobile.data.remote.dto.LogoutRequest
import dev.spiffocode.sigesmobile.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApiService,
    private val userApi: UserApiService,
    private val session: SessionManager
) {
    suspend fun login(identifier: String, password: String): NetworkResult<AuthenticatedResponse> {
        val result = safeApiCall { authApi.login(LoginRequest(identifier, password)) }

        if (result is NetworkResult.Success) {
            val auth = result.data

            session.saveSession(
                id = "",
                accessToken  = auth.accessToken,
                refreshToken = auth.refreshToken,
                role         = auth.role.name,
                firstName    = "",
                lastName     = "",
                email        = "",
                phoneNumber  = "",
                birthDate    = ""
            )

            val profileResult = safeApiCall { userApi.lookupByIdentifier(identifier.trim()) }
            if (profileResult is NetworkResult.Success) {
                val user = profileResult.data
                session.saveSession(
                    id                 = user.id.toString(),
                    accessToken        = auth.accessToken,
                    refreshToken       = auth.refreshToken,
                    role               = auth.role.name,
                    firstName          = user.firstName,
                    lastName           = user.lastName,
                    email              = user.email,
                    employeeNumber     = user.employeeNumber,
                    registrationNumber = user.registrationNumber,
                    profilePictureUrl  = user.profilePictureUrl,
                    phoneNumber        = user.phoneNumber,
                    birthDate          = user.birthDate.toString()
                )
            }
        }

        return result
    }

    suspend fun logout(): NetworkResult<Unit> {
        val accessToken  = session.accessToken  ?: return NetworkResult.Error(401, "No hay sesión activa")
        val refreshToken = session.refreshToken ?: return NetworkResult.Error(401, "No hay sesión activa")

        val result = safeApiCall {
            authApi.logout(
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