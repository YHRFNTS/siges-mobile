package dev.spiffocode.sigesmobile.ui.components.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.R
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
public fun LoginHeader(modifier: Modifier = Modifier) {
    Box(
        modifier         = modifier
            .background(Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.primaryContainer)))
            .padding(top = 80.dp, bottom = 48.dp, start = 24.dp, end = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier         = Modifier.size(100.dp).background(MaterialTheme.colorScheme.background, shape = CircleShape).padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.logo_siges_sinletras),
                    contentDescription = "Logo de SIGES",
                    modifier           = Modifier.fillMaxSize(),
                    contentScale       = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("SIGES", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text("SISTEMA DE GESTIÓN DE ESPACIOS Y EQUIPOS", style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Preview
@Composable
fun LoginHeaderPreview(){
    SigesmobileTheme(darkTheme = true) {
        LoginHeader()
    }
}