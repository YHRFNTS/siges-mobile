package dev.spiffocode.sigesmobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RequestAccountRecovery(
    @SerializedName("email") val email: String,
) {
    @SerializedName("platform")
    val platform: String = "MOBILE"
}

data class PasswordUpdateRequest(
    @SerializedName("token") val token: String,
    @SerializedName("newPassword") val newPassword: String
)