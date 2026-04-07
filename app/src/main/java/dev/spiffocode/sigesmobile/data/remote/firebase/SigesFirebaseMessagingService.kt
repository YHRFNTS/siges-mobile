package dev.spiffocode.sigesmobile.data.remote.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import dev.spiffocode.sigesmobile.MainActivity
import dev.spiffocode.sigesmobile.R
import dev.spiffocode.sigesmobile.data.local.SessionManager
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

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        scope.launch {
            sessionManager.saveFcmToken(token)
            if (sessionManager.isLoggedIn) {
                userRepository.registerPushToken(token)
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
            showNotification(it.title ?: "Siges", it.body ?: "", message.data)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "Actualización de Reservación"
        val body = data["body"] ?: "Tienes una nueva actualización en tu reservación."
        showNotification(title, body, data)
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

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
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

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
