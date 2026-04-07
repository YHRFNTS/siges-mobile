package dev.spiffocode.sigesmobile.ui.components.newrequest

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.data.remote.dto.BuildingDto
import dev.spiffocode.sigesmobile.data.remote.dto.EquipmentDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.SpaceDto
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceSelectionSection(
    searchQuery: String,
    modifier: Modifier = Modifier,
    onSearchQueryChange: (String) -> Unit = {},
    onFocusGained: () -> Unit = {},
    searchResults: List<Any>,
    isSearching: Boolean,
    selectedSpace: SpaceDto?,
    selectedEquipment: EquipmentDto?,
    onSpaceSelected: (SpaceDto) -> Unit = {},
    onEquipmentSelected: (EquipmentDto) -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(
            text = "SELECCIONA EL RECURSO *",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Show selected resource card if a resource is selected
        if (selectedSpace != null || selectedEquipment != null) {
            val title = selectedSpace?.name ?: selectedEquipment?.name ?: ""
            val subtitle = selectedSpace?.building?.name ?: selectedEquipment?.building?.name ?: ""

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Allow resetting the selection by clearing searchQuery implicitly externally 
                        // Or we can provide an onClear selection, but for now they can just search again
                    },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Seleccionado",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = title, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        if (subtitle.isNotBlank()) {
                            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Field
        var expanded by remember { mutableStateOf(false) }
        
        // Show dropdown when results arrive
        LaunchedEffect(searchResults) {
            if (searchResults.isNotEmpty()) {
                expanded = true
            }
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    onSearchQueryChange(it)
                    expanded = true
                },
                placeholder = { Text("Buscar recurso...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .onFocusChanged { focus ->
                        if (focus.isFocused) {
                            expanded = true
                            onFocusGained()
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true
            )

            if (searchResults.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    searchResults.forEach { result ->
                        when (result) {
                            is SpaceDto -> {
                                DropdownMenuItem(
                                    text = { 
                                        Column {
                                            Text(result.name, fontWeight = FontWeight.Bold)
                                            Text(result.building?.name ?: "", style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        onSpaceSelected(result)
                                        expanded = false
                                    }
                                )
                            }
                            is EquipmentDto -> {
                                DropdownMenuItem(
                                    text = { 
                                        Column {
                                            Text(result.name, fontWeight = FontWeight.Bold)
                                            Text(result.building?.name ?: "", style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        onEquipmentSelected(result)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ResourceSelectionSectionPreview(){
    SigesmobileTheme {
        ResourceSelectionSection(
            searchQuery = "Aula 1",
            searchResults = listOf(
                EquipmentDto(
                    id = 1L,
                    name = "Aula 1",
                    building = BuildingDto(
                        id = 1L,
                        name = "Edificio A"
                    ),
                    status = ReservableStatus.AVAILABLE,
                    availableForStudents = true
                )
            ),
            isSearching = false,
            selectedSpace = null,
            selectedEquipment = null
        )
    }
}
