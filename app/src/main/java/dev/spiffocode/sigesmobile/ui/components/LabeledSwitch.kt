package dev.spiffocode.sigesmobile.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
public fun LabeledSwitch(
    text: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit = {},
    spacing: Dp = 8.dp
){

    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(spacing))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.background,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.background,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LabeledSwitchOnPreview() {
    SigesmobileTheme {
        LabeledSwitch(
            text = "App",
            checked = true
        )
    }
}


@Composable
@Preview(showBackground = true)
fun LabeledSwitchOffPreview() {
    SigesmobileTheme {
        LabeledSwitch(
            text = "App",
            checked = false
        )
    }
}