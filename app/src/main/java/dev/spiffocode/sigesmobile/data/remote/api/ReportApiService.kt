package dev.spiffocode.sigesmobile.data.remote.api

import dev.spiffocode.sigesmobile.data.remote.dto.DashboardStatsDto
import dev.spiffocode.sigesmobile.data.remote.dto.ResourceStatsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ReportApiService {

    @GET("reports/dashboard")
    suspend fun getDashboardStats(): Response<DashboardStatsDto>

    @GET("reports/resources")
    suspend fun getAllResourceStats(): Response<List<ResourceStatsDto>>

    @GET("reports/resources/{id}")
    suspend fun getResourceStats(
        @Path("id") id: Long
    ): Response<ResourceStatsDto>
}