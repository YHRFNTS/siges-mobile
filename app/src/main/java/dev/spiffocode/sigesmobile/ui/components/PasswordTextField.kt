package dev.spiffocode.sigesmobile.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
fun PasswordTextField(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    placeholder: String = "*********",
    leadingIcon: ImageVector = Icons.Default.Lock,
    isVisible: Boolean,
    onVisibilityToggle: () -> Unit = {},
    isError: Boolean = false
) {
    val plum = Color(0xFF6B5B95)
    val borderGray = Color(0xFFE5E7EB)
    val textSecondary = Color(0xFF6B7280)
    val textPrimary = Color(0xFF2D3142)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = {
            IconButton(onClick = onVisibilityToggle) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Alternar visibilidad",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = MaterialTheme.shapes.extraSmall,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        singleLine = true
    )
}

@Preview
@Composable
fun PasswordTextFieldPreview() {
    SigesmobileTheme {
        PasswordTextField(
            value = "hello",
            isVisible = true
        )
    }
}


@Preview
@Composable
fun PasswordTextFieldPlaceholderPreview() {
    SigesmobileTheme {
        PasswordTextField(
            value = "",
            isVisible = true
        )
    }
}


@Preview
@Composable
fun PasswordTextFieldNonVisiblePreview() {
    SigesmobileTheme {
        PasswordTextField(
            value = "hello",
            isVisible = false
        )
    }
}