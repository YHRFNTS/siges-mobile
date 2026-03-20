package dev.spiffocode.sigesmobile.ui.screens.passwordRecovery

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.spiffocode.sigesmobile.ui.components.login.PrimaryButton
import dev.spiffocode.sigesmobile.ui.components.login.PrimaryTextField
import dev.spiffocode.sigesmobile.ui.theme.Background
import dev.spiffocode.sigesmobile.ui.theme.Lav
import dev.spiffocode.sigesmobile.ui.theme.Mint
import dev.spiffocode.sigesmobile.ui.theme.Plum
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.ui.theme.Teal
import dev.spiffocode.sigesmobile.ui.theme.TextPrimary
import dev.spiffocode.sigesmobile.ui.theme.TextSecondary
import dev.spiffocode.sigesmobile.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        IconButton(
            onClick  = onNavigateBack,
            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Lav)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Plum)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier         = Modifier.size(80.dp).clip(CircleShape).background(Lav).align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Lock, contentDescription = null, tint = Plum, modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text       = "Recuperar Contraseña",
            fontSize   = 26.sp,
            fontWeight = FontWeight.Bold,
            color      = TextPrimary,
            textAlign  = TextAlign.Center,
            modifier   = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text      = "Ingresa tu correo institucional y te enviaremos un enlace seguro para restablecer tu acceso.",
            fontSize  = 14.sp,
            color     = TextSecondary,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!state.isSent) {
            Text("Correo Institucional", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            PrimaryTextField(
                value         = state.email,
                onValueChange = viewModel::onEmailChange,
                placeholder   = "usuario@utez.edu.mx",
                leadingIcon   = Icons.Default.Email
            )

            state.errorMessage?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text      = "Enviar Instrucciones",
                onClick   = viewModel::sendRecoveryEmail,
                isLoading = state.isLoading
            )
        } else {
            Card(
                colors   = CardDefaults.cardColors(containerColor = Mint),
                shape    = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier            = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("¡Correo enviado!", fontWeight = FontWeight.Bold, color = Teal, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Revisa la bandeja de entrada de ${state.email} para continuar.",
                        color     = Teal,
                        fontSize  = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(text = "Volver", onClick = onNavigateBack, isLoading = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    SigesmobileTheme { ForgotPasswordScreen(onNavigateBack = {}) }
}