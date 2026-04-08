package dev.spiffocode.sigesmobile.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateUtilsTest {

    @Test
    fun `millisToLocalDate converts UTC millis correctly to LocalDate`() {
        // 2024-04-09 00:00:00 UTC is 1712620800000 ms
        val millis: Long = 1712620800000L
        val expected = LocalDate.of(2024, 4, 9)
        
        val actual = DateUtils.millisToLocalDate(millis)
        
        assertEquals(expected, actual)
    }

    @Test
    fun `localDateToMillis converts LocalDate correctly to UTC millis`() {
        val date = LocalDate.of(2024, 4, 9)
        val expected: Long = 1712620800000L
        
        val actual = DateUtils.localDateToMillis(date)
        
        assertEquals(expected, actual)
    }

    @Test
    fun `conversion is consistent both ways`() {
        val originalDate = LocalDate.of(2025, 12, 25)
        val millis = DateUtils.localDateToMillis(originalDate)
        val convertedDate = DateUtils.millisToLocalDate(millis)
        
        assertEquals(originalDate, convertedDate)
    }
}
