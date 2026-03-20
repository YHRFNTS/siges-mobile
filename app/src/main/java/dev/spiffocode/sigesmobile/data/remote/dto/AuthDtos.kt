package dev.spiffocode.sigesmobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("password")   val password: String
)

data class RefreshRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class LogoutRequest(
    @SerializedName("refreshToken") val refreshToken: String
)
data class AuthenticatedResponse(
    @SerializedName("accessToken")  val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("role")         val role: UserRole,
    @SerializedName("claims")       val claims: List<GrantedAuthority>
)

data class GrantedAuthority(
    @SerializedName("authority") val authority: String
)

data class RefreshResponse(
    @SerializedName("accessToken") val accessToken: String
)

data class InvalidCredentialsProblem(
    @SerializedName("status")            val status: Int,
    @SerializedName("detail")            val detail: String?,
    @SerializedName("remainingAttempts") val remainingAttempts: Int?
)