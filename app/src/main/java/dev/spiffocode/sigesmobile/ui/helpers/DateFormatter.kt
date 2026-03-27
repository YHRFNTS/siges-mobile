package dev.spiffocode.sigesmobile.ui.helpers

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.number


fun LocalDateTime.toHumanString(): String{
    val format = LocalDateTime.Format {
        year(); char('-'); monthNumber(); char('-');
        this@Format.day(padding = Padding.ZERO)
        char(' '); hour(); char(':'); minute(); char(':'); second()
    }
    return format.format(this)
}

fun LocalDateTime.toCardDateString(end: LocalDateTime): String {
    val monthAbbr = mapOf(
        1 to "Ene", 2 to "Feb", 3 to "Mar", 4 to "Abr",
        5 to "May", 6 to "Jun", 7 to "Jul", 8 to "Ago",
        9 to "Sep", 10 to "Oct", 11 to "Nov", 12 to "Dic"
    )
    val month = monthAbbr[month.number] ?: ""
    val startTime = "%02d:%02d".format(hour, minute)
    val endTime = "%02d:%02d".format(end.hour, end.minute)
    return "$day $month · $startTime – $endTime"
}