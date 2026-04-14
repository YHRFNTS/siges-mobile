package dev.spiffocode.sigesmobile.ui.components.passwordRecovery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.ui.helpers.labelWithAsterisk
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme


@Composable
public fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    readOnly: Boolean = false,
    isError: Boolean = false,
    required: Boolean = false
) {
    Column(modifier = Modifier
        .widthIn(max = 480.dp)
        .fillMaxWidth()
        .padding(bottom = 16.dp)
    ) {
        OutlinedTextField(
            label = {
                Text(
                    text = if (required) labelWithAsterisk(label).text else label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            readOnly = readOnly,
            enabled = !readOnly,
            isError = isError,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            )
        )
    }
}

@Preview
@Composable
fun ProfileFieldPreview() {
    SigesmobileTheme {
        ProfileField(label = "NOMBRE", value = "Ana Martínez López")
    }
}