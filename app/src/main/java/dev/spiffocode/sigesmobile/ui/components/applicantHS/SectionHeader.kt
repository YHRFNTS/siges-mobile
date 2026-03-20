package dev.spiffocode.sigesmobile.ui.components.applicantHS

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionHeader(title: String, actionText: String, onActionClick: () -> Unit) {
    val textPrimary = Color(0xFF2D3142)
    val plum = Color(0xFF6B5B95)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
        Text(
            actionText,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = plum,
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}