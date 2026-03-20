package dev.spiffocode.sigesmobile.domain.repository

import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.api.SpaceApiService
import dev.spiffocode.sigesmobile.data.remote.dto.PageSpaceAssetDto
import dev.spiffocode.sigesmobile.data.remote.dto.PageSpaceDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceAssetDto
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceTypeDto
import dev.spiffocode.sigesmobile.data.remote.safeApiCall
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpaceRepository @Inject constructor(
    private val api: SpaceApiService
) {

    suspend fun searchSpaces(
        page: Int = 0,
        size: Int = 20,
        sort: String? = null,
        searchQuery: String? = null,
        status: ReservableStatus? = null,
        buildingId: Long? = null,
        studentsAvailable: Boolean? = null,
        spaceTypeIdFilter: Long? = null,
        requestStart: LocalDateTime? = null,
        requestEnd: LocalDateTime? = null,
        capacity: Int? = null,
        showMode: ShowMode? = null
    ): NetworkResult<PageSpaceDto> = safeApiCall {
        api.searchSpaces(
            page              = page,
            size              = size,
            sort              = sort,
            searchQuery       = searchQuery,
            status            = status,
            buildingId        = buildingId,
            studentsAvailable = studentsAvailable,
            spaceTypeIdFilter = spaceTypeIdFilter,
            requestStart      = requestStart,
            requestEnd        = requestEnd,
            capacity          = capacity,
            showMode          = showMode
        )
    }

    suspend fun getSpace(id: Long): NetworkResult<SpaceDto> =
        safeApiCall { api.getSpace(id) }

    suspend fun getAllSpaceTypes(
        q: String? = null,
        showMode: String? = null
    ): NetworkResult<List<SpaceTypeDto>> =
        safeApiCall { api.getAllSpaceTypes(q, showMode) }

    suspend fun getSpaceType(id: Long): NetworkResult<SpaceTypeDto> =
        safeApiCall { api.getSpaceType(id) }

    suspend fun searchSpaceAssets(
        page: Int = 0,
        size: Int = 20,
        sort: String? = null,
        searchQuery: String? = null,
        buildingId: Long? = null,
        spaceId: Long? = null,
        equipmentTypeId: Long? = null,
        showMode: ShowMode? = null
    ): NetworkResult<PageSpaceAssetDto> = safeApiCall {
        api.searchSpaceAssets(
            page            = page,
            size            = size,
            sort            = sort,
            searchQuery     = searchQuery,
            buildingId      = buildingId,
            spaceId         = spaceId,
            equipmentTypeId = equipmentTypeId,
            showMode        = showMode
        )
    }

    suspend fun getSpaceAsset(id: Long): NetworkResult<SpaceAssetDto> =
        safeApiCall { api.getSpaceAsset(id) }
}