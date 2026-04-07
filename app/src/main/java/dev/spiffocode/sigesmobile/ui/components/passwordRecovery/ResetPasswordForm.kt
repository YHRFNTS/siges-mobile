package dev.spiffocode.sigesmobile.ui.components.passwordRecovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.spiffocode.sigesmobile.ui.components.PasswordTextField
import dev.spiffocode.sigesmobile.ui.components.PrimaryButton
import dev.spiffocode.sigesmobile.ui.components.SigesErrorBanner
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme


@Composable
fun ResetPasswordForm(
    emailFromLink: String,
    newPassword: String,
    modifier: Modifier = Modifier,
    onNewPasswordChange: (String) -> Unit = {},
    isNewPasswordVisible: Boolean,
    toggleNewPasswordVisibility: () -> Unit = {},
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit = {},
    isConfirmPasswordVisible: Boolean,
    toggleConfirmPasswordVisibility: () -> Unit = {},
    errorMessage: String?,
    resetPassword: () -> Unit = {},
    isLoading: Boolean,
    isNewPasswordError: Boolean = false,
    isConfirmPasswordError: Boolean = false

){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crear nueva contraseña",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (emailFromLink.isNotBlank()) {
                Text(
                    "Usuario a recuperar",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = emailFromLink,
                    onValueChange = {},
                    enabled = false,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                "Nueva Contraseña",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasswordTextField(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                placeholder = "Mínimo 8 caracteres",
                leadingIcon = Icons.Default.Lock,
                isVisible = isNewPasswordVisible,
                onVisibilityToggle = toggleNewPasswordVisibility,
                isError = isNewPasswordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Confirmar Contraseña",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasswordTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                placeholder = "Repite tu nueva contraseña",
                leadingIcon = Icons.Default.Lock,
                isVisible = isConfirmPasswordVisible,
                onVisibilityToggle = toggleConfirmPasswordVisibility,
                isError = isConfirmPasswordError
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                SigesErrorBanner(errorMessage = it)
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Actualizar Contraseña",
                onClick = resetPassword,
                isLoading = isLoading
            )
        }
    }
}

@Composable
@Preview
fun ResetPasswordFormPreview(){
    SigesmobileTheme {
        ResetPasswordForm(
            emailFromLink = "20243ds158@utez.edu.mx",
            newPassword = "test123#",
            isNewPasswordVisible = false,
            confirmPassword = "test123#",
            isConfirmPasswordVisible = false,
            errorMessage = null,
            isLoading = false
        )
    }
}


@Composable
@Preview
fun ResetPasswordFormLoadingPreview(){
    SigesmobileTheme(darkTheme = true) {
        ResetPasswordForm(
            emailFromLink = "20243ds158@utez.edu.mx",
            newPassword = "test123#",
            isNewPasswordVisible = false,
            confirmPassword = "test123#",
            isConfirmPasswordVisible = false,
            errorMessage = null,
            isLoading = true
        )
    }
}


@Composable
@Preview
fun ResetPasswordFormErrorPreview(){
    SigesmobileTheme {
        ResetPasswordForm(
            emailFromLink = "20243ds158@utez.edu.mx",
            newPassword = "test123#",
            isNewPasswordVisible = false,
            confirmPassword = "test123#",
            isConfirmPasswordVisible = false,
            errorMessage = "Error de red",
            isLoading = false
        )
    }
}