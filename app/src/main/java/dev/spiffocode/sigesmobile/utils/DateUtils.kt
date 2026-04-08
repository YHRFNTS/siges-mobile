package dev.spiffocode.sigesmobile.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

object DateUtils {
    /**
     * Converts milliseconds from DatePicker (which are in UTC) to LocalDate
     * correctly ensuring no timezone offset shifts the date.
     */
    fun millisToLocalDate(millis: Long): LocalDate {
        return Instant.ofEpochMilli(millis)
            .atOffset(ZoneOffset.UTC)
            .toLocalDate()
    }

    /**
     * Converts LocalDate to UTC milliseconds for DatePickerState.
     */
    fun localDateToMillis(date: LocalDate): Long {
        return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
}
