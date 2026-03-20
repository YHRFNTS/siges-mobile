package dev.spiffocode.sigesmobile.data.remote.api

import dev.spiffocode.sigesmobile.data.remote.dto.BuildingDto
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BuildingApiService {

    @GET("buildings")
    suspend fun getAllBuildings(
        @Query("showMode") showMode: ShowMode?
    ): Response<List<BuildingDto>>

    @GET("buildings/{id}")
    suspend fun getBuilding(
        @Path("id") id: Long
    ): Response<BuildingDto>
}