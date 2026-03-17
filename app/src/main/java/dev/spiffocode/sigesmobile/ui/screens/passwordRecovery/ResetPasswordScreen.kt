package dev.spiffocode.sigesmobile.ui.screens.passwordRecovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import dev.spiffocode.sigesmobile.ui.components.login.PasswordTextField
import dev.spiffocode.sigesmobile.ui.components.login.PrimaryButton
import dev.spiffocode.sigesmobile.ui.theme.*

@Composable
fun ResetPasswordScreen(
    emailFromLink: String = "usuario@utez.edu.mx",
    onNavigateToLogin: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Lav)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Lock, contentDescription = null, tint = Plum, modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Crear nueva contraseña",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isSuccess) {
            Text("Usuario a recuperar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = emailFromLink,
                onValueChange = {},
                enabled = false,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = TextSecondary,
                    disabledBorderColor = Color(0xFFE5E7EB),
                    disabledContainerColor = Color(0xFFF3F4F6),
                    disabledLeadingIconColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Nueva Contraseña", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = null
                },
                placeholder = "Mínimo 8 caracteres",
                leadingIcon = Icons.Default.Lock,
                isVisible = isNewPasswordVisible,
                onVisibilityToggle = { isNewPasswordVisible = !isNewPasswordVisible }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Confirmar Contraseña", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                placeholder = "Repite tu nueva contraseña",
                leadingIcon = Icons.Default.Lock,
                isVisible = isConfirmPasswordVisible,
                onVisibilityToggle = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
            )

            errorMessage?.let { error ->
                Text(text = error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Actualizar Contraseña",
                onClick = {
                    if (newPassword.length < 8) {
                        errorMessage = "La contraseña debe tener al menos 8 caracteres."
                    } else if (newPassword != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden."
                    } else {
                        isSuccess = true
                    }
                },
                isLoading = false
            )
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = Mint),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("¡Contraseña actualizada!", fontWeight = FontWeight.Bold, color = Teal, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tu contraseña ha sido cambiada exitosamente. Ya puedes iniciar sesión con tus nuevas credenciales.", color = Teal, fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Ir al Login",
                onClick = onNavigateToLogin,
                isLoading = false
            )
        }
    }
}