package dev.spiffocode.sigesmobile.ui.components.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.spiffocode.sigesmobile.ui.components.PasswordTextField
import dev.spiffocode.sigesmobile.ui.components.PrimaryButton
import dev.spiffocode.sigesmobile.ui.components.PrimaryTextField
import dev.spiffocode.sigesmobile.ui.components.SigesErrorBanner
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.LoginViewModel

@Composable
public fun LoginForm(
    identifier: String,
    onIdentifierChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    togglePasswordVisibility: () -> Unit,
    rememberMe: Boolean,
    toggleRememberMe: (Boolean) -> Unit,
    errorMessage: String?,
    onLogin: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onNavigateToForgotPassword: () -> Unit,
    isIdentifierError: Boolean = false,
    isPasswordError: Boolean = false
) {

    Column(modifier = modifier
        .padding(24.dp)
        .widthIn(max = 480.dp)) {

        Text("Bienvenido", style = MaterialTheme.typography.headlineLarge,  color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Ingresa tus credenciales para acceder", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Usuario", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryTextField(
            value         = identifier,
            onValueChange = onIdentifierChange,
            placeholder   = "Correo / Matrícula / Número de empleado",
            leadingIcon   = Icons.Default.Email,
            isError       = isIdentifierError
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Contraseña", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(
            value              = password,
            onValueChange      = onPasswordChange,
            placeholder        = "••••••••",
            leadingIcon        = Icons.Default.Lock,
            isVisible          = isPasswordVisible,
            onVisibilityToggle = togglePasswordVisibility,
            isError            = isPasswordError
        )

        SigesErrorBanner(errorMessage = errorMessage)

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked         = rememberMe,
                    onCheckedChange = toggleRememberMe,
                    colors          = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Text("Recordarme", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick        = onNavigateToForgotPassword,
                modifier       = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text       = "¿Olvidaste tu contraseña?",
                    style   =  MaterialTheme.typography.bodyMedium,
                    color      = MaterialTheme.colorScheme.primary,
                    textAlign  = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text      = "Iniciar Sesión",
            onClick   = onLogin,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
public fun LoginForm(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    onNavigateToForgotPassword: () -> Unit
){
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LoginForm(
        identifier                 = state.identifier,
        onIdentifierChange         = viewModel::onIdentifierChange,
        password                   = state.password,
        onPasswordChange           = viewModel::onPasswordChange,
        isPasswordVisible          = state.isPasswordVisible,
        togglePasswordVisibility   = viewModel::togglePasswordVisibility,
        rememberMe                 = state.rememberMe,
        toggleRememberMe           = viewModel::toggleRememberMe,
        errorMessage               = state.errorMessage,
        onLogin                    = viewModel::login,
        isLoading                  = state.isLoading,
        modifier                   = modifier,
        onNavigateToForgotPassword = onNavigateToForgotPassword,
        isIdentifierError          = state.isIdentifierError,
        isPasswordError            = state.isPasswordError
    )
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {
    SigesmobileTheme {
        LoginForm(
            identifier = "test",
            onIdentifierChange = {},
            password = "test",
            onPasswordChange = {},
            isPasswordVisible = false,
            togglePasswordVisibility = {},
            rememberMe = false,
            toggleRememberMe = {},
            errorMessage = null,
            onLogin = {},
            isLoading = false,
            onNavigateToForgotPassword = {}
        )
    }
}