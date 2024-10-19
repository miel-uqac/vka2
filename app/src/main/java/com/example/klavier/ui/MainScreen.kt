package com.example.klavier.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import com.example.klavier.R

@Composable
fun MainScreen(
    onSettingsButtonClicked : () -> Unit = {}
)
{
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Macros", "ColorPicker")

    Column{
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ){
            Text(
                "Klavier",
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            IconButton(
                onClick = onSettingsButtonClicked,
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        }
        TabRow(
            selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        Row {
            when (tabIndex) {
                0 -> MacrosTab(onSettingsButtonClicked)
                1 -> ColorPickerTab()
            }
        }
    }
}

@Composable
fun MacrosTab(
    onSettingsButtonClicked: () -> Unit,
)
{
    Column{
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)){
            IconButton(
                onClick = onSettingsButtonClicked,
                modifier = Modifier,
            ) {
                Icon(
                    painter = painterResource(R.drawable.copy_icon), contentDescription = "copier"
                )
            }
            IconButton(
                onClick = onSettingsButtonClicked,
                modifier = Modifier,
            ) {
                Icon(
                    painter = painterResource(R.drawable.paste_icon), contentDescription = "copier"
                )
            }
        }
    }
}

@Composable
fun ColorPickerTab()
{
    Box()
    {
        Text(
            text = "color picker",
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}