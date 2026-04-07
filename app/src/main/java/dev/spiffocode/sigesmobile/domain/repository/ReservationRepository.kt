package dev.spiffocode.sigesmobile.domain.repository

import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.api.ReservationApiService
import dev.spiffocode.sigesmobile.data.remote.dto.ApproveReservationRequest
import dev.spiffocode.sigesmobile.data.remote.dto.CancelReservationRequest
import dev.spiffocode.sigesmobile.data.remote.dto.CreateReservationRequest
import dev.spiffocode.sigesmobile.data.remote.dto.DayAvailabilityItem
import dev.spiffocode.sigesmobile.data.remote.dto.EditNoteRequest
import dev.spiffocode.sigesmobile.data.remote.dto.NoteItem
import dev.spiffocode.sigesmobile.data.remote.dto.PageReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.PublishNoteRequest
import dev.spiffocode.sigesmobile.data.remote.dto.RejectReservationRequest
import dev.spiffocode.sigesmobile.data.remote.dto.RescheduleReservationRequest
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationType
import dev.spiffocode.sigesmobile.data.remote.safeApiCall
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepository @Inject constructor(
    private val api: ReservationApiService
) {

    suspend fun getReservations(
        page: Int = 0,
        size: Int = 20,
        sort: String? = null,
        petitionerId: Long? = null,
        petitionerName: String? = null,
        date: LocalDate? = null,
        dateFrom: LocalDate? = null,
        dateTo: LocalDate? = null,
        statuses: List<ReservationStatus>? = null,
        reservableId: Long? = null,
        type: ReservationType? = null
    ): NetworkResult<PageReservationResponse> = safeApiCall {
        api.getReservations(
            page           = page,
            size           = size,
            sort           = sort,
            petitionerId   = petitionerId,
            petitionerName = petitionerName,
            date           = date,
            dateFrom       = dateFrom,
            dateTo         = dateTo,
            statuses       = statuses,
            reservableId   = reservableId,
            type           = type
        )
    }

    suspend fun getReservation(id: Long): NetworkResult<ReservationResponse> =
        safeApiCall { api.getReservation(id) }

    suspend fun createReservation(
        reservableId: Long,
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime,
        type: ReservationType,
        companions: Int? = null,
        reason: String?
    ): NetworkResult<ReservationResponse> = safeApiCall {
        api.createReservation(
            CreateReservationRequest(reservableId, date, startTime, endTime, type, companions, reason)
        )
    }

    suspend fun approveReservation(id: Long, observation: String?): NetworkResult<ReservationResponse> =
        safeApiCall { api.approveReservation(id, ApproveReservationRequest(observation)) }

    suspend fun rejectReservation(id: Long, reason: String): NetworkResult<ReservationResponse> =
        safeApiCall { api.rejectReservation(id, RejectReservationRequest(reason)) }

    suspend fun startReservation(id: Long): NetworkResult<ReservationResponse> =
        safeApiCall { api.startReservation(id) }

    suspend fun finishReservation(id: Long): NetworkResult<ReservationResponse> =
        safeApiCall { api.finishReservation(id) }

    suspend fun rescheduleReservation(
        id: Long,
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime
    ): NetworkResult<ReservationResponse> = safeApiCall {
        api.rescheduleReservation(id, RescheduleReservationRequest(date, startTime, endTime))
    }

    suspend fun cancelReservation(id: Long, reason: String): NetworkResult<ReservationResponse> =
        safeApiCall { api.cancelReservation(id, CancelReservationRequest(reason)) }

    suspend fun addNote(reservationId: Long, comment: String): NetworkResult<ReservationResponse> =
        safeApiCall { api.addNote(reservationId, PublishNoteRequest(reservationId, comment)) }

    suspend fun editNote(reservationId: Long, noteId: Long, comment: String): NetworkResult<NoteItem> =
        safeApiCall { api.editNote(reservationId, noteId, EditNoteRequest(comment)) }

    suspend fun getCalendar(
        reservableId: Long,
        from: LocalDate? = null,
        to: LocalDate? = null
    ): NetworkResult<List<DayAvailabilityItem>> =
        safeApiCall { api.getCalendar(reservableId, from, to) }
}