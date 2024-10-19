package com.example.klavier.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackButtonClicked : () -> Unit = {},
)
{
    Scaffold(
        topBar = {
            TopAppBar(
                title = { "Settings" },
                modifier = modifier,
                actions = {
                    IconButton(onClick = onBackButtonClicked) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    ) {
                        Text("Claviers")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    ) {
                        Text("Thèmes")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    ) {
                        Text("Dispositions")
                    }
                }
            )
        }
    )
    {
            innerPadding -> { }
    }
}
