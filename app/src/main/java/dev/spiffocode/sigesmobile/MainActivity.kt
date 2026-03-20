package dev.spiffocode.sigesmobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.ui.navigation.AppNavigation
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SigesmobileTheme {
                AppNavigation(sessionManager = sessionManager)
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}