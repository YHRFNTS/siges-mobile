package dev.spiffocode.sigesmobile.domain.repository

import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.api.ReservableApiService
import dev.spiffocode.sigesmobile.data.remote.dto.PageReservableDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ShowMode
import dev.spiffocode.sigesmobile.data.remote.safeApiCall
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservableRepository @Inject constructor(
    private val api: ReservableApiService

){

    suspend fun searchSpaces(
        page: Int = 0,
        size: Int = 20,
        sort: String? = null,
        searchQuery: String? = null,
        status: ReservableStatus? = null,
        buildingId: Long? = null,
        studentsAvailable: Boolean? = null,
        requestStart: LocalDateTime? = null,
        requestEnd: LocalDateTime? = null,
        showMode: ShowMode? = null
    ): NetworkResult<PageReservableDto> = safeApiCall {
        api.searchReservables(
            page              = page,
            size              = size,
            sort              = sort,
            searchQuery       = searchQuery,
            status            = status,
            buildingId        = buildingId,
            studentsAvailable = studentsAvailable,
            requestStart      = requestStart,
            requestEnd        = requestEnd,
            showMode          = showMode
        )
    }

    suspend fun getSpace(id: Long): NetworkResult<ReservableDto> =
        safeApiCall { api.getReservable(id) }

}