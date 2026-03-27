package dev.spiffocode.sigesmobile.ui.helpers

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char


fun LocalDateTime.toHumanString(): String{
    val format = LocalDateTime.Format {
        year(); char('-'); monthNumber(); char('-');
        this@Format.day(padding = Padding.ZERO)
        char(' '); hour(); char(':'); minute(); char(':'); second()
    }
    return format.format(this)
}