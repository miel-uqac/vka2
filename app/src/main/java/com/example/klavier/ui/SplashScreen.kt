package com.example.klavier.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.klavier.R

@Composable
fun SplashScreen(
    onContinueClicked : () -> Unit,
    modifier: Modifier = Modifier
)
{
    Box(modifier) {
        Image(
            painter = painterResource(R.drawable.splashscreenimage),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )
        Button(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = onContinueClicked
        ) {
            Text("Continuer")
        }
    }
}