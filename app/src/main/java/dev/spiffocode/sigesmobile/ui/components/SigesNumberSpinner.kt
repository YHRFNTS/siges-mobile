package dev.spiffocode.sigesmobile.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.ui.helpers.labelWithAsterisk

/**
 * A stylized number spinner using a DropdownMenu.
 *
 * @param value The current selected value as a string.
 * @param onValueChange Callback when a new value is selected.
 * @param max The maximum number allowed in the spinner.
 * @param label The label for the field.
 * @param placeholder The placeholder when no value is selected.
 * @param leadingIcon Optional leading icon.
 * @param enabled Whether the component is enabled.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigesNumberSpinner(
    value: String,
    onValueChange: (String) -> Unit,
    max: Int,
    label: String,
    placeholder: String = "Selecciona...",
    leadingIcon: ImageVector? = Icons.Outlined.Person,
    enabled: Boolean = true,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value         = if (value.isEmpty()) "" else value,
            onValueChange = {},
            readOnly      = true,
            isError       = isError,
            label         = { Text(labelWithAsterisk(label)) },
            placeholder   = { Text(placeholder) },
            leadingIcon   = leadingIcon?.let { { Icon(it, contentDescription = null) } },
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors        = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                disabledBorderColor  = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                disabledTextColor    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                disabledLabelColor   = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                errorBorderColor     = MaterialTheme.colorScheme.error,
                errorLabelColor      = MaterialTheme.colorScheme.error
            ),
            shape         = RoundedCornerShape(12.dp),
            modifier      = Modifier.menuAnchor().fillMaxWidth(),
            enabled       = enabled
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (i in 1..max) {
                DropdownMenuItem(
                    text = { Text(i.toString()) },
                    onClick = {
                        onValueChange(i.toString())
                        expanded = false
                    }
                )
            }
        }
    }
}
