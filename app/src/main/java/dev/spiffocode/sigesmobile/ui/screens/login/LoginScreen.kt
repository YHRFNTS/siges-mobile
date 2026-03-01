package dev.spiffocode.sigesmobile.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import dev.spiffocode.sigesmobile.R
import dev.spiffocode.sigesmobile.ui.components.PasswordTextField
import dev.spiffocode.sigesmobile.ui.components.PrimaryButton
import dev.spiffocode.sigesmobile.ui.components.PrimaryTextField

val Plum = Color(0xFF6B5B95)
val TextPrimary = Color(0xFF2D3142)
val TextSecondary = Color(0xFF6B7280)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFCFB))
            .verticalScroll(scrollState)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                Text("SIGES", fontSize = 32.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("SISTEMA DE GESTIÓN DE ESPACIOS Y EQUIPOS", fontSize = 12.sp, color = TextSecondary)
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {

            Text("Bienvenido", fontSize = 26.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Ingresa tus credenciales para acceder", fontSize = 14.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(24.dp))

            Text("Correo Institucional", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            PrimaryTextField(
                value = state.email,
                onValueChange = { viewModel.onEmailChange(it) },
                placeholder = "usuario@utez.edu.mx",
                leadingIcon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Contraseña", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            PasswordTextField(
                value = state.contrasena,
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = state.rememberMe,
                        onCheckedChange = { viewModel.toggleRememberMe(it) },
                        colors = CheckboxDefaults.colors(checkedColor = Plum)
                    )
                    Text("Recordarme", fontSize = 14.sp, color = TextPrimary)
                }
                Text("¿Olvidaste tu contraseña?", fontSize = 14.sp, color = Plum, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Iniciar Sesión",
                onClick = { viewModel.login() },
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}