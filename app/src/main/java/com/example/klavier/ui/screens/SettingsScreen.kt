package com.example.klavier.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.klavier.data.Layout
import com.example.klavier.ui.components.settings.ClaviersTab


@Composable
fun SettingsScreen(
    ChangeTheme: (Boolean) -> Unit,
    ChangeSensibility: (Float) -> Unit,
    sensibility: Float,
    SetLayout: (Layout) -> Unit,
    onBackButtonClicked: () -> Unit = {},
    isDarkTheme: Boolean,
    actualLayout: Layout
)
{


    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    )
    {
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(16.dp),horizontalArrangement = Arrangement.Center
        ){
            IconButton(onClick = onBackButtonClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Text(
                "Klavier",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
                )

        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp), // Espace autour du Divider
            thickness = 1.dp, // Épaisseur de la ligne
            color = Color.Gray // Couleur de la ligne
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            Text(
                text = "Paramètres"
                ,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterVertically)
            )
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
                ClaviersTab(SetLayout, actualLayout)

        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            ThemesTab(ChangeTheme, isDarkTheme)
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(16.dp),
            horizontalArrangement = Arrangement.Center){
            UpdateSensibility(sensibility, ChangeSensibility)
        }
    }
}

@Composable
fun UpdateSensibility(sensibility: Float, changeSensibility: (Float) -> Unit) {
    val newSensibility = remember { mutableFloatStateOf(sensibility) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Sensibilité de la souris : ${newSensibility.floatValue}",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Slider(
                value = newSensibility.floatValue,
                onValueChange = {
                    newSensibility.floatValue = it
                    changeSensibility(it)
                },
                valueRange = 0.5f..5f,
                steps = 44,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

    }
}


@Composable
fun ThemesTab(changeTheme: (Boolean) -> Unit, isDarkTheme: Boolean) {


    Box() {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = if (isDarkTheme) "Thème sombre activé" else "Thème clair activé",
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Switch(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically),
                checked = isDarkTheme,
                onCheckedChange = {
                    changeTheme(it)
                }
            )
        }
    }

}



