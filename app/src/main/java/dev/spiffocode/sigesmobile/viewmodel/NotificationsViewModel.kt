package dev.spiffocode.sigesmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spiffocode.sigesmobile.data.remote.NetworkResult
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationReadStatus
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.domain.repository.NotificationRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
        loadUnreadCount()
        observeFcmUpdates()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val notifications = notificationsRepository
                    .listNotifications(page = 0, size = 20, sort = "sentAt,desc")

                if(notifications is NetworkResult.Success){
                    _uiState.update {
                        it.copy(
                            notifications = (it.notifications + notifications.data.content).distinctBy { n -> n.id },
                            hasNextPage = !notifications.data.last,
                            totalElements = notifications.data.totalElements
                        )
                    }
                }

            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadUnreadCount() {
        viewModelScope.launch {
            val result = notificationsRepository.listNotifications(
                status = NotificationReadStatus.UNREAD,
                size = 1
            )
            if (result is NetworkResult.Success) {
                _uiState.update { it.copy(unreadCount = result.data.totalElements) }
            }
        }
    }

    private fun observeFcmUpdates() {
        viewModelScope.launch {
            notificationsRepository.incomingNotifications.collect { newNotif ->
                _uiState.update { current ->
                    if (current.notifications.any { it.id == newNotif.id }) {
                        current
                    } else {
                        current.copy(
                            notifications = listOf(newNotif) + current.notifications,
                            totalElements = current.totalElements + 1,
                            unreadCount = current.unreadCount + 1
                        )
                    }
                }
            }
        }
    }

    fun loadNextPage() {
        val current = _uiState.value
        if (current.isLoadingMore || !current.hasNextPage) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            val nextPage = current.currentPage + 1
            try {
                val notifications = notificationsRepository
                    .listNotifications(page = nextPage, size = 20)

                if(notifications is NetworkResult.Success){
                    _uiState.update {
                        it.copy(
                            notifications = (it.notifications + notifications.data.content).distinctBy { n -> n.id },
                            hasNextPage = !notifications.data.last,
                            currentPage = nextPage,
                            isLoadingMore = false
                        )
                    }
                }

            } finally {
                _uiState.update { it.copy(isLoadingMore = false) }
            }
        }
    }

    fun onClick(notification: NotificationResponse){
        if (notification.readStatus == NotificationReadStatus.UNREAD) {
            viewModelScope.launch {
                notificationsRepository.markAsRead(notification.id)
            }
            _uiState.update { current ->
                current.copy(
                    notifications = current.notifications.map { 
                        if (it.id == notification.id) it.copy(readStatus = NotificationReadStatus.READ) else it 
                    },
                    unreadCount = (current.unreadCount - 1).coerceAtLeast(0)
                )
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            notificationsRepository.markAllAsRead()
            _uiState.update { current ->
                current.copy(
                    notifications = current.notifications.map { it.copy(readStatus = NotificationReadStatus.READ) },
                    unreadCount = 0
                )
            }
        }
    }
}



data class NotificationsUiState(
    val notifications: List<NotificationResponse> = emptyList(),
    val totalElements: Long = 0,
    val unreadCount: Long = 0,
    val currentPage: Int = 0,
    val hasNextPage: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
)