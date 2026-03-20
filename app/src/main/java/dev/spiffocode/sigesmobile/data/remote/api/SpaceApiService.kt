package dev.spiffocode.sigesmobile.data.remote.api

import dev.spiffocode.sigesmobile.data.remote.dto.PageSpaceAssetDto
import dev.spiffocode.sigesmobile.data.remote.dto.PageSpaceDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceAssetDto
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceTypeDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

interface SpaceApiService {

    @GET("spaces")
    suspend fun searchSpaces(
        @Query("page")              page: Int = 0,
        @Query("size")              size: Int = 20,
        @Query("sort")              sort: String?,
        @Query("searchQuery")       searchQuery: String?,
        @Query("status")            status: ReservableStatus?,
        @Query("buildingId")        buildingId: Long?,
        @Query("studentsAvailable") studentsAvailable: Boolean?,
        @Query("spaceTypeIdFilter") spaceTypeIdFilter: Long?,
        @Query("requestStart")      requestStart: LocalDateTime?,
        @Query("requestEnd")        requestEnd: LocalDateTime?,
        @Query("capacity")          capacity: Int?,
        @Query("showMode")          showMode: ShowMode?
    ): Response<PageSpaceDto>

    @GET("spaces/{id}")
    suspend fun getSpace(
        @Path("id") id: Long
    ): Response<SpaceDto>

    @GET("space-types")
    suspend fun getAllSpaceTypes(
        @Query("q")        q: String? = null,
        @Query("showMode") showMode: String? = null
    ): Response<List<SpaceTypeDto>>

    @GET("space-types/{id}")
    suspend fun getSpaceType(
        @Path("id") id: Long
    ): Response<SpaceTypeDto>

    @GET("spaces/assets")
    suspend fun searchSpaceAssets(
        @Query("page")            page: Int = 0,
        @Query("size")            size: Int = 20,
        @Query("sort")            sort: String? = null,
        @Query("searchQuery")     searchQuery: String? = null,
        @Query("buildingId")      buildingId: Long? = null,
        @Query("spaceId")         spaceId: Long? = null,
        @Query("equipmentTypeId") equipmentTypeId: Long? = null,
        @Query("showMode")        showMode: ShowMode? = null
    ): Response<PageSpaceAssetDto>

    @GET("spaces/assets/{id}")
    suspend fun getSpaceAsset(
        @Path("id") id: Long
    ): Response<SpaceAssetDto>
}