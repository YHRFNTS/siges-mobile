package dev.spiffocode.sigesmobile.data.remote.dto


import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class UserInfoUpdateRequest(
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("firstName")   val firstName: String,
    @SerializedName("lastName")    val lastName: String,
    @SerializedName("birthDate")   val birthDate: LocalDate
)

data class PushTokenRequest(
    @SerializedName("token")    val token: String,
    @SerializedName("deviceId") val deviceId: String? = null,
) {
    @SerializedName("platform")
    val platform: String = "MOBILE"
}

data class NotificationPreferenceUpdateRequest(
    @SerializedName("type")         val type: NotificationType,
    @SerializedName("emailEnabled") val emailEnabled: Boolean? = null,
    @SerializedName("inAppEnabled") val inAppEnabled: Boolean? = null
)

data class ProfilePictureResponse(
    @SerializedName("profilePictureUrl") val profilePictureUrl: String
)

data class NotificationPreferenceResponse(
    @SerializedName("type")         val type: NotificationType,
    @SerializedName("emailEnabled") val emailEnabled: Boolean,
    @SerializedName("inAppEnabled") val inAppEnabled: Boolean
)

data class PageUserResponse(
    @SerializedName("content")          val content: List<UserResponse>,
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


enum class UserRole { ADMIN, STUDENT, INSTITUTIONAL_STAFF }

enum class NotificationType {
    COMMENT_ON_RESERVATION,
    RESERVATION_RESCHEDULE,
    RESERVATION_REMINDER,
    RESERVATION_CREATED,
    RESERVATION_APPROVED,
    RESERVATION_REJECTED,
    RESERVATION_CANCELLED,
    PASSWORD_CHANGED,
    LOGIN_NEW_DEVICE
}