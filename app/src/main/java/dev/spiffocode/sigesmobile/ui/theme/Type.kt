package dev.spiffocode.sigesmobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.spiffocode.sigesmobile.R

val dmSerifDisplay = FontFamily(
    Font(R.font.dm_serif_display, FontWeight.Normal
    )
)

// Set of Material typography styles to start with
val Typography = Typography(
    // "SIGES" en login
    displayMedium = TextStyle(
        fontFamily = dmSerifDisplay,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        letterSpacing = 4.sp
    ),
    // Nombre del usuario en Home/Perfil
    headlineLarge = TextStyle(
        fontFamily = dmSerifDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp
    ),
    // Títulos de pantalla (con back button)
    headlineMedium = TextStyle(
        fontFamily = dmSerifDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    // Títulos de pantalla (con back button)
    headlineSmall = TextStyle(
        fontFamily = dmSerifDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    // Nombre del recurso en cards
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    // Subtítulos, descripción, fecha+hora
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    // Metadata de íconos en cards
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    // Labels de sección en formularios/detalle (uppercase)
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 1.sp
    ),
    // Chips de estado y BottomNav labels
    labelSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp
    )
)