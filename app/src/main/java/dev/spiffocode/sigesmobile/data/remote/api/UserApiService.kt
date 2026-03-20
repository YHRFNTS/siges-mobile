package dev.spiffocode.sigesmobile.data.remote.api

import dev.spiffocode.sigesmobile.data.remote.dto.NotificationPreferenceResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationPreferenceUpdateRequest
import dev.spiffocode.sigesmobile.data.remote.dto.PageUserResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ProfilePictureResponse
import dev.spiffocode.sigesmobile.data.remote.dto.PushTokenRequest
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import dev.spiffocode.sigesmobile.data.remote.dto.UserInfoUpdateRequest
import dev.spiffocode.sigesmobile.data.remote.dto.UserResponse
import dev.spiffocode.sigesmobile.data.remote.dto.UserRole
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {


    @GET("users")
    suspend fun searchUsers(
        @Query("page")      page: Int = 0,
        @Query("size")      size: Int = 20,
        @Query("sort")      sort: String?,
        @Query("q")         q: String?,
        @Query("createdBy") createdBy: String?,
        @Query("showMode")  showMode: ShowMode?,
        @Query("userTypes") userTypes: List<UserRole>?
    ): Response<PageUserResponse>

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: Long
    ): Response<UserResponse>

    @GET("users/lookup")
    suspend fun lookupByIdentifier(
        @Query("identifier") identifier: String
    ): Response<UserResponse>

    @PATCH("users/{id}")
    suspend fun updateCommonInfo(
        @Path("id") id: Long,
        @Body request: UserInfoUpdateRequest
    ): Response<UserResponse>

    @Multipart
    @PUT("users/me/profile-picture")
    suspend fun updateProfilePicture(
        @Part file: MultipartBody.Part
    ): Response<ProfilePictureResponse>

    @POST("users/me/push-tokens")
    suspend fun registerPushToken(
        @Body request: PushTokenRequest
    ): Response<Unit>

    @DELETE("users/me/push-tokens/{token}")
    suspend fun unregisterPushToken(
        @Path("token") token: String
    ): Response<Unit>

    @GET("users/me/notification-preferences")
    suspend fun getNotificationPreferences(): Response<List<NotificationPreferenceResponse>>

    @PUT("users/me/notification-preferences")
    suspend fun updateNotificationPreferences(
        @Body request: List<NotificationPreferenceUpdateRequest>
    ): Response<List<NotificationPreferenceResponse>>
}