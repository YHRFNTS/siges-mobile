package dev.spiffocode.sigesmobile.data.remote.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import dev.spiffocode.sigesmobile.MainActivity
import dev.spiffocode.sigesmobile.R
import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationMetadata
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationReadStatus
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.NotificationType
import dev.spiffocode.sigesmobile.domain.repository.NotificationRepository
import dev.spiffocode.sigesmobile.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SigesFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        val deviceId = android.provider.Settings.Secure.getString(
            contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )
        scope.launch {
            sessionManager.saveFcmToken(token)
            if (sessionManager.isLoggedIn) {
                userRepository.registerPushToken(token, deviceId)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "From: ${message.from}")

        // Check if message contains a data payload.
        if (message.data.isNotEmpty()) {
            Log.d("FCM", "Message data payload: " + message.data)
            handleDataMessage(message.data)
        }

        // Check if message contains a notification payload.
        message.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
            val data = message.data
            showNotification(it.title ?: "Siges", it.body ?: "", data)
            
            // Send to repository for in-app update
            scope.launch {
                val notification = mapToNotificationResponse(it.title ?: "Siges", it.body ?: "", data)
                notificationRepository.onFcmMessageReceived(notification)
            }
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "Actualización de Reservación"
        val body = data["body"] ?: "Tienes una nueva actualización en tu reservación."
        showNotification(title, body, data)
        
        // Send to repository for in-app update
        scope.launch {
            val notification = mapToNotificationResponse(title, body, data)
            notificationRepository.onFcmMessageReceived(notification)
        }
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val channelId = "siges_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Siges Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Canal para notificaciones de reservaciones"
        }
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val requestCode = (data["reservationId"] ?: data["id"] ?: System.currentTimeMillis().toString()).hashCode()

        val pendingIntent = PendingIntent.getActivity(
            this, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_siges_sinletras)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun mapToNotificationResponse(title: String, body: String, data: Map<String, String>): NotificationResponse {
        val id = (data["id"] ?: System.currentTimeMillis().toString()).toLongOrNull() ?: System.currentTimeMillis()
        val typeStr = data["type"] ?: "RESERVATION_CREATED"
        val type = try { NotificationType.valueOf(typeStr) } catch(e: Exception) { NotificationType.RESERVATION_CREATED }
        
        return NotificationResponse(
            id = id,
            title = title,
            message = body,
            readStatus = NotificationReadStatus.UNREAD,
            type = type,
            sentAt = java.time.LocalDateTime.now(),
            reservation = null, // Summary not available in push
            metadata = NotificationMetadata(
                reservationId = data["reservationId"]?.toLongOrNull()
            )
        )
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
