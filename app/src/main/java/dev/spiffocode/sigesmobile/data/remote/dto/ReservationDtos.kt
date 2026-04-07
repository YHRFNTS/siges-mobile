package dev.spiffocode.sigesmobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class CreateReservationRequest(
    @SerializedName("reservableId") val reservableId: Long,
    @SerializedName("date")         val date: LocalDate,
    @SerializedName("startTime")    val startTime: LocalTime,
    @SerializedName("endTime")      val endTime: LocalTime,
    @SerializedName("type")         val type: ReservationType,
    @SerializedName("companions")   val companions: Int? = null,
    @SerializedName("requestReason") val requestReason: String? = null
)

data class RescheduleReservationRequest(
    @SerializedName("date")      val date: LocalDate,
    @SerializedName("startTime") val startTime: LocalTime,
    @SerializedName("endTime")   val endTime: LocalTime
)

data class RejectReservationRequest(
    @SerializedName("reason") val reason: String
)

data class CancelReservationRequest(
    @SerializedName("reason") val reason: String
)

data class ApproveReservationRequest(
    @SerializedName("observation") val observation: String? = null
)

data class PublishNoteRequest(
    @SerializedName("reservationId") val reservationId: Long,
    @SerializedName("comment")       val comment: String
)

data class EditNoteRequest(
    @SerializedName("comment") val comment: String
)

enum class ReservationType { GROUP, SINGLE }

enum class ReservationStatus { PENDING, APPROVED, REJECTED, CANCELLED, IN_PROGRESS, FINISHED }

data class NoteItem(
    @SerializedName("id")        val id: Long,
    @SerializedName("comment")   val comment: String,
    @SerializedName("createdAt") val createdAt: LocalDateTime?,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime?,
    @SerializedName("createdBy") val createdBy: UserResponse?
)

data class ReservationResponse(
    @SerializedName("id")          val id: Long,
    @SerializedName("petitioner")  val petitioner: UserResponse? = null,
    @SerializedName("reservable")  val reservable: ReservableDto? = null,
    @SerializedName("notes")       val notes: List<NoteItem>? = null,
    @SerializedName("status")      val status: ReservationStatus,
    @SerializedName("date")        val date: LocalDate,
    @SerializedName("startTime")   val startTime: LocalTime,
    @SerializedName("endTime")     val endTime: LocalTime,
    @SerializedName("type")        val type: ReservationType,
    @SerializedName("companions")  val companions: Int? = null,
    @SerializedName("approvedAt")  val approvedAt: LocalDateTime? = null,
    @SerializedName("rejectedAt")  val rejectedAt: LocalDateTime? = null,
    @SerializedName("cancelledAt") val cancelledAt: LocalDateTime? = null,
    @SerializedName("finishedAt")  val finishedAt: LocalDateTime? = null,
    @SerializedName("createdAt")   val createdAt: LocalDateTime? = null,
    @SerializedName("requestReason") val requestReason: String? = null,
    @SerializedName("rejectionReason") val rejectionReason: String? = null,
    @SerializedName("approvalReason") val approvalReason: String? = null
)

data class PageReservationResponse(
    @SerializedName("content")           val content: List<ReservationResponse>,
    @SerializedName("totalElements")     val totalElements: Long,
    @SerializedName("totalPages")        val totalPages: Int,
    @SerializedName("number")            val number: Int,
    @SerializedName("size")              val size: Int,
    @SerializedName("first")             val first: Boolean,
    @SerializedName("last")              val last: Boolean,
    @SerializedName("numberOfElements")  val numberOfElements: Int,
    @SerializedName("empty")             val empty: Boolean,
    @SerializedName("pageable")          val pageable: PageableObject?,
    @SerializedName("sort")              val sort: SortObject?
)

data class TimeBlockItem(
    @SerializedName("start") val start: LocalTime,
    @SerializedName("end")   val end: LocalTime
)

data class OccupiedBlockItem(
    @SerializedName("start")  val start: LocalTime,
    @SerializedName("end")    val end: LocalTime,
    @SerializedName("status") val status: ReservationStatus
)

data class DayAvailabilityItem(
    @SerializedName("date")            val date: LocalDate,
    @SerializedName("availableBlocks") val availableBlocks: List<TimeBlockItem>,
    @SerializedName("occupiedBlocks")  val occupiedBlocks: List<OccupiedBlockItem>
)