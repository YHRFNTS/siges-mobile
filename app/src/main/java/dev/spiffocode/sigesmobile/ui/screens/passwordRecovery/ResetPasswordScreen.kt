package dev.spiffocode.sigesmobile.ui.screens.passwordRecovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.spiffocode.sigesmobile.ui.components.PasswordTextField
import dev.spiffocode.sigesmobile.ui.components.PrimaryButton
import dev.spiffocode.sigesmobile.ui.theme.Background
import dev.spiffocode.sigesmobile.ui.theme.Lav
import dev.spiffocode.sigesmobile.ui.theme.Plum
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.ui.theme.Teal
import dev.spiffocode.sigesmobile.ui.theme.TextPrimary
import dev.spiffocode.sigesmobile.ui.theme.TextSecondary
import dev.spiffocode.sigesmobile.viewmodel.ResetPasswordViewModel

@Composable
fun ResetPasswordScreen(
    token: String,
    emailFromLink: String = "",
    onNavigateToLogin: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isSuccess) {
        Column(
            modifier            = Modifier.fillMaxSize().background(Background).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier         = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Teal, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "¡Contraseña actualizada!",
                fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 22.sp, textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Tu contraseña ha sido cambiada exitosamente. Ya puedes iniciar sesión con tus nuevas credenciales.",
                color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            PrimaryButton(text = "Ir al Login", onClick = onNavigateToLogin, isLoading = false)
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().background(Background).padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier         = Modifier.size(80.dp).clip(CircleShape).background(Lav).align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = Plum, modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text       = "Crear nueva contraseña",
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (emailFromLink.isNotBlank()) {
                Text("Usuario a recuperar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value         = emailFromLink,
                    onValueChange = {},
                    enabled       = false,
                    leadingIcon   = { Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary) },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        disabledTextColor        = TextSecondary,
                        disabledBorderColor      = Color(0xFFE5E7EB),
                        disabledContainerColor   = Color(0xFFF3F4F6),
                        disabledLeadingIconColor = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("Nueva Contraseña", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordTextField(
                value              = state.newPassword,
                onValueChange      = viewModel::onNewPasswordChange,
                placeholder        = "Mínimo 8 caracteres",
                leadingIcon        = Icons.Default.Lock,
                isVisible          = state.isNewPasswordVisible,
                onVisibilityToggle = viewModel::toggleNewPasswordVisibility
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Confirmar Contraseña", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordTextField(
                value              = state.confirmPassword,
                onValueChange      = viewModel::onConfirmPasswordChange,
                placeholder        = "Repite tu nueva contraseña",
                leadingIcon        = Icons.Default.Lock,
                isVisible          = state.isConfirmPasswordVisible,
                onVisibilityToggle = viewModel::toggleConfirmPasswordVisibility
            )

            state.errorMessage?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text      = "Actualizar Contraseña",
                onClick   = { viewModel.resetPassword(token) },
                isLoading = state.isLoading
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordScreenPreview() {
    SigesmobileTheme {
        ResetPasswordScreen(token = "preview", onNavigateToLogin = {})
    }
}