package com.example.klavier.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.example.klavier.R
import com.example.klavier.USBController
import androidx.compose.ui.platform.LocalContext

@Composable
fun MainScreen(
    sendData: (String) -> Unit,
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
        Row (modifier  = Modifier.fillMaxSize(0.5f)) {
            when (tabIndex) {
                0 -> MacrosTab(sendData = sendData)
                1 -> ColorPickerTab()
            }

        }
        Row (modifier = Modifier.align(Alignment.CenterHorizontally)
        ){
            keyboardInput(sendData)
        }
    }
}

@Composable
fun MacrosTab(
    sendData: (String) -> Unit
)
{
    val context = LocalContext.current
    Column{
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)){
            IconButton(
                onClick = {
                    sendData(context.getString(R.string.id_copy))
                },
                modifier = Modifier,
            ) {
                Icon(
                    painter = painterResource(R.drawable.copy_icon), contentDescription = "copier"
                )
            }
            IconButton(
                onClick = {
                    sendData(context.getString(R.string.id_paste))
                },
                modifier = Modifier,
            ) {
                Icon(
                    painter = painterResource(R.drawable.paste_icon), contentDescription = "coller"
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


@Composable
fun keyboardInput(sendData: (String) -> Unit, modifier: Modifier = Modifier) {
    var input by remember { mutableStateOf("") }
    val backspace: String = """\b"""
    TextField(
            value = input,
            onValueChange = { newText ->

                val addedText =
                if (newText.isNotEmpty()) {
                    findFirstDifferenceIndex(newText, input)?.let {
                        if (it >= 0) {
                            for (i in 1..it) {
                                sendData(backspace)
                            }
                            newText.substring(it)
                        } else {
                            if(newText.length > input.length){
                                newText.substring(input.length)
                            }
                            else{
                                for (i in 1..input.length-newText.length) {
                                    sendData(backspace)
                                }
                                ""
                                }
                        }
                    }
                }else{
                        sendData(backspace)
                        "."
                    }

                input = if (addedText != ".") newText else ""

                if (addedText.isNotEmpty()) {
                    sendData(addedText)
                }

            },
            label = { Text("Label") },
            singleLine = true,
            textStyle = TextStyle(color = Color.Transparent) // Le texte est également invisible
        )

}


fun findFirstDifferenceIndex(str1: String, str2: String): Int {
    return str1.zip(str2).indexOfFirst { (char1, char2) -> char1 != char2 }
}