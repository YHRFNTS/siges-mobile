package dev.spiffocode.sigesmobile.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.spiffocode.sigesmobile.ui.components.login.LoginForm
import dev.spiffocode.sigesmobile.ui.components.login.LoginHeader
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            when(it) {
                LoginViewModel.UiEvent.LoginSuccess -> onNavigateToHome()
                LoginViewModel.UiEvent.LoginError -> Unit
            }
        }
    }

    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    LoginContent(
        isCompact = isCompact,
        identifier = uiState.identifier,
        onIdentifierChange = viewModel::onIdentifierChange,
        password = uiState.password,
        onPasswordChange = viewModel::onPasswordChange,
        isPasswordVisible = uiState.isPasswordVisible,
        togglePasswordVisibility = viewModel::togglePasswordVisibility,
        rememberMe = uiState.rememberMe,
        toggleRememberMe = viewModel::toggleRememberMe,
        errorMessage = uiState.errorMessage,
        onLogin = viewModel::login,
        isLoading = uiState.isLoading,
        onNavigateToForgotPassword = onNavigateToForgotPassword,
    )

}

@Composable
fun LoginContent(
    isCompact: Boolean,
    identifier: String,
    onIdentifierChange: (String) -> Unit = {},
    password: String,
    onPasswordChange: (String) -> Unit = {},
    isPasswordVisible: Boolean,
    togglePasswordVisibility: () -> Unit = {},
    rememberMe: Boolean,
    toggleRememberMe: (Boolean) -> Unit = {},
    errorMessage: String?,
    onLogin: () -> Unit = {},
    isLoading: Boolean,
    onNavigateToForgotPassword: () -> Unit = {}
) {
    if (isCompact) {
        MobileLoginLayout(
            identifier = identifier,
            onIdentifierChange = onIdentifierChange,
            password = password,
            onPasswordChange = onPasswordChange,
            isPasswordVisible = isPasswordVisible,
            togglePasswordVisibility = togglePasswordVisibility,
            rememberMe = rememberMe,
            toggleRememberMe = toggleRememberMe,
            errorMessage = errorMessage,
            onLogin = onLogin,
            isLoading = isLoading,
            onNavigateToForgotPassword = onNavigateToForgotPassword
        )
    } else {
        ExpandedLoginLayout(
            identifier = identifier,
            onIdentifierChange = onIdentifierChange,
            password = password,
            onPasswordChange = onPasswordChange,
            isPasswordVisible = isPasswordVisible,
            togglePasswordVisibility = togglePasswordVisibility,
            rememberMe = rememberMe,
            toggleRememberMe = toggleRememberMe,
            errorMessage = errorMessage,
            onLogin = onLogin,
            isLoading = isLoading,
            onNavigateToForgotPassword = onNavigateToForgotPassword
        )
    }
}

@Composable
fun MobileLoginLayout(
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
    onNavigateToForgotPassword: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(480.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            LoginHeader(modifier = Modifier.fillMaxWidth())
            LoginForm(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                identifier = identifier,
                onIdentifierChange = onIdentifierChange,
                password = password,
                onPasswordChange = onPasswordChange,
                isPasswordVisible = isPasswordVisible,
                togglePasswordVisibility = togglePasswordVisibility,
                rememberMe = rememberMe,
                toggleRememberMe = toggleRememberMe,
                errorMessage = errorMessage,
                onLogin = onLogin,
                isLoading = isLoading,
                onNavigateToForgotPassword = onNavigateToForgotPassword,
            )
        }
    }
}

@Composable
fun ExpandedLoginLayout(
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
    onNavigateToForgotPassword: () -> Unit
) {
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
                    .widthIn(max = 450.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
                LoginForm(
                    modifier = Modifier.fillMaxWidth(),
                    identifier = identifier,
                    onIdentifierChange = onIdentifierChange,
                    password = password,
                    onPasswordChange = onPasswordChange,
                    isPasswordVisible = isPasswordVisible,
                    togglePasswordVisibility = togglePasswordVisibility,
                    rememberMe = rememberMe,
                    toggleRememberMe = toggleRememberMe,
                    errorMessage = errorMessage,
                    onLogin = onLogin,
                    isLoading = isLoading,
                    onNavigateToForgotPassword = onNavigateToForgotPassword,
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun LoginScreenPreview() {
    SigesmobileTheme {
        LoginContent(
            isCompact = false,
            identifier = "usuario@utez.edu.mx",
            password = "",
            isLoading = false,
            errorMessage = null,
            isPasswordVisible = true,
            rememberMe = false,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenErrorPreview() {
    SigesmobileTheme {
        LoginContent(
            isCompact = true,
            identifier = "usuario@utez.edu.mx",
            password = "",
            isLoading = false,
            errorMessage = "Error de conexión",
            isPasswordVisible = true,
            rememberMe = false,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenLoadingErrorPreview() {
    SigesmobileTheme {
        LoginContent(
            isCompact = true,
            identifier = "usuario@utez.edu.mx",
            password = "",
            isLoading = true,
            errorMessage = null,
            isPasswordVisible = true,
            rememberMe = false,
        )
    }
}