package dev.spiffocode.sigesmobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

enum class ReservableStatus { AVAILABLE, MAINTENANCE, LOANED }

enum class ShowMode { ACTIVE, INACTIVE, ALL }


data class SpaceTypeDto(
    @SerializedName("id")          val id: Long,
    @SerializedName("name")        val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("deletedAt")   val deletedAt: LocalDateTime?
)

data class SpaceAssetDto(
    @SerializedName("id")           val id: Long,
    @SerializedName("name")         val name: String,
    @SerializedName("description")  val description: String?,
    @SerializedName("inventoryNum") val inventoryNum: String,
    @SerializedName("space")        val space: SpaceSummaryDto?,
    @SerializedName("type")         val type: EquipmentTypeDto?,
    @SerializedName("createdAt")    val createdAt: LocalDateTime?,
    @SerializedName("updatedAt")    val updatedAt: LocalDateTime?,
    @SerializedName("createdBy")    val createdBy: String?,
    @SerializedName("deletedAt")    val deletedAt: LocalDateTime?
)

data class PageSpaceAssetDto(
    @SerializedName("content")          val content: List<SpaceAssetDto>,
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

data class SpaceSummaryDto(
    @SerializedName("id")                     val id: Long,
    @SerializedName("name")                   val name: String,
    @SerializedName("status")                 val status: ReservableStatus,
    @SerializedName("description")            val description: String,
    @SerializedName("availableForStudents")   val availableForStudents: Boolean,
    @SerializedName("building")               val building: BuildingDto?,
    @SerializedName("createdAt")              val createdAt: LocalDateTime?,
    @SerializedName("updatedAt")              val updatedAt: LocalDateTime?,
    @SerializedName("createdBy")              val createdBy: String?,
    @SerializedName("deletedAt")              val deletedAt: LocalDateTime?,
    @SerializedName("availabilitySlots")      val availabilitySlots: List<AvailabilitySlotDto>,
    @SerializedName("availabilityExceptions") val availabilityExceptions: List<AvailabilityExceptionDto>
)

data class SpaceDto(
    @SerializedName("id")                     val id: Long,
    @SerializedName("name")                   val name: String,
    @SerializedName("status")                 val status: ReservableStatus,
    @SerializedName("description")            val description: String?,
    @SerializedName("availableForStudents")   val availableForStudents: Boolean,
    @SerializedName("building")               val building: BuildingDto?,
    @SerializedName("createdAt")              val createdAt: LocalDateTime?,
    @SerializedName("updatedAt")              val updatedAt: LocalDateTime?,
    @SerializedName("createdBy")              val createdBy: String?,
    @SerializedName("deletedAt")              val deletedAt: LocalDateTime?,
    @SerializedName("availabilitySlots")      val availabilitySlots: List<AvailabilitySlotDto>?,
    @SerializedName("availabilityExceptions") val availabilityExceptions: List<AvailabilityExceptionDto>?,
    @SerializedName("spaceType")              val spaceType: SpaceTypeDto?,
    @SerializedName("bookInAdvanceDuration")  val bookInAdvanceDuration: Duration,
    @SerializedName("capacity")               val capacity: Int?,
    @SerializedName("assets")                 val assets: List<SpaceAssetDto>?
)

data class PageSpaceDto(
    @SerializedName("content")          val content: List<SpaceDto>,
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