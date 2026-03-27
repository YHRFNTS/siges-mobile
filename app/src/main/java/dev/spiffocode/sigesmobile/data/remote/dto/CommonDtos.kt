package dev.spiffocode.sigesmobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


data class PageableObject(
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize")   val pageSize: Int,
    @SerializedName("paged")      val paged: Boolean,
    @SerializedName("unpaged")    val unpaged: Boolean
)

data class SortObject(
    @SerializedName("sorted")   val sorted: Boolean,
    @SerializedName("unsorted") val unsorted: Boolean,
    @SerializedName("empty")    val empty: Boolean
)


data class ValidationProblem(
    @SerializedName("type")   val type: String?,
    @SerializedName("title")  val title: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("detail") val detail: String?,
    @SerializedName("errors") val errors: List<String>?
)

data class ProblemDetail(
    @SerializedName("type")   val type: String?,
    @SerializedName("title")  val title: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("detail") val detail: String?
)


data class BuildingDto(
    @SerializedName("id")        val id: Long,
    @SerializedName("name")      val name: String,
    @SerializedName("deletedAt") val deletedAt: LocalDateTime?
)

data class EquipmentTypeDto(
    @SerializedName("id")          val id: Long,
    @SerializedName("name")        val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("deletedAt")   val deletedAt: LocalDateTime? = null
)

data class AvailabilitySlotDto(
    @SerializedName("id")          val id: Long?,
    @SerializedName("reservableId") val reservableId: Long?,
    @SerializedName("dateFrom")    val dateFrom: LocalDate?,
    @SerializedName("dateTo")      val dateTo: LocalDate?,
    @SerializedName("startTime")   val startTime: LocalTime,
    @SerializedName("endTime")     val endTime: LocalTime,
    @SerializedName("daysOfWeek")  val daysOfWeek: List<DayOfWeek>?
)

data class AvailabilityExceptionDto(
    @SerializedName("id")        val id: Long?,
    @SerializedName("dateFrom")  val dateFrom: LocalDate,
    @SerializedName("dateTo")    val dateTo: LocalDate,
    @SerializedName("startTime") val startTime: LocalTime,
    @SerializedName("endTime")   val endTime: LocalTime,
    @SerializedName("reason")    val reason: String
)

open class UserResponse(
    @SerializedName("id")                val id: Long,
    @SerializedName("email")             val email: String,
    @SerializedName("phoneNumber")       val phoneNumber: String?,
    @SerializedName("firstName")         val firstName: String,
    @SerializedName("lastName")          val lastName: String,
    @SerializedName("birthDate")         val birthDate: LocalDate,
    @SerializedName("createdAt")         val createdAt: LocalDateTime,
    @SerializedName("createdBy")         val createdBy: String,
    @SerializedName("deletedAt")         val deletedAt: LocalDateTime?,
    @SerializedName("role")              val role: UserRole,
    @SerializedName("enabled")           val enabled: Boolean,
    @SerializedName("profilePictureUrl") val profilePictureUrl: String?,
    @SerializedName("registrationNumber") val registrationNumber: String?,
    @SerializedName("employeeNumber")     val employeeNumber: String?
)