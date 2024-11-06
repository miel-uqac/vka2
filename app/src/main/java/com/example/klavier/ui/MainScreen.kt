package com.example.klavier.ui


import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.example.klavier.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun MainScreen(
    sendData: (String) -> Unit,
    onSettingsButtonClicked : () -> Unit = {},
    macroLabels: ArrayList<String>,
    macroIcons: ArrayList<Int>,
    macroFunctions: ArrayList<() -> Unit>
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
                modifier = Modifier.align(Alignment.CenterVertically))

            IconButton(
                onClick = onSettingsButtonClicked,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings")
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
        Row (modifier  = Modifier.fillMaxWidth(1f)) {
            when (tabIndex) {
                0 -> MacrosTab(sendData = sendData, macroLabels, macroIcons, macroFunctions)
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
    sendData: (String) -> Unit,
    macroLabels: ArrayList<String>,
    macroIcons: ArrayList<Int>,
    macroFunctions: ArrayList<() -> Unit>
)
{
    val context = LocalContext.current
    Column{
        LazyRow(
            modifier = Modifier
                .height(50.dp)
        ) {
            // macro copier
            item{
                GridItem(
                    onClick = {
                        sendData(context.getString(R.string.id_copy))
                    },
                    painter = R.drawable.copy_icon,
                    contentDescription = "copier"
                )
            }
            // macro coller
            item{
                GridItem(
                    onClick = {
                        sendData(context.getString(R.string.id_paste))
                    },
                    painter = R.drawable.paste_icon,
                    contentDescription = "coller"
                )
            }
            items(macroLabels) {arrayItem ->
                GridItem(
                    onClick = macroFunctions[macroLabels.indexOf(arrayItem)],
                    painter = macroIcons[macroLabels.indexOf(arrayItem)],
                    contentDescription = arrayItem
                )
            }
        }
        Row(modifier = Modifier.align(Alignment.Start))
        {
            var showDialog by remember { mutableStateOf(false) }
            if (showDialog) {
                AddMacrosDialog(onDismissRequest = { showDialog = false }, onConfirmation = { showDialog = false }, context, sendData, macroLabels, macroIcons, macroFunctions)
            }
            IconButton(
                onClick = {
                    showDialog = true
                },
                modifier = Modifier,
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_icon), contentDescription = "ajouter une macro"
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
    val backspaceKeyCode = Key.Backspace.nativeKeyCode
    val backspace: String = """\b"""
    TextField(
            value = input,
            onValueChange = { newText ->

                val addedText =
                if (newText.isNotEmpty()) {
                    findFirstDifferenceIndex(newText, input).let {
                        if (it >= 0) {
                            for (i in it..input.length-1) {
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
                        ""
                    }

                input = if (addedText != ".") newText else ""

                if (addedText.isNotEmpty()) {
                    sendData(addedText)
                }

            },
            label = { Text("Label") },
            modifier = Modifier
                .onKeyEvent { keyEvent ->
                    if (keyEvent.nativeKeyEvent.keyCode == backspaceKeyCode && input.isEmpty()) {
                        sendData(backspace)
                        true
                    } else {
                        false
                    }
                }
                .fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(color = Color.Transparent) // Le texte est également invisible
        )

}


fun findFirstDifferenceIndex(str1: String, str2: String): Int {
    return str1.zip(str2).indexOfFirst { (char1, char2) -> char1 != char2 }
}

@Composable
fun AddMacrosDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    context : Context,
    sendData: (String) -> Unit,
    macroLabels : ArrayList<String>,
    macroIcons : ArrayList<Int>,
    macroFunctions : ArrayList<() -> Unit>
)
{
    Dialog(onDismissRequest = { onDismissRequest() })
    {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        )
        {
            Column (modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxHeight()
                .padding(top = 20.dp, bottom = 20.dp)
            ){
                Text(
                    text = "Selectionnez les macros à ajouter.",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp)
                )

                OutlinedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    modifier = Modifier
                        .size(width = 240.dp, height = 200.dp)
                )
                {
                    Column(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .verticalScroll(rememberScrollState())
                    ){
                        MacroChip(
                            "couper", R.drawable.cut_icon, {}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "tout selectionner", R.drawable.select_all_icon, {}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "undo", R.drawable.undo_icon, {}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "redo", R.drawable.redo_icon, {}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "chercher", R.drawable.find_icon, {}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "chercher et remplacer", R.drawable.find_replace_icon, {}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "imprimer", R.drawable.print_icon, {}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "gras", R.drawable.bold_icon, {}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "italique", R.drawable.italic_icon, {}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "souligner", R.drawable.underline_icon, {}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

                Row(modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                ){
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Ajouter")
                    }
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Annuler")
                    }
                }
            }
        }
    }
}

@Composable
fun MacroChip(
    label: String,
    @DrawableRes iconResource: Int,
    onClick: () -> Unit,
    macroLabels : ArrayList<String>,
    macroIcons : ArrayList<Int>,
    macroFunctions : ArrayList<() -> Unit>,
    modifier: Modifier
) {
    var selected by remember { mutableStateOf(false) }

    FilterChip(
        onClick = {
            selected = !selected
            if (selected) {
                if (!macroLabels.contains(label)) {
                    macroLabels.add(label)
                    macroIcons.add(iconResource)
                    macroFunctions.add(onClick)
                }
            }
            else {
                macroLabels.remove(label)
                macroIcons.remove(iconResource)
                macroFunctions.remove(onClick)
            }
        },
        label = {
            Icon(painter = painterResource(iconResource), contentDescription = label)
            Text(label)
        },
        selected = selected,
    )
}

@Composable
fun GridItem(
    onClick: () -> Unit,
    painter: Int,
    contentDescription: String
)
{
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(painter),
            contentDescription = contentDescription
        )
    }
}