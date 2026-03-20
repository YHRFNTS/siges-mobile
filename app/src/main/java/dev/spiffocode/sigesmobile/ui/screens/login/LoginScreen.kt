package dev.spiffocode.sigesmobile.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.spiffocode.sigesmobile.R
import dev.spiffocode.sigesmobile.ui.components.login.PasswordTextField
import dev.spiffocode.sigesmobile.ui.components.login.PrimaryButton
import dev.spiffocode.sigesmobile.ui.components.login.PrimaryTextField
import dev.spiffocode.sigesmobile.ui.theme.PlumLogin
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.ui.theme.TextPrimaryLogin
import dev.spiffocode.sigesmobile.ui.theme.TextSecondaryLogin
import dev.spiffocode.sigesmobile.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFCFB))
            .verticalScroll(scrollState)
    ) {
        LoginHeader(modifier = Modifier.fillMaxWidth())

        LoginForm(
            modifier = Modifier.fillMaxWidth(),
            viewModel = viewModel,
            onNavigateToHome = onNavigateToHome,
            onNavigateToForgotPassword = onNavigateToForgotPassword
        )
    }
}

@Composable
private fun LoginHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Brush.linearGradient(colors = listOf(Color(0xFFE8DFF5), Color(0xFFD9E8F5))))
            .padding(top = 80.dp, bottom = 48.dp, start = 24.dp, end = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, shape = CircleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_siges_sinletras),
                    contentDescription = "Logo de SIGES",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("SIGES", fontSize = 32.sp, color = TextPrimaryLogin, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("SISTEMA DE GESTIÓN DE ESPACIOS Y EQUIPOS", fontSize = 12.sp, color = TextSecondaryLogin, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun LoginForm(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = modifier.padding(24.dp)) {

        Text("Bienvenido", fontSize = 26.sp, color = TextPrimaryLogin, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Ingresa tus credenciales para acceder", fontSize = 14.sp, color = TextSecondaryLogin)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Usuario", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLogin)
        Spacer(modifier = Modifier.height(8.dp))

        PrimaryTextField(
            value = state.email,
            onValueChange = { viewModel.onEmailChange(it) },
            placeholder = "Usuario / Correo Institucional",
            leadingIcon = Icons.Default.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Contraseña", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLogin)
        Spacer(modifier = Modifier.height(8.dp))

        PasswordTextField(
            value = state.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            placeholder = "••••••••",
            leadingIcon = Icons.Default.Lock,
            isVisible = state.isPasswordVisible,
            onVisibilityToggle = { viewModel.togglePasswordVisibility() }
        )

        state.errorMessage?.let { error ->
            Text(text = error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.rememberMe,
                    onCheckedChange = { viewModel.toggleRememberMe(it) },
                    colors = CheckboxDefaults.colors(checkedColor = PlumLogin)
                )
                Text("Recordarme", fontSize = 14.sp, color = TextPrimaryLogin)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onNavigateToForgotPassword,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    fontSize = 14.sp,
                    color = PlumLogin,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Iniciar Sesión",
            onClick = {
                viewModel.login(onSuccess = { onNavigateToHome() })
            },
            isLoading = state.isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SigesmobileTheme {
        LoginScreen(
            onNavigateToHome = {},
            onNavigateToForgotPassword = {},
        )
    }
}