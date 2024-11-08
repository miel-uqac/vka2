package com.example.klavier.ui


import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.example.klavier.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.skydoves.colorpicker.compose.*
import kotlin.math.abs
import androidx.compose.ui.window.Dialog
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
        Column (modifier  = Modifier.fillMaxSize(1f)) {
            when (tabIndex) {
                0 -> {
                    MacrosTab(sendData = sendData)
                    TouchPad(sendData = sendData)
                }
                1 -> ColorPickerTab(sendData = sendData)
            }

        }
        Row (modifier = Modifier.align(Alignment.CenterHorizontally)
        ){
            keyboardInput(sendData)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        sendData = { /* Action de test pour sendData */ },
        onSettingsButtonClicked = { /* Action de test pour le bouton paramètres */ }
    )
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
fun ColorPickerTab(
    sendData: (String) -> Unit
)
{
    val controller = rememberColorPickerController()
    var hexCode by remember { mutableStateOf("") }
    var textColor by remember { mutableStateOf(Color.Transparent) }
    Row() {
        Column(
            modifier = Modifier
                .weight(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .height(180.dp)
            ) {
                HsvColorPicker(
                    modifier = Modifier.padding(10.dp),
                    controller = controller,
                    drawOnPosSelected = {
                        drawColorIndicator(
                            controller.selectedPoint.value,
                            controller.selectedColor.value,
                        )
                    },
                    onColorChanged = { colorEnvelope ->
                        hexCode = colorEnvelope.hexCode
                        textColor = colorEnvelope.color
                    },
                    initialColor = Color.Red,
                )
            }

            Spacer(modifier = Modifier.height(8.dp)) // Espacement entre les éléments

            AlphaSlider(
                modifier = Modifier
                    .testTag("HSV_AlphaSlider")
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(35.dp)
                    .align(Alignment.CenterHorizontally),
                controller = controller,
            )

            Spacer(modifier = Modifier.height(16.dp))

            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(35.dp),
                controller = controller,
            )
        }

        Column(
            modifier = Modifier
                .weight(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "#$hexCode",
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(16.dp))

            AlphaTile(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .align(Alignment.CenterHorizontally),
                controller = controller,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                    sendData("#$hexCode");
            }) {
                Text(text = "Envoyer")
            }

        }
    }
}


@Composable
fun keyboardInput(sendData: (String) -> Unit, modifier: Modifier = Modifier) {
    var input by remember { mutableStateOf("") }
    val backspaceKeyCode = Key.Backspace.nativeKeyCode
    val backspace: String = """\b"""
    val focusRequester = remember { FocusRequester() }
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

            modifier = Modifier.onKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.keyCode == backspaceKeyCode && input.isEmpty()) {
                    sendData(backspace)
                    true
                } else {
                    false
                }}
                .fillMaxWidth()
                .alpha(0f)
                .focusRequester(focusRequester),

            singleLine = true,
            textStyle = TextStyle(color = Color.Transparent), // Le texte est également invisible,


        )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

}


fun findFirstDifferenceIndex(str1: String, str2: String): Int {
    return str1.zip(str2).indexOfFirst { (char1, char2) -> char1 != char2 }
}

@Composable
fun TouchPad(
    sendData: (String) -> Unit
) {

    val context = LocalContext.current

    // PARAMETERS
    val deadZone = 2
    val sensibility = 1.2

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
            .background(Color.LightGray) // FOR DEBUG
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    var isHold = false
                    var firstPosition = Offset.Zero
                    var lastPosition = Offset.Zero
                    var firstHoldTime = 0L

                    while (true) {
                        val event = awaitPointerEvent()

                        //Détection de deux doigts  pour le scroll
                        if (event.changes.size == 2) {
                            val pointer1 = event.changes[0]
                            val pointer2 = event.changes[1]

                            if (pointer1.pressed && pointer2.pressed) {

                                val offsetDelta = (pointer1.position.y + pointer2.position.y) / 2 -
                                        (pointer1.previousPosition.y + pointer2.previousPosition.y) / 2

                                if (abs(offsetDelta) > deadZone) { //Creer un autre deadZone ?
                                    val scrollDirection = if (offsetDelta > 0) -1 else 1 //L'inverse suivant le sens de slide qu'on veut
                                    val scrollCommand = context.getString(R.string.id_mouse_scroll) + ":H:$scrollDirection"
                                    sendData(scrollCommand)
                                    isHold = false
                                }
                            }
                        } else {
                            event.changes.forEach { change ->
                                var stopEvent = false

                                // Détection du premier appui
                                if (!isHold && change.pressed) {
                                    // Premier maintien détecté
                                    firstPosition = change.position
                                    lastPosition = change.position
                                    isHold = true
                                    firstHoldTime = change.uptimeMillis
                                }
                                // Détection du relâchement
                                else if (isHold && !change.pressed) {
                                    val delta = firstPosition - lastPosition
                                    val deltaTime = abs(change.uptimeMillis - firstHoldTime)

                                    if (delta.x.toInt() == 0 && delta.y.toInt() == 0 && deltaTime <500) {
                                        // Clic simple : pas de déplacement détecté et click trop court pour être un maintient
                                        sendData(context.getString(R.string.id_click_gauche))
                                        stopEvent = true
                                        isHold = false

                                    } else if (abs(delta.x) <= deadZone && abs(delta.y) <= deadZone && !stopEvent) {
                                        // Clic maintenu = clic droit de souris
                                        sendData(context.getString(R.string.id_click_droit))
                                        stopEvent = true
                                        isHold = false

                                    } else {
                                        isHold = false
                                    }
                                }

                                // Déplacement pendant le maintien
                                if (isHold && change.pressed && !stopEvent) {
                                    val currentPosition = change.position
                                    val delta = currentPosition - lastPosition

                                    if (abs(delta.x) > deadZone || abs(delta.y) > deadZone) {
                                        // Déplacement significatif
                                        val moveCommand = context.getString(R.string.id_mouse_move) +
                                                ":${(sensibility*delta.x).toInt()}:${(sensibility*delta.y).toInt()}"
                                        sendData(moveCommand)
                                    }

                                    // Mettre à jour la dernière position pour le prochain déplacement
                                    lastPosition = currentPosition
                                }

                                // Consommer l'événement pour éviter qu'il ne soit traité plusieurs fois
                                change.consume()
                            }
                        }


                    }
                }
            }
    )


}

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
                            "couper", R.drawable.cut_icon, {sendData(context.getString(R.string.id_cut))}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "tout selectionner", R.drawable.select_all_icon, {sendData(context.getString(R.string.id_select_all))}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "undo", R.drawable.undo_icon, {sendData(context.getString(R.string.id_undo))}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "redo", R.drawable.redo_icon, {sendData(context.getString(R.string.id_redo))}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "chercher", R.drawable.find_icon, {sendData(context.getString(R.string.id_search))}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "chercher et remplacer", R.drawable.find_replace_icon, {sendData(context.getString(R.string.id_search_replace))}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "imprimer", R.drawable.print_icon, {sendData(context.getString(R.string.id_print))}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "gras", R.drawable.bold_icon, {sendData(context.getString(R.string.id_gras))}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "italique", R.drawable.italic_icon, {sendData(context.getString(R.string.id_italique))}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "souligner", R.drawable.underline_icon, {sendData(context.getString(R.string.id_souligne))}, macroLabels, macroIcons, macroFunctions, Modifier.align(Alignment.CenterHorizontally)
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
