package com.example.klavier.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.klavier.R
import kotlin.reflect.KFunction1
import androidx.compose.ui.platform.LocalContext

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
        Image(
            painter = painterResource(R.drawable.splashscreenimage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
        )
            var value: String = ""
            if(!connected){
                value = "Veuillez connecter le MC"
            }
            else(
                if(!hasPermission){
                    value = "Veuillez autoriser l'accès USB"
                }
                else{
                        value = "Chargement..."
                }
            )
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = value,
                        fontSize = 40.sp,
                        lineHeight = 116.sp,
                    )
                    AnimatedVisibility(visible = !hasPermission && connected, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Button(onClick = { askPermission(context) },
                            content = { Text("Autoriser") },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                }


    }
    LaunchedEffect(hasPermission) {
        if(hasPermission) {
            onGranted()
        }
    }
}