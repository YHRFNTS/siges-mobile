package dev.spiffocode.sigesmobile.data.remote.api

import dev.spiffocode.sigesmobile.data.remote.dto.NotificationReadStatus
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationStatusChangeRequest
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationType
import dev.spiffocode.sigesmobile.data.remote.dto.PageNotificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {

    @GET("notifications")
    suspend fun listNotifications(
        @Query("page")   page: Int = 0,
        @Query("size")   size: Int = 20,
        @Query("sort")   sort: String?,
        @Query("status") status: NotificationReadStatus?,
        @Query("type")   type: NotificationType?
    ): Response<PageNotificationResponse>

    @PATCH("notifications/{id}/status")
    suspend fun changeNotificationStatus(
        @Path("id") id: Long,
        @Body request: NotificationStatusChangeRequest
    ): Response<NotificationResponse>

    @PATCH("notifications/status")
    suspend fun changeAllNotificationsStatus(
        @Body request: NotificationStatusChangeRequest
    ): Response<Unit>
}