package dev.spiffocode.sigesmobile.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme


@Composable
public fun ProfileHeader(
    fullName: String,
    initials: String,
    roleLabel: String,
    identifier: String,
    profilePictureUrl: String? = null
) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!profilePictureUrl.isNullOrBlank()) {
            AsyncImage(
                model = profilePictureUrl,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier         = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.onPrimary,
                        MaterialTheme.colorScheme.onSecondary))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = initials.ifBlank { "?" },
                    fontSize   = 32.sp,
                    color      = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text       = fullName.ifBlank { "—" },
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text     = if (identifier.isNotBlank()) "$roleLabel · $identifier" else roleLabel,
            fontSize = 13.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun ProfileHeaderPreview(){
    SigesmobileTheme {
        ProfileHeader(
            fullName = "Ana Martínez López",
            initials = "AM",
            roleLabel = "Estudiante",
            identifier = "20243ds158@utez.edu.mx"
        )
    }
}