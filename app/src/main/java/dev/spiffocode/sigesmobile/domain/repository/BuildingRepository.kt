package dev.spiffocode.sigesmobile.domain.repository

import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.api.BuildingApiService
import dev.spiffocode.sigesmobile.data.remote.dto.BuildingDto
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import dev.spiffocode.sigesmobile.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuildingRepository @Inject constructor(
    private val api: BuildingApiService
) {
    suspend fun getAllBuildings(showMode: ShowMode? = null): NetworkResult<List<BuildingDto>> =
        safeApiCall { api.getAllBuildings(showMode) }

    suspend fun getBuilding(id: Long): NetworkResult<BuildingDto> =
        safeApiCall { api.getBuilding(id) }

    suspend fun createBuilding(request: dev.spiffocode.sigesmobile.data.remote.dto.BuildingRegisterDto): NetworkResult<BuildingDto> =
        safeApiCall { api.createBuilding(request) }

    suspend fun updateBuilding(id: Long, request: dev.spiffocode.sigesmobile.data.remote.dto.BuildingRegisterDto): NetworkResult<BuildingDto> =
        safeApiCall { api.updateBuilding(id, request) }

    suspend fun deactivateBuilding(id: Long): NetworkResult<BuildingDto> =
        safeApiCall { api.deactivateBuilding(id) }
}