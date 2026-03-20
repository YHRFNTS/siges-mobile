package dev.spiffocode.sigesmobile.data.remote.api

import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentDto
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentTypeDto
import dev.spiffocode.sigesmobile.data.remote.dto.PageEquipmentDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

interface EquipmentApiService {

    @GET("equipments")
    suspend fun searchEquipments(
        @Query("page")              page: Int = 0,
        @Query("size")              size: Int = 20,
        @Query("sort")              sort: String?,
        @Query("searchQuery")       searchQuery: String?,
        @Query("status")            status: ReservableStatus?,
        @Query("buildingId")        buildingId: Long?,
        @Query("studentsAvailable") studentsAvailable: Boolean?,
        @Query("spaceId")           spaceId: Long?,
        @Query("equipmentTypeId")   equipmentTypeId: Long?,
        @Query("requestStart")      requestStart: LocalDateTime?,
        @Query("requestEnd")        requestEnd: LocalDateTime?,
        @Query("showMode")          showMode: ShowMode?
    ): Response<PageEquipmentDto>

    @GET("equipments/{id}")
    suspend fun getEquipment(
        @Path("id") id: Long
    ): Response<EquipmentDto>


    @GET("equipment-types")
    suspend fun getAllEquipmentTypes(
        @Query("q")        q: String? = null,
        @Query("showMode") showMode: ShowMode? = null
    ): Response<List<EquipmentTypeDto>>

    @GET("equipment-types/{id}")
    suspend fun getEquipmentType(
        @Path("id") id: Long
    ): Response<EquipmentTypeDto>
}