package dev.spiffocode.sigesmobile.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.spiffocode.sigesmobile.ui.components.SigesErrorBanner
import dev.spiffocode.sigesmobile.ui.components.passwordRecovery.ProfileField
import dev.spiffocode.sigesmobile.ui.components.profile.ProfileImagePicker
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.EditProfileUiState
import dev.spiffocode.sigesmobile.viewmodel.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    windowSizeClass: WindowSizeClass? = null,
    viewModel: EditProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }

    EditProfileScreenContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onSaveClick = viewModel::saveChanges,
        onUploadProfilePicture = {viewModel.uploadProfilePicture(it)}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreenContent(
    state: EditProfileUiState,
    onNavigateBack: () -> Unit = {},
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onPhoneNumberChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onUploadProfilePicture: (ByteArray) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                ProfileImagePicker(
                    fallbackInitial = state.firstName.firstOrNull()?.uppercase()?.first(),
                    profilePictureUrl = state.pendingPictureBytes ?: state.profilePictureUrl,
                    isUploadingPicture = state.isLoading,
                    onUploadPicture = onUploadProfilePicture
                )

                Spacer(modifier = Modifier.height(32.dp))

                ProfileField(
                    label = "Nombre",
                    value = state.firstName,
                    onValueChange = onFirstNameChange,
                    isError = state.isFirstNameError
                )
                ProfileField(
                    label = "Apellidos",
                    value = state.lastName,
                    onValueChange = onLastNameChange,
                    isError = state.isLastNameError
                )
                ProfileField(
                    label = "Tipo de Usuario",
                    value = state.role?.name ?: "",
                    readOnly = true
                )

                if (!state.employeeNumber.isNullOrBlank()) {
                    ProfileField(
                        label = "Número de Empleado",
                        value = state.employeeNumber,
                        readOnly = true
                    )
                }
                if (!state.registrationNumber.isNullOrBlank()) {
                    ProfileField(
                        label = "Matrícula",
                        value = state.registrationNumber,
                        readOnly = true
                    )
                }

                ProfileField(label = "Correo Electrónico", value = state.email, readOnly = true)
                ProfileField(
                    label = "Teléfono",
                    value = state.phoneNumber,
                    onValueChange = onPhoneNumberChange,
                    isError = state.isPhoneNumberError
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !state.isLoading && !state.isUploadingPicture
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.background
                        )
                    } else {
                        Text("Guardar Cambios", style = MaterialTheme.typography.titleMedium)
                    }
                }

                state.error?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    SigesErrorBanner(errorMessage = it)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    SigesmobileTheme {
        EditProfileScreenContent(
            state = EditProfileUiState(
                firstName = "Ana",
                lastName = "Martínez López",
                email = "ana.martinez@institucion.edu",
                phoneNumber = "+52 777 555 1234",
                employeeNumber = "EMP-8870"
            )
        )
    }
}
