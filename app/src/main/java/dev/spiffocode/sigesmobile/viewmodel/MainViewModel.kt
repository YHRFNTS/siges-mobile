package dev.spiffocode.sigesmobile.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.domain.repository.UserRepository
import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExternalNavigationEvent {
    data class ReservationDetail(val id: Long, val isAdmin: Boolean) : ExternalNavigationEvent()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<ExternalNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun handleIntent(intent: Intent?) {
        intent?.let {
            val reservationId = it.getStringExtra("reservationId")?.toLongOrNull()
                ?: it.getSerializableExtra("reservationId")?.toString()?.toLongOrNull()
            
            if (reservationId != null) {
                viewModelScope.launch {
                    _navigationEvent.emit(
                        ExternalNavigationEvent.ReservationDetail(
                            id = reservationId,
                            isAdmin = sessionManager.role == "ADMIN"
                        )
                    )
                }
            }
        }
    }

    fun registerFcmToken() {
        Log.d("MainViewModel", "registerFcmToken called. isLoggedIn: ${sessionManager.isLoggedIn}")
        val token = sessionManager.fcmToken
        if (token != null && sessionManager.isLoggedIn) {
            viewModelScope.launch {
                when (val result = userRepository.registerPushToken(token)) {
                    is dev.spiffocode.sigesmobile.data.remote.NetworkResult.Success -> {
                        Log.d("MainViewModel", "FCM Token registered successfully with API")
                    }
                    is dev.spiffocode.sigesmobile.data.remote.NetworkResult.Error -> {
                        Log.e("MainViewModel", "Failed to register FCM Token: ${result.message}")
                    }
                    else -> Unit
                }
            }
        } else if (token == null) {
            Log.w("MainViewModel", "No FCM token found in session manager")
        }
    }
}
