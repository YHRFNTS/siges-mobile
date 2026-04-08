package dev.spiffocode.sigesmobile.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationPreferenceResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationType
import dev.spiffocode.sigesmobile.ui.components.LabeledSwitch
import dev.spiffocode.sigesmobile.ui.screens.profile.toDisplayInfo
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme


@Composable
public fun NotificationPreferenceItem(
    preference: NotificationPreferenceResponse,
    onToggleInApp: (Boolean) -> Unit = {},
    onToggleEmail: (Boolean) -> Unit = {}
) {
    val (title, description) = preference.type.toDisplayInfo()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            // Spacer removed for extra compactness
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.width(16.dp))


        LabeledSwitch(
            text = "Email",
            checked = preference.emailEnabled,
            onChange = onToggleEmail
        )

        Spacer(modifier = Modifier.width(16.dp))

        LabeledSwitch(
            text = "App",
            checked = preference.inAppEnabled,
            onChange = onToggleInApp
        )
    }
}

@Composable
@Preview
fun NotificationPreferenceItemPreview(){
    SigesmobileTheme {
        NotificationPreferenceItem(
            preference = NotificationPreferenceResponse(
                type = NotificationType.COMMENT_ON_RESERVATION,
                inAppEnabled = true,
                emailEnabled = false
            )
        )
    }
}