package com.example.klavier.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.klavier.R
import kotlin.reflect.KFunction1
import androidx.compose.ui.platform.LocalContext
import com.example.klavier.ui.theme.DarkerBlue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.klavier.ui.theme.LightestBlue

// Screen composable that displays the splash screen

@Composable
fun SplashScreen(
    connected: Boolean,
    hasPermission: Boolean,
    onGranted: () -> Unit,
    askPermission: KFunction1<Context, Unit>,
    modifier: Modifier = Modifier
)
{
    val context = LocalContext.current
    Box(modifier) {
        // image de fond
        Image(
            painter = painterResource(R.drawable.splashscreenimage2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
        )
            var value: String = ""
        // Gère l'affichage du texte en fonction de la connexion et de la permission
            if(!connected){
                value = stringResource(R.string.Connect_mc)
            }
            else(
                if(!hasPermission){
                    value = stringResource(R.string.Authorize_usb)
                }
                else{
                        value = stringResource(R.string.Charge)
                }
            )
                // dialogue demandant l'autorisation de bluetooth
                Card(modifier = Modifier
                    .align(Alignment.Center)
                    .width(320.dp)
                    ,shape = MaterialTheme.shapes.medium

                ) {
                    Column(
                        Modifier
                            .background(LightestBlue)
                            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(
                            text = value,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            color = DarkerBlue,
                            modifier = Modifier.padding(16.dp)
                        )
                        // Si la permission n'est pas accordée et que la connexion est active, affiche le bouton d'autorisation
                        AnimatedVisibility(
                            visible = !hasPermission && connected,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Button(
                                onClick = { askPermission(context) },
                                content = { Text(stringResource(R.string.Authorize), style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 16.dp),
                                colors = buttonColors(MaterialTheme.colorScheme.secondary, Color.White)
                            )
                        }

                    }
                }
    }
    // If hasPermission is true, get the main screen
    LaunchedEffect(hasPermission) {
        if(hasPermission) {
            onGranted()
        }
    }
}