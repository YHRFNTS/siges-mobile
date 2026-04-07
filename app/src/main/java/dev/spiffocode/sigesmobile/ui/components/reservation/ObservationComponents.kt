package dev.spiffocode.sigesmobile.ui.components.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.spiffocode.sigesmobile.data.remote.dto.NoteItem
import dev.spiffocode.sigesmobile.ui.components.detail.SectionTitle
import dev.spiffocode.sigesmobile.ui.helpers.toHumanString
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime

@Composable
fun ObservationChat(
    notes: List<NoteItem>,
    currentUserId: Long?,
    onAddNote: (String) -> Unit,
    onEditNote: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newNoteText by remember { mutableStateOf("") }
    var editingNoteId by remember { mutableStateOf<Long?>(null) }
    var editingNoteText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle("OBSERVACIONES / CHAT")

        if (notes.isEmpty()) {
            Text(
                text = "No hay observaciones aún.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            notes.forEach { note ->
                val isMine = note.createdBy?.id == currentUserId
                ObservationBubble(
                    note = note,
                    isMine = isMine,
                    onEditClick = {
                        editingNoteId = note.id
                        editingNoteText = note.comment
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = if (editingNoteId != null) editingNoteText else newNoteText,
                onValueChange = { if (editingNoteId != null) editingNoteText = it else newNoteText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(if (editingNoteId != null) "Editar observación..." else "Escribir observación...") },
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                trailingIcon = {
                    if (editingNoteId != null) {
                        IconButton(onClick = {
                            editingNoteId = null
                            editingNoteText = ""
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Cancelar edición", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = {
                    if (editingNoteId != null) {
                        if (editingNoteText.isNotBlank()) {
                            onEditNote(editingNoteId!!, editingNoteText)
                            editingNoteId = null
                            editingNoteText = ""
                        }
                    } else {
                        if (newNoteText.isNotBlank()) {
                            onAddNote(newNoteText)
                            newNoteText = ""
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(48.dp),
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ObservationBubble(
    note: NoteItem,
    isMine: Boolean,
    onEditClick: () -> Unit
) {
    val alignment = if (isMine) Alignment.End else Alignment.Start
    val containerColor = if (isMine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val contentColor = if (isMine) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val shape = if (isMine) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (!isMine) {
            Text(
                text = "${note.createdBy?.firstName} ${note.createdBy?.lastName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }
        
        Box(
            modifier = Modifier
                .clip(shape)
                .background(containerColor)
                .then(if (isMine) Modifier.clickable { onEditClick() } else Modifier)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = note.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = note.createdAt?.toKotlinLocalDateTime()?.toHumanString() ?: "",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = contentColor.copy(alpha = 0.7f)
                    )
                    if (isMine) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier.size(12.dp),
                            tint = contentColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
