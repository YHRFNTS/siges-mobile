package dev.spiffocode.sigesmobile.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun FilterSelector(
    value: String,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    onExpandedChange: (Boolean) -> Unit = { },
    content: @Composable ColumnScope.() -> Unit
){

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange(!expanded)},
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            shape = shape,
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun FilterSelectorPreview(){
    SigesmobileTheme {
        FilterSelector(
            "Todos"
        ) {
            DropdownMenuItem(
                text = { Text("Todos") },
                onClick = {}
            )
            DropdownMenuItem(
                text = { Text("Cables") },
                onClick = {}
            )
        }
    }
}