package dev.spiffocode.sigesmobile.domain.repository

import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.api.NotificationApiService
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationReadStatus
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationStatusChangeRequest
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationType
import dev.spiffocode.sigesmobile.data.remote.dto.PageNotificationResponse
import dev.spiffocode.sigesmobile.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val api: NotificationApiService
) {
    suspend fun listNotifications(
        page: Int = 0,
        size: Int = 20,
        sort: String? = null,
        status: NotificationReadStatus? = null,
        type: NotificationType? = null
    ): NetworkResult<PageNotificationResponse> = safeApiCall {
        api.listNotifications(
            page   = page,
            size   = size,
            sort   = sort,
            status = status,
            type   = type
        )
    }

    suspend fun markAsRead(id: Long): NetworkResult<NotificationResponse> =
        safeApiCall {
            api.changeNotificationStatus(id,
                NotificationStatusChangeRequest(NotificationReadStatus.READ)
            )
        }

    suspend fun markAsUnread(id: Long): NetworkResult<NotificationResponse> =
        safeApiCall {
            api.changeNotificationStatus(id,
                NotificationStatusChangeRequest(NotificationReadStatus.UNREAD)
            )
        }

    suspend fun markAllAsRead(): NetworkResult<Unit> =
        safeApiCall {
            api.changeAllNotificationsStatus(NotificationStatusChangeRequest(NotificationReadStatus.READ))
        }

    suspend fun markAllAsUnread(): NetworkResult<Unit> =
        safeApiCall {
            api.changeAllNotificationsStatus(NotificationStatusChangeRequest(NotificationReadStatus.UNREAD))
        }
}