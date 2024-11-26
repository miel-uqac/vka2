package com.example.klavier.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.klavier.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.klavier.ui.components.ColorPickerTab
import com.example.klavier.ui.components.KeyboardInput
import com.example.klavier.ui.components.macros.MacrosTab
import com.example.klavier.ui.components.TouchPad


// Ecran principal
@Composable
fun MainScreen(
    sendData: (String) -> Unit,
    sensibility: Float,
    onSettingsButtonClicked: () -> Unit = {},
    macroLabels: ArrayList<String>,
    macroIcons: ArrayList<Int>,
    macroFunctions: ArrayList<() -> Unit>
)
{
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Macros", "ColorPicker")

    Column{
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),horizontalArrangement = Arrangement.Center
        ){
            Text(
                "Klavier",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )

            IconButton(
                onClick = onSettingsButtonClicked,
                Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings")
            }
        }
        HorizontalDivider(
            thickness = 1.dp, // Épaisseur de la ligne
            color = Color.Gray // Couleur de la ligne
        )
        // Permet de switch entre les macroses et le color picker
        TabRow(
            selectedTabIndex = tabIndex, containerColor = MaterialTheme.colorScheme.background, contentColor = MaterialTheme.colorScheme.onPrimary) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        Column (modifier  = Modifier.fillMaxSize(1f)) {
            when (tabIndex) {
                0 -> {
                    // Permet d'utiliser les macros
                    MacrosTab(
                        sendData = sendData,
                        macroFunctions = macroFunctions,
                        macroIcons = macroIcons,
                        macroLabels = macroLabels

                    )
                    // Affiche la zone de souris
                    TouchPad(sensibility = sensibility, sendData = sendData)
                }
                1 -> ColorPickerTab(sendData = sendData)
            }

        }
        Row (modifier = Modifier.align(Alignment.CenterHorizontally)
        ){
            // Affiche et gère le clavier
            KeyboardInput(sendData, LocalContext.current)
        }
    }
}











