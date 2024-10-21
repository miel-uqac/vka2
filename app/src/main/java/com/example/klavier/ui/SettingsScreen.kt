package com.example.klavier.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackButtonClicked : () -> Unit = {},
)
{
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Claviers", "Thèmes", "Dispositions")

    Column()
    {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            IconButton(onClick = onBackButtonClicked) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "back"
                )
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
        }
        when (tabIndex) {
            0 -> ClaviersTab()
            1 -> ThemesTab()
            2 -> DispositionsTab()
        }
    }
}

@Composable
fun DispositionsTab() {
    Box(){ Text(text = "Dispositions") }
}

@Composable
fun ThemesTab() {
    Box(){ Text(text = "Thèmes") }
}

@Composable
fun ClaviersTab() {
    Box(){ Text(text = "Claviers") }
}
