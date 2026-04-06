package dev.spiffocode.sigesmobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.Duration
import java.time.LocalDateTime


data class ReservableDto(
    @SerializedName("id")                     val id: Long,
    @SerializedName("reservableType")         val reservableType: ReservableType,
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

    // optional if space
    @SerializedName("spaceType")              val spaceType: SpaceTypeDto? = null,
    @SerializedName("bookInAdvanceDuration")  val bookInAdvanceDuration: Duration? = null,
    @SerializedName("capacity")               val capacity: Int? = null,
    @SerializedName("assets")                 val assets: List<SpaceAssetDto>? = null,

    // optional if equipment
    @SerializedName("spaceAttached")          val spaceAttached: SpaceDto? = null,
    @SerializedName("type")                   val type: EquipmentTypeDto? = null,
    @SerializedName("inventoryIdNum")         val inventoryIdNum: String? = null
)

data class PageReservableDto(
    @SerializedName("content")          val content: List<ReservableDto>,
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