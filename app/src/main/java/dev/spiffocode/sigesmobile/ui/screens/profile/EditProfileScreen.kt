package dev.spiffocode.sigesmobile.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dev.spiffocode.sigesmobile.ui.theme.*
import dev.spiffocode.sigesmobile.viewmodel.EditProfileUiState
import dev.spiffocode.sigesmobile.viewmodel.EditProfileViewModel
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            if (bytes != null) {
                viewModel.uploadProfilePicture(bytes)
            }
        }
    }

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
        onChangePhotoClick = { imagePickerLauncher.launch("image/*") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreenContent(
    state: EditProfileUiState,
    onNavigateBack: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onChangePhotoClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold, color = Plum) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Photo Picker avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(Plum, Lav)))
                    .clickable { onChangePhotoClick() },
                contentAlignment = Alignment.Center
            ) {
                if (!state.profilePictureUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = state.profilePictureUrl,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = state.firstName.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 36.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Camera icon overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isUploadingPicture) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Cambiar Foto", tint = Color.White)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Fields
            ProfileField(label = "NOMBRE", value = state.firstName, onValueChange = onFirstNameChange)
            ProfileField(label = "APELLIDOS", value = state.lastName, onValueChange = onLastNameChange)
            ProfileField(label = "TIPO DE USUARIO", value = state.role?.name ?: "", readOnly = true)
            
            if (!state.employeeNumber.isNullOrBlank()) {
                ProfileField(label = "NÚMERO DE EMPLEADO", value = state.employeeNumber, readOnly = true)
            }
            if (!state.registrationNumber.isNullOrBlank()) {
                ProfileField(label = "MATRÍCULA", value = state.registrationNumber, readOnly = true)
            }
            
            ProfileField(label = "CORREO ELECTRÓNICO", value = state.email, readOnly = true)
            ProfileField(label = "TELÉFONO", value = state.phoneNumber, onValueChange = onPhoneNumberChange)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Plum),
                enabled = !state.isLoading && !state.isUploadingPicture
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("✓ Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            
            state.error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = Color.Red, fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    readOnly: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            readOnly = readOnly,
            enabled = !readOnly,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Plum,
                unfocusedBorderColor = Color.LightGray,
                disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),
                disabledTextColor = TextSecondary
            )
        )
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
            ),
            onNavigateBack = {},
            onFirstNameChange = {},
            onLastNameChange = {},
            onPhoneNumberChange = {},
            onSaveClick = {},
            onChangePhotoClick = {}
        )
    }
}
