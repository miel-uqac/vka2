package com.example.klavier.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.klavier.R
import com.example.klavier.ui.theme.DarkerBlue
import com.example.klavier.ui.theme.LightestBlue
import androidx.compose.ui.graphics.Color

@Composable
fun SplashScreen(
    connected: Boolean,
    hasPermission: Boolean,
    onGranted: () -> Unit,
    askPermission: (Context) -> Unit,
    onSkipDemo: (() -> Unit)? = null,         // <-- NOUVEAU : permet d'entrer en mode démo
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(modifier.fillMaxSize()) {
        // Image de fond
        Image(
            painter = painterResource(R.drawable.splashscreenimage2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
        )

        // Message d'état
        val message = when {
            !connected -> stringResource(R.string.Connect_mc)        // Ex: "Connectez le micro-contrôleur"
            !hasPermission -> stringResource(R.string.Authorize_usb) // Ex: "Autorisez l'accès USB"
            else -> stringResource(R.string.Charge)                  // Ex: "Chargement…"
        }

        // Carte centrale
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .width(320.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                Modifier
                    .background(LightestBlue)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = message,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = DarkerBlue,
                    modifier = Modifier.padding(8.dp)
                )

                // Bouton "Autoriser" visible seulement si connecté mais sans permission
                AnimatedVisibility(visible = connected && !hasPermission) {
                    Button(
                        onClick = { askPermission(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.Authorize),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                // Bouton "Continuer en mode démo" (toujours utile pour avancer)
                AnimatedVisibility(visible = onSkipDemo != null) {
                    Button(
                        onClick = { onSkipDemo?.invoke() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Continuer en mode démo",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }

    // Navigation automatique quand la permission est accordée (peu importe l'état précédent)
    LaunchedEffect(hasPermission) {
        if (hasPermission) onGranted()
    }
}
