package dev.spiffocode.sigesmobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
data class EquipmentDto(
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
    @SerializedName("spaceAttached")          val spaceAttached: SpaceDto?,
    @SerializedName("type")                   val type: EquipmentTypeDto?,
    @SerializedName("inventoryIdNum")         val inventoryIdNum: String?
)

data class PageEquipmentDto(
    @SerializedName("content")          val content: List<EquipmentDto>,
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