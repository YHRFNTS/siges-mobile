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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.spiffocode.sigesmobile.ui.components.login.LoginForm
import dev.spiffocode.sigesmobile.ui.components.login.LoginHeader
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
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

    LoginContent(
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

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun LoginScreenPreview() {
    SigesmobileTheme {
        LoginContent(
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
            identifier = "usuario@utez.edu.mx",
            password = "",
            isLoading = true,
            errorMessage = null,
            isPasswordVisible = true,
            rememberMe = false,
        )
    }
}