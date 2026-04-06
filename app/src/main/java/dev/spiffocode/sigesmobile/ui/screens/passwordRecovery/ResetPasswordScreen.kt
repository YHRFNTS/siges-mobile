package dev.spiffocode.sigesmobile.ui.screens.passwordRecovery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dev.spiffocode.sigesmobile.ui.components.passwordRecovery.ResetPasswordForm
import dev.spiffocode.sigesmobile.ui.components.passwordRecovery.ResetPasswordSuccess
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.ResetPasswordUiState
import dev.spiffocode.sigesmobile.viewmodel.ResetPasswordViewModel

@Composable
fun ResetPasswordScreen(
    token: String,
    emailFromLink: String = "",
    onNavigateToLogin: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    ResetPasswordContent(
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
    state: ResetPasswordUiState,
    emailFromLink: String = "",
    onNavigateToLogin: () -> Unit = {},
    onNewPasswordChange: (String) -> Unit = {},
    toggleNewPasswordVisibility: () -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    toggleConfirmPasswordVisibility: () -> Unit = {},
    resetPassword: () -> Unit = {},
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

@Preview(showBackground = true, device = "spec:width=1920dp,height=1080dp,dpi=160")
@Composable
fun ResetPasswordScreenPreview() {
    SigesmobileTheme {
        ResetPasswordContent(
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