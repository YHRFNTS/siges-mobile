package dev.spiffocode.sigesmobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DashboardStatsDto(
    @SerializedName("pendingRequests")             val pendingRequests: Int,
    @SerializedName("pendingRequestsToday")        val pendingRequestsToday: Int,
    @SerializedName("pendingRequestsPercentage")   val pendingRequestsPercentage: Double,
    @SerializedName("pendingRequestsDiffYesterday") val pendingRequestsDiffYesterday: Int,

    @SerializedName("availableSpaces")             val availableSpaces: Int,
    @SerializedName("totalSpaces")                 val totalSpaces: Int,
    @SerializedName("availableSpacesPercentage")   val availableSpacesPercentage: Double,
    @SerializedName("availableSpacesDiffYesterday") val availableSpacesDiffYesterday: Int,

    @SerializedName("inUseEquipments")             val inUseEquipments: Int,
    @SerializedName("totalEquipments")             val totalEquipments: Int,
    @SerializedName("inUseEquipmentsPercentage")   val inUseEquipmentsPercentage: Double,
    @SerializedName("inUseEquipmentsDiffYesterday") val inUseEquipmentsDiffYesterday: Int,

    @SerializedName("todayReservations")           val todayReservations: Int,
    @SerializedName("avgDailyReservations30d")     val avgDailyReservations30d: Double,
    @SerializedName("todayReservationsDiffAvg")    val todayReservationsDiffAvg: Double,
    @SerializedName("reservationsThisMonth")       val reservationsThisMonth: Int
)

enum class ReservableType { SPACE, EQUIPMENT}

data class ResourceStatsDto(
    @SerializedName("reservableId")             val reservableId: Long,
    @SerializedName("resourceName")             val resourceName: String,
    @SerializedName("resourceStatus")           val resourceStatus: String,
    @SerializedName("resourceType")             val resourceType: ReservableType,
    @SerializedName("totalReservations")        val totalReservations: Long,
    @SerializedName("reservationsThisMonth")    val reservationsThisMonth: Long,
    @SerializedName("occupancyRate")            val occupancyRate: Double,
    @SerializedName("avgDaysBetweenReservations") val avgDaysBetweenReservations: Double
)