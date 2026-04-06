package dev.spiffocode.sigesmobile.ui.screens.passwordRecovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.spiffocode.sigesmobile.ui.components.login.LoginHeader
import dev.spiffocode.sigesmobile.ui.components.passwordRecovery.ResetPasswordForm
import dev.spiffocode.sigesmobile.ui.components.passwordRecovery.ResetPasswordSuccess
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.ResetPasswordUiState
import dev.spiffocode.sigesmobile.viewmodel.ResetPasswordViewModel

@Composable
fun ResetPasswordScreen(
    windowSizeClass: WindowSizeClass,
    token: String,
    emailFromLink: String = "",
    onNavigateToLogin: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    ResetPasswordContent(
        isCompact = isCompact,
        state = state,
        emailFromLink = emailFromLink,
        onNavigateToLogin = onNavigateToLogin,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        toggleNewPasswordVisibility = viewModel::toggleNewPasswordVisibility,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        toggleConfirmPasswordVisibility = viewModel::toggleConfirmPasswordVisibility,
        resetPassword = { viewModel.resetPassword(token) }
    )

}

@Composable
fun ResetPasswordContent(
    isCompact: Boolean,
    state: ResetPasswordUiState,
    emailFromLink: String = "",
    onNavigateToLogin: () -> Unit = {},
    onNewPasswordChange: (String) -> Unit = {},
    toggleNewPasswordVisibility: () -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    toggleConfirmPasswordVisibility: () -> Unit = {},
    resetPassword: () -> Unit = {},
){
    if (isCompact) {
        MobileResetPasswordLayout(
            state = state,
            emailFromLink = emailFromLink,
            onNavigateToLogin = onNavigateToLogin,
            onNewPasswordChange = onNewPasswordChange,
            toggleNewPasswordVisibility = toggleNewPasswordVisibility,
            onConfirmPasswordChange = onConfirmPasswordChange,
            toggleConfirmPasswordVisibility = toggleConfirmPasswordVisibility,
            resetPassword = resetPassword
        )
    } else {
        ExpandedResetPasswordLayout(
            state = state,
            emailFromLink = emailFromLink,
            onNavigateToLogin = onNavigateToLogin,
            onNewPasswordChange = onNewPasswordChange,
            toggleNewPasswordVisibility = toggleNewPasswordVisibility,
            onConfirmPasswordChange = onConfirmPasswordChange,
            toggleConfirmPasswordVisibility = toggleConfirmPasswordVisibility,
            resetPassword = resetPassword
        )
    }
}

@Composable
fun MobileResetPasswordLayout(
    state: ResetPasswordUiState,
    emailFromLink: String,
    onNavigateToLogin: () -> Unit,
    onNewPasswordChange: (String) -> Unit,
    toggleNewPasswordVisibility: () -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    toggleConfirmPasswordVisibility: () -> Unit,
    resetPassword: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isSuccess) {
            ResetPasswordSuccess(
                onNavigateToLogin = onNavigateToLogin
            )
        } else {
            ResetPasswordForm(
                emailFromLink = emailFromLink,
                newPassword = state.newPassword,
                onNewPasswordChange = onNewPasswordChange,
                isNewPasswordVisible = state.isNewPasswordVisible,
                onConfirmPasswordChange = onConfirmPasswordChange,
                confirmPassword = state.confirmPassword,
                isLoading = state.isLoading,
                isConfirmPasswordVisible = state.isConfirmPasswordVisible,
                toggleNewPasswordVisibility = toggleNewPasswordVisibility,
                toggleConfirmPasswordVisibility = toggleConfirmPasswordVisibility,
                resetPassword = resetPassword,
                errorMessage = state.errorMessage
            )
        }
    }
}

@Composable
fun ExpandedResetPasswordLayout(
    state: ResetPasswordUiState,
    emailFromLink: String,
    onNavigateToLogin: () -> Unit,
    onNewPasswordChange: (String) -> Unit,
    toggleNewPasswordVisibility: () -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    toggleConfirmPasswordVisibility: () -> Unit,
    resetPassword: () -> Unit
){
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1.2f).fillMaxHeight()
        ) {
            LoginHeader(modifier = Modifier.fillMaxSize())
        }
        
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
                if (state.isSuccess) {
                    ResetPasswordSuccess(
                        onNavigateToLogin = onNavigateToLogin
                    )
                } else {
                    ResetPasswordForm(
                        emailFromLink = emailFromLink,
                        newPassword = state.newPassword,
                        onNewPasswordChange = onNewPasswordChange,
                        isNewPasswordVisible = state.isNewPasswordVisible,
                        onConfirmPasswordChange = onConfirmPasswordChange,
                        confirmPassword = state.confirmPassword,
                        isLoading = state.isLoading,
                        isConfirmPasswordVisible = state.isConfirmPasswordVisible,
                        toggleNewPasswordVisibility = toggleNewPasswordVisibility,
                        toggleConfirmPasswordVisibility = toggleConfirmPasswordVisibility,
                        resetPassword = resetPassword,
                        errorMessage = state.errorMessage
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1920dp,height=1080dp,dpi=160")
@Composable
fun ResetPasswordScreenPreview() {
    SigesmobileTheme {
        ResetPasswordContent(
            isCompact = true,
            state = ResetPasswordUiState(
                newPassword = "test123#",
                confirmPassword = "test123#",
                isLoading = false,
                isSuccess = false,
                errorMessage = null
            ),
            emailFromLink = "20243ds158@utez.edu.mx"
        )
    }
}