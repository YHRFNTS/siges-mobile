package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.ui.components.ButtonWithLeadingIcon
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
fun SectionHeader(
    verticalPadding: Dp = 8.dp,
    horizontalPadding: Dp = 24.dp,
    title: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
        ButtonWithLeadingIcon(
            icon = Icons.AutoMirrored.Filled.ArrowForwardIos,
            text = actionText,
            onClick = onActionClick
        )

    }
}

@Composable
@Preview(showBackground = true)
fun SectionHeaderPreview(){
    SigesmobileTheme {
        SectionHeader(
            title = "Mis Solicitudes",
            actionText = "Ver todas"
        ) { }
    }
}