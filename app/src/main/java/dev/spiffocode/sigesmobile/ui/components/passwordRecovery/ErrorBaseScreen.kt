package dev.spiffocode.sigesmobile.ui.components.passwordRecovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import dev.spiffocode.sigesmobile.ui.components.PrimaryButton
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
fun ErrorBaseScreen(
    isCompact: Boolean = true,
    icon: ImageVector,
    iconBgColor: Color = MaterialTheme.colorScheme.errorContainer,
    iconTintColor: Color = MaterialTheme.colorScheme.error,
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isCompact) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        if (isCompact) {
             ErrorContent(
                icon = icon, iconBgColor = iconBgColor, iconTintColor = iconTintColor,
                title = title, description = description, buttonText = buttonText,
                onButtonClick = onButtonClick,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp)
            )
        } else {
            Card(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                ErrorContent(
                    icon = icon, iconBgColor = iconBgColor, iconTintColor = iconTintColor,
                    title = title, description = description, buttonText = buttonText,
                    onButtonClick = onButtonClick,
                    modifier = Modifier.padding(40.dp)
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            text = buttonText,
            onClick = onButtonClick,
            isLoading = false
        )
    }
}

@Preview
@Composable
fun ErrorBaseScreenPreview(){
    SigesmobileTheme {
        ErrorBaseScreen(
            icon = Icons.Outlined.Close,
            title = "Enlace caducado",
            description = "Por motivos de seguridad, los enlaces de recuperación expiran después de 15 minutos o al solicitar uno nuevo.",
            buttonText = "Solicitar nuevo enlace"
        )
    }
}