package dev.spiffocode.sigesmobile.ui.helpers

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

/**
 * Creates an [AnnotatedString] that adds a red asterisk at the end of the [label].
 */
@Composable
fun labelWithAsterisk(label: String): AnnotatedString {
    return buildAnnotatedString {
        append(label.removeSuffix("*").trim())
        append(" ")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
            append("*")
        }
    }
}
