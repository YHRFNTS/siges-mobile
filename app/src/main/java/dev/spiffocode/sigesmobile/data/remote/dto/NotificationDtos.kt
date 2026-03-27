package dev.spiffocode.sigesmobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

enum class NotificationReadStatus { READ, UNREAD }


data class NotificationStatusChangeRequest(
    @SerializedName("status") val status: NotificationReadStatus
)


data class ReservationSummaryResponse(
    @SerializedName("id")          val id: Long,
    @SerializedName("status")      val status: ReservationStatus,
    @SerializedName("date")        val date: LocalDate,
    @SerializedName("startTime")   val startTime: LocalTime,
    @SerializedName("endTime")     val endTime: LocalTime,
    @SerializedName("type")        val type: ReservationType,
    @SerializedName("companions")  val companions: Int?,
    @SerializedName("approvedAt")  val approvedAt: LocalDateTime?,
    @SerializedName("rejectedAt")  val rejectedAt: LocalDateTime?,
    @SerializedName("cancelledAt") val cancelledAt: LocalDateTime?,
    @SerializedName("finishedAt")  val finishedAt: LocalDateTime?,
    @SerializedName("createdAt")   val createdAt: LocalDateTime?
)

data class NotificationResponse(
    @SerializedName("id")          val id: Long,
    @SerializedName("title")       val title: String,
    @SerializedName("message")     val message: String,
    @SerializedName("readStatus")  val readStatus: NotificationReadStatus,
    @SerializedName("type")        val type: NotificationType,
    @SerializedName("sentAt")      val sentAt: LocalDateTime,
    @SerializedName("reservation") val reservation: ReservationSummaryResponse?,
    @SerializedName("metadata")    val metadata: NotificationMetadata?
)
data class NotificationMetadata(
    @SerializedName("platform")      val platform: NotificationPlatform? = null,
    @SerializedName("reservationId") val reservationId: Long? = null,
    @SerializedName("reservableId")  val reservableId: Long? = null,
    @SerializedName("issuedByName")  val issuedByName: String? = null,
    @SerializedName("issuedById")    val issuedById: Long? = null
)

enum class NotificationPlatform {
    WEB, MOBILE
}

data class PageNotificationResponse(
    @SerializedName("content")          val content: List<NotificationResponse>,
    @SerializedName("totalElements")    val totalElements: Long,
    @SerializedName("totalPages")       val totalPages: Int,
    @SerializedName("number")           val number: Int,
    @SerializedName("size")             val size: Int,
    @SerializedName("first")            val first: Boolean,
    @SerializedName("last")             val last: Boolean,
    @SerializedName("numberOfElements") val numberOfElements: Int,
    @SerializedName("empty")            val empty: Boolean,
    @SerializedName("pageable")         val pageable: PageableObject?,
    @SerializedName("sort")             val sort: SortObject?
)