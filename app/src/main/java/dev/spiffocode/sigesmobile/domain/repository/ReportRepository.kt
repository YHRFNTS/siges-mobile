package dev.spiffocode.sigesmobile.domain.repository

import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.api.ReportApiService
import dev.spiffocode.sigesmobile.data.remote.dto.DashboardStatsDto
import dev.spiffocode.sigesmobile.data.remote.dto.ResourceStatsDto
import dev.spiffocode.sigesmobile.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val api: ReportApiService
) {
    suspend fun getDashboardStats(): NetworkResult<DashboardStatsDto> =
        safeApiCall { api.getDashboardStats() }

    suspend fun getAllResourceStats(): NetworkResult<List<ResourceStatsDto>> =
        safeApiCall { api.getAllResourceStats() }

    suspend fun getResourceStats(id: Long): NetworkResult<ResourceStatsDto> =
        safeApiCall { api.getResourceStats(id) }
}