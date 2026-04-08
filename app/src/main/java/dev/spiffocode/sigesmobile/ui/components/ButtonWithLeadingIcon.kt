package dev.spiffocode.sigesmobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
fun ButtonWithLeadingIcon(
    icon: ImageVector,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    iconSize: Dp = 14.dp,
    iconColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = Modifier
            .background(backgroundColor),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        onClick = onClick) {
        Text(text, style = textStyle, color = textColor)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview
@Composable
fun ButtonWithLeadingIconPreview() {
    SigesmobileTheme {
        ButtonWithLeadingIcon(
            icon = Icons.AutoMirrored.Outlined.ArrowForwardIos,
            text = "Send"
        )
    }
}