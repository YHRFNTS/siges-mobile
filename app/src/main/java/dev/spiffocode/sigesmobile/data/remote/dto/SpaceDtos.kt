package dev.spiffocode.sigesmobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.Duration
import java.time.LocalDateTime

enum class ReservableStatus { AVAILABLE, MAINTENANCE, LOANED }

enum class ShowMode { ACTIVE, INACTIVE, ALL }


data class SpaceTypeDto(
    @SerializedName("id")          val id: Long,
    @SerializedName("name")        val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("deletedAt")   val deletedAt: LocalDateTime? = null
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
    @SerializedName("description")            val description: String? = null,
    @SerializedName("availableForStudents")   val availableForStudents: Boolean,
    @SerializedName("building")               val building: BuildingDto? = null,
    @SerializedName("createdAt")              val createdAt: LocalDateTime? = null,
    @SerializedName("updatedAt")              val updatedAt: LocalDateTime? = null,
    @SerializedName("createdBy")              val createdBy: String? = null,
    @SerializedName("deletedAt")              val deletedAt: LocalDateTime? = null,
    @SerializedName("availabilitySlots")      val availabilitySlots: List<AvailabilitySlotDto>? = null,
    @SerializedName("availabilityExceptions") val availabilityExceptions: List<AvailabilityExceptionDto>? = null,
    @SerializedName("spaceType")              val spaceType: SpaceTypeDto? = null,
    @SerializedName("bookInAdvanceDuration")  val bookInAdvanceDuration: Duration,
    @SerializedName("capacity")               val capacity: Int? = null,
    @SerializedName("assets")                 val assets: List<SpaceAssetDto>? = null
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