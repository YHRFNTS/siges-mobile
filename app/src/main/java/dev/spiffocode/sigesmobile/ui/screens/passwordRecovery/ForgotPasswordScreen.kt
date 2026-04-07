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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.spiffocode.sigesmobile.ui.components.PrimaryButton
import dev.spiffocode.sigesmobile.ui.components.PrimaryTextField
import dev.spiffocode.sigesmobile.ui.components.SigesErrorBanner
import dev.spiffocode.sigesmobile.ui.components.login.LoginHeader
import dev.spiffocode.sigesmobile.ui.theme.SigesTheme
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.ForgotPasswordUiState
import dev.spiffocode.sigesmobile.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    windowSizeClass: WindowSizeClass,
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    ForgotPasswordContent(
        isCompact = isCompact,
        state = state,
        onNavigateBack = onNavigateBack,
        onEmailChange = viewModel::onEmailChange,
        sendRecoveryEmail = viewModel::sendRecoveryEmail
    )
}

@Composable
fun ForgotPasswordContent(
    isCompact: Boolean,
    state: ForgotPasswordUiState,
    onNavigateBack: () -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    sendRecoveryEmail: () -> Unit = {}
) {
    if (isCompact) {
        MobileForgotPasswordLayout(
            state = state,
            onNavigateBack = onNavigateBack,
            onEmailChange = onEmailChange,
            sendRecoveryEmail = sendRecoveryEmail
        )
    } else {
        ExpandedForgotPasswordLayout(
            state = state,
            onNavigateBack = onNavigateBack,
            onEmailChange = onEmailChange,
            sendRecoveryEmail = sendRecoveryEmail
        )
    }
}

@Composable
fun MobileForgotPasswordLayout(
    state: ForgotPasswordUiState,
    onNavigateBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    sendRecoveryEmail: () -> Unit
){
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

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
                text = "Recuperar Contraseña",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ingresa tu correo institucional y te enviaremos un enlace seguro para restablecer tu acceso.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (!state.isSent) {
                Text(
                    "Correo Institucional",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                PrimaryTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = "usuario@utez.edu.mx",
                    leadingIcon = Icons.Default.Email,
                    isError = state.isEmailError
                )

                state.errorMessage?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    SigesErrorBanner(errorMessage = it)
                }

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(
                    text = "Enviar Instrucciones",
                    onClick = sendRecoveryEmail,
                    isLoading = state.isLoading
                )
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SigesTheme.extendedColors.statusApproved),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "¡Correo enviado!",
                            style = MaterialTheme.typography.titleMedium,
                            color = SigesTheme.extendedColors.onStatusApproved
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Revisa la bandeja de entrada de ${state.email} para continuar.",
                            color = SigesTheme.extendedColors.onStatusApproved,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(text = "Volver", onClick = onNavigateBack, isLoading = false)
            }
        }
    }
}

@Composable
fun ExpandedForgotPasswordLayout(
    state: ForgotPasswordUiState,
    onNavigateBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    sendRecoveryEmail: () -> Unit
){
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Branding Pane
        Box(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
        ) {
            LoginHeader(modifier = Modifier.fillMaxSize())
        }
        
        // Right Form Pane
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

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
                    text = "Recuperar Contraseña",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ingresa tu correo institucional y te enviaremos un enlace seguro para restablecer tu acceso.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (!state.isSent) {
                    Text(
                        "Correo Institucional",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    PrimaryTextField(
                        value = state.email,
                        onValueChange = onEmailChange,
                        placeholder = "usuario@utez.edu.mx",
                        leadingIcon = Icons.Default.Email,
                        isError = state.isEmailError
                    )

                    state.errorMessage?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        SigesErrorBanner(errorMessage = it)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    PrimaryButton(
                        text = "Enviar Instrucciones",
                        onClick = sendRecoveryEmail,
                        isLoading = state.isLoading
                    )
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SigesTheme.extendedColors.statusApproved),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "¡Correo enviado!",
                                style = MaterialTheme.typography.titleMedium,
                                color = SigesTheme.extendedColors.onStatusApproved
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Revisa la bandeja de entrada de ${state.email} para continuar.",
                                color = SigesTheme.extendedColors.onStatusApproved,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    PrimaryButton(text = "Volver", onClick = onNavigateBack, isLoading = false)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    SigesmobileTheme {
        ForgotPasswordContent(
            isCompact = true,
            state = ForgotPasswordUiState(
                email = "usuario@utez.edu.mx",
                isSent = false,
                isLoading = false,
                errorMessage = null
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenLoadingPreview() {
    SigesmobileTheme {
        ForgotPasswordContent(
            isCompact = true,
            state = ForgotPasswordUiState(
                email = "usuario@utez.edu.mx",
                isSent = false,
                isLoading = true,
                errorMessage = null
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenSentPreview() {
    SigesmobileTheme {
        ForgotPasswordContent(
            isCompact = true,
            state = ForgotPasswordUiState(
                email = "usuario@utez.edu.mx",
                isSent = true,
                isLoading = false,
                errorMessage = null
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenErrorPreview() {
    SigesmobileTheme {
        ForgotPasswordContent(
            isCompact = true,
            state = ForgotPasswordUiState(
                email = "usuario@utez.edu.mx",
                isSent = false,
                isLoading = false,
                errorMessage = "Error de conexión"
            )
        )
    }
}