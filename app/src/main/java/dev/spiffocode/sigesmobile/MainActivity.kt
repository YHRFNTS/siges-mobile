package dev.spiffocode.sigesmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import dev.spiffocode.sigesmobile.ui.navigation.AppNavigation
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SigesmobileTheme {
                AppNavigation()
            }
        }
    }
}