package dev.spiffocode.sigesmobile.domain.repository

import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.api.EquipmentApiService
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentDto
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentTypeDto
import dev.spiffocode.sigesmobile.data.remote.dto.PageEquipmentDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import dev.spiffocode.sigesmobile.data.remote.safeApiCall
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EquipmentRepository @Inject constructor(
    private val api: EquipmentApiService
) {

    suspend fun searchEquipments(
        page: Int = 0,
        size: Int = 20,
        sort: String? = null,
        searchQuery: String? = null,
        status: ReservableStatus? = null,
        buildingId: Long? = null,
        studentsAvailable: Boolean? = null,
        spaceId: Long? = null,
        equipmentTypeId: Long? = null,
        requestStart: LocalDateTime? = null,
        requestEnd: LocalDateTime? = null,
        showMode: ShowMode? = null
    ): NetworkResult<PageEquipmentDto> = safeApiCall {
        api.searchEquipments(
            page              = page,
            size              = size,
            sort              = sort,
            searchQuery       = searchQuery,
            status            = status,
            buildingId        = buildingId,
            studentsAvailable = studentsAvailable,
            spaceId           = spaceId,
            equipmentTypeId   = equipmentTypeId,
            requestStart      = requestStart,
            requestEnd        = requestEnd,
            showMode          = showMode
        )
    }

    suspend fun getEquipment(id: Long): NetworkResult<EquipmentDto> =
        safeApiCall { api.getEquipment(id) }

    suspend fun getAllEquipmentTypes(
        q: String? = null,
        showMode: ShowMode? = null
    ): NetworkResult<List<EquipmentTypeDto>> =
        safeApiCall { api.getAllEquipmentTypes(q, showMode) }

    suspend fun getEquipmentType(id: Long): NetworkResult<EquipmentTypeDto> =
        safeApiCall { api.getEquipmentType(id) }

    suspend fun createEquipmentType(request: dev.spiffocode.sigesmobile.data.remote.dto.EquipmentTypeRegisterDto): NetworkResult<EquipmentTypeDto> =
        safeApiCall { api.createEquipmentType(request) }

    suspend fun updateEquipmentType(id: Long, request: dev.spiffocode.sigesmobile.data.remote.dto.EquipmentTypeRegisterDto): NetworkResult<EquipmentTypeDto> =
        safeApiCall { api.updateEquipmentType(id, request) }

    suspend fun deactivateEquipmentType(id: Long): NetworkResult<EquipmentTypeDto> =
        safeApiCall { api.deactivateEquipmentType(id) }
}