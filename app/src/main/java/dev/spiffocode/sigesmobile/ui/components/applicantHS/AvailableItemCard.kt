package dev.spiffocode.sigesmobile.ui.components.applicantHS

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AvailableItemCard(
    title: String,
    meta: String,
    status: String,
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    val textPrimary = Color(0xFF2D3142)
    val textSecondary = Color(0xFF6B7280)
    val teal = Color(0xFF4A9B8E)
    val plum = Color(0xFF6B5B95)
    val lav = Color(0xFFE8DFF5)
    val sky = Color(0xFFD9E8F5)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(colors = listOf(lav, sky))),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = plum, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                Text(meta, fontSize = 12.sp, color = textSecondary)
                Text(status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = teal)
            }
        }
    }
}