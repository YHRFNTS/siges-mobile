package dev.spiffocode.sigesmobile.data.remote.api

import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.space.PageSpaceAssetDto
import dev.spiffocode.sigesmobile.data.remote.dto.space.PageSpaceDto
import dev.spiffocode.sigesmobile.data.remote.dto.space.SpaceAssetDto
import dev.spiffocode.sigesmobile.data.remote.dto.space.SpaceAssetRegisterDto
import dev.spiffocode.sigesmobile.data.remote.dto.space.SpaceAssetUpdateDto
import dev.spiffocode.sigesmobile.data.remote.dto.space.SpaceDto
import dev.spiffocode.sigesmobile.data.remote.dto.space.SpaceRegisterDto
import dev.spiffocode.sigesmobile.data.remote.dto.space.SpaceTypeDto
import dev.spiffocode.sigesmobile.data.remote.dto.space.SpaceTypeRegisterDto
import dev.spiffocode.sigesmobile.data.remote.dto.space.SpaceTypeUpdateDto
import dev.spiffocode.sigesmobile.data.remote.dto.space.SpaceUpdateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
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
        @Query("showMode")          showMode: String?        // ACTIVE | INACTIVE | ALL
    ): Response<PageSpaceDto>

    @GET("spaces/{id}")
    suspend fun getSpace(
        @Path("id") id: Long
    ): Response<SpaceDto>

    @POST("spaces")
    suspend fun registerSpace(
        @Body request: SpaceRegisterDto
    ): Response<SpaceDto>

    @PUT("spaces/{id}")
    suspend fun updateSpace(
        @Path("id") id: Long,
        @Body request: SpaceUpdateDto
    ): Response<SpaceDto>

    @PATCH("spaces/{id}/activate")
    suspend fun activateSpace(
        @Path("id") id: Long
    ): Response<Unit>

    @PATCH("spaces/{id}/deactivate")
    suspend fun deactivateSpace(
        @Path("id") id: Long
    ): Response<Unit>

    // ── Space Types ───────────────────────────────────────────────────────────

    @GET("space-types")
    suspend fun getAllSpaceTypes(
        @Query("q")        q: String? = null,
        @Query("showMode") showMode: String? = null
    ): Response<List<SpaceTypeDto>>

    @GET("space-types/{id}")
    suspend fun getSpaceType(
        @Path("id") id: Long
    ): Response<SpaceTypeDto>

    @POST("space-types")
    suspend fun registerSpaceType(
        @Body request: SpaceTypeRegisterDto
    ): Response<SpaceTypeDto>

    @PUT("space-types/{id}")
    suspend fun updateSpaceType(
        @Path("id") id: Long,
        @Body request: SpaceTypeUpdateDto
    ): Response<SpaceTypeDto>

    @PATCH("space-types/{id}/activate")
    suspend fun activateSpaceType(
        @Path("id") id: Long
    ): Response<Unit>

    @PATCH("space-types/{id}/deactivate")
    suspend fun deactivateSpaceType(
        @Path("id") id: Long
    ): Response<Unit>

    // ── Space Assets ──────────────────────────────────────────────────────────

    @GET("spaces/assets")
    suspend fun searchSpaceAssets(
        @Query("page")            page: Int = 0,
        @Query("size")            size: Int = 20,
        @Query("sort")            sort: String? = null,
        @Query("searchQuery")     searchQuery: String? = null,
        @Query("buildingId")      buildingId: Long? = null,
        @Query("spaceId")         spaceId: Long? = null,
        @Query("equipmentTypeId") equipmentTypeId: Long? = null,
        @Query("showMode")        showMode: String? = null
    ): Response<PageSpaceAssetDto>

    @GET("spaces/assets/{id}")
    suspend fun getSpaceAsset(
        @Path("id") id: Long
    ): Response<SpaceAssetDto>

    @POST("spaces/{spaceId}/assets")
    suspend fun registerSpaceAsset(
        @Path("spaceId") spaceId: Long,
        @Body request: SpaceAssetRegisterDto
    ): Response<SpaceAssetDto>

    @PUT("spaces/assets/{id}")
    suspend fun updateSpaceAsset(
        @Path("id") id: Long,
        @Body request: SpaceAssetUpdateDto
    ): Response<SpaceAssetDto>

    @PATCH("spaces/assets/{id}/activate")
    suspend fun activateSpaceAsset(
        @Path("id") id: Long
    ): Response<Unit>

    @PATCH("spaces/assets/{id}/deactivate")
    suspend fun deactivateSpaceAsset(
        @Path("id") id: Long
    ): Response<Unit>
}