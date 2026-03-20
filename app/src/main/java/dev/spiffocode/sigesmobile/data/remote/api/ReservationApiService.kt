package dev.spiffocode.sigesmobile.data.remote.api

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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReservationApiService {

    @GET("reservations")
    suspend fun getReservations(
        @Query("page")           page: Int = 0,
        @Query("size")           size: Int = 20,
        @Query("sort")           sort: String? = null,
        @Query("petitionerId")   petitionerId: Long? = null,
        @Query("petitionerName") petitionerName: String? = null,
        @Query("date")           date: String? = null,        // yyyy-MM-dd
        @Query("dateFrom")       dateFrom: String? = null,
        @Query("dateTo")         dateTo: String? = null,
        @Query("status")         status: String? = null,      // ReservationStatus.name
        @Query("reservableId")   reservableId: Long? = null,
        @Query("type")           type: String? = null         // GROUP | SINGLE
    ): Response<PageReservationResponse>

    // ── Single ───────────────────────────────────────────────────────────────

    @GET("reservations/{id}")
    suspend fun getReservation(
        @Path("id") id: Long
    ): Response<ReservationResponse>

    // ── Create ───────────────────────────────────────────────────────────────

    @POST("reservations")
    suspend fun createReservation(
        @Body request: CreateReservationRequest
    ): Response<ReservationResponse>

    // ── State transitions (admin) ─────────────────────────────────────────────

    @PATCH("reservations/{id}/approve")
    suspend fun approveReservation(
        @Path("id") id: Long
    ): Response<ReservationResponse>

    @PATCH("reservations/{id}/reject")
    suspend fun rejectReservation(
        @Path("id") id: Long,
        @Body request: RejectReservationRequest
    ): Response<ReservationResponse>

    @PATCH("reservations/{id}/start")
    suspend fun startReservation(
        @Path("id") id: Long
    ): Response<ReservationResponse>

    @PATCH("reservations/{id}/finish")
    suspend fun finishReservation(
        @Path("id") id: Long
    ): Response<ReservationResponse>

    // ── User actions ─────────────────────────────────────────────────────────

    @PATCH("reservations/{id}")
    suspend fun rescheduleReservation(
        @Path("id") id: Long,
        @Body request: RescheduleReservationRequest
    ): Response<ReservationResponse>

    @PATCH("reservations/{id}/cancel")
    suspend fun cancelReservation(
        @Path("id") id: Long,
        @Body request: CancelReservationRequest
    ): Response<ReservationResponse>

    // ── Notes ─────────────────────────────────────────────────────────────────

    @POST("reservations/{id}/notes")
    suspend fun addNote(
        @Path("id") id: Long,
        @Body request: PublishNoteRequest
    ): Response<ReservationResponse>

    @PATCH("reservations/{id}/notes/{noteId}")
    suspend fun editNote(
        @Path("id")     id: Long,
        @Path("noteId") noteId: Long,
        @Body request: EditNoteRequest
    ): Response<NoteItem>

    // ── Calendar ─────────────────────────────────────────────────────────────

    @GET("reservables/{reservableId}/calendar")
    suspend fun getCalendar(
        @Path("reservableId") reservableId: Long,
        @Query("from")        from: String? = null,  // yyyy-MM-dd
        @Query("to")          to: String? = null
    ): Response<List<DayAvailabilityItem>>
}