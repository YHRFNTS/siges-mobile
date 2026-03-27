package dev.spiffocode.sigesmobile.data.remote.api

import dev.spiffocode.sigesmobile.data.remote.dto.PageReservableDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

interface ReservableApiService {
    @GET("reservables")
    suspend fun searchReservables(
        @Query("page")              page: Int = 0,
        @Query("size")              size: Int = 20,
        @Query("sort")              sort: String?,
        @Query("searchQuery")       searchQuery: String?,
        @Query("status")            status: ReservableStatus?,
        @Query("buildingId")        buildingId: Long?,
        @Query("studentsAvailable") studentsAvailable: Boolean?,
        @Query("requestStart")      requestStart: LocalDateTime?,
        @Query("requestEnd")        requestEnd: LocalDateTime?,
        @Query("showMode")          showMode: ShowMode?
    ): Response<PageReservableDto>

    @GET("reservables/{id}")
    suspend fun getReservable(
        @Path("id") id: Long
    ): Response<ReservableDto>
}