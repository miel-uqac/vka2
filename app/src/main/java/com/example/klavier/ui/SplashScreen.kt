package com.example.klavier.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.klavier.R

@Composable
fun SplashScreen(
    connected : Boolean,
    hasPermission : Boolean,
    onGranted : () -> Unit,
    modifier: Modifier = Modifier
)
{
    var accessGranted by remember { mutableStateOf(false) }

    Box(modifier) {
        Image(
            painter = painterResource(R.drawable.splashscreenimage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.align(Alignment.Center)
                                .fillMaxSize()
        )
            var value = ""
            if(!connected){
                value = "Veuillez connecter le MC"
            }
            else(
                if(!hasPermission){
                    value = "Veuillez autoriser l'accès USB"
                }
                else{
                    if (!accessGranted) {
                        accessGranted = true
                        onGranted()
                        Log.i("USB", "USB permission granted")
                    }
                    else{
                        value = "Chargement..."
                    }
                }
            )

                Text(text=value,
                    fontSize = 40.sp,
                    lineHeight = 116.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
    }
}