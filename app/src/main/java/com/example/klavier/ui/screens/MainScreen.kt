package com.example.klavier.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import com.example.klavier.ui.components.ColorPickerTab
import com.example.klavier.ui.components.KeyboardInputWithImeLabel
import com.example.klavier.ui.components.TouchPad
import com.example.klavier.ui.components.macros.MacrosTab

// --------------------------- Composants locaux ---------------------------

@Composable
private fun HoldableArrowButton(
    label: String,
    name: String, // "ARROW_UP" | ...
    send: (String) -> Unit,
    modifier: Modifier = Modifier,
    bgColor: Color = Color(0xFF2F2F2F),          // ⬅️ nouveau
    contentColor: Color = Color.White,           // ⬅️ nouveau
    corner: Int = 28                             // ⬅️ nouveau (rayon arrondi)
) {
    val downTok = "#\$KEYDOWN:$name"
    val upTok   = "#\$KEYUP:$name"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(corner.dp))         // ⬅️ corner param
            .background(bgColor)                         // ⬅️ remplace la couleur en dur
            .padding(horizontal = 22.dp, vertical = 14.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        send(downTok)
                        try { awaitRelease() } finally { send(upTok) }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize = 15.sp,                // même taille que End / Shift
            fontWeight = FontWeight.Medium,  // même épaisseur
            color = contentColor
        )
    }
}


/**
 * Shift de la ligne des raccourcis — HOLD-ONLY :
 * - Appui  -> #$SHIFT_DOWN
 * - Relâche -> #$SHIFT_UP
 * Aucun #$K_SHIFT, aucun texte.
 */
@Composable
private fun ShiftShortcutHoldOnly(
    send: (String) -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),   // bien arrondi
        color = color,
        contentColor = Color.Black,
        modifier = modifier
            .height(54.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        send("#\$SHIFT_DOWN")
                        try { awaitRelease() } finally { send("#\$SHIFT_UP") }
                    }
                )
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("Shift", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// --------------------------- Écran principal ---------------------------

@Composable
fun MainScreen(
    sendData: (String) -> Unit,
    sensibility: Float,
    onSettingsButtonClicked: () -> Unit = {},
    macroLabels: ArrayList<String>,
    macroIcons: ArrayList<Int>,
    macroFunctions: ArrayList<() -> Unit>
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Macros", "ColorPicker", "Programmation")
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Titre + réglages
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Klavier",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            IconButton(
                onClick = onSettingsButtonClicked,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
            }
        }

        Divider(thickness = 1.dp, color = Color.Gray)

        // Onglets
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = Color(0xFFF2F2F2),
            contentColor = Color.Black
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title, fontSize = 13.sp) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (tabIndex) {
            // --- Onglet 0 : Macros + Touchpad court ---
            0 -> {
                MacrosTab(
                    sendData = sendData,
                    macroFunctions = macroFunctions,
                    macroIcons = macroIcons,
                    macroLabels = macroLabels
                )
                TouchPad(
                    sensibility = sensibility,
                    sendData = sendData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(top = 12.dp)
                )
            }

            // --- Onglet 1 : ColorPicker ---
            1 -> ColorPickerTab(sendData = sendData)

            // --- Onglet 2 : Programmation ---
            2 -> {
                val density = LocalDensity.current
                val imeVisible = WindowInsets.ime.getBottom(density) > 0
                val keyboardController = LocalSoftwareKeyboardController.current

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 8.dp)
                ) {
                    // ========== 1) FLECHES (maintenables) + TOUCHPAD ==========
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(0.9f)   // flèches légèrement moins larges
                                .padding(end = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HoldableArrowButton(
                                label = "↑", name = "ARROW_UP", send = sendData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)    // taille réduite mais toujours confortable
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                HoldableArrowButton(
                                    label = "←", name = "ARROW_LEFT", send = sendData,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(70.dp)
                                )
                                HoldableArrowButton(
                                    label = "→", name = "ARROW_RIGHT", send = sendData,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(70.dp)
                                )
                            }
                            HoldableArrowButton(
                                label = "↓", name = "ARROW_DOWN", send = sendData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                            )
                        }

                        TouchPad(
                            sensibility = sensibility,
                            sendData = sendData,
                            modifier = Modifier
                                .weight(1.1f)          // pavé tactile légèrement plus large
                                .height(230.dp)        // et un peu plus haut
                        )
                    }


                    Spacer(modifier = Modifier.height(10.dp))

                    // ========== 2) RACCOURCIS (avec Shift HOLD-ONLY arrondi) ==========
                    val rowColor1 = Color(0xFF90CAF9) // bleu clair
                    val rowColor2 = Color(0xFFA5D6A7) // vert clair
                    val rowColor3 = Color(0xFFFFAB91) // orange clair

                    // Ligne 1
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("Ctrl+C" to "CTRL+C", "Ctrl+V" to "CTRL+V", "Ctrl+A" to "CTRL+A").forEach { (label, code) ->
                            Button(
                                onClick = { sendData(code) },
                                modifier = Modifier.weight(1f).height(54.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = rowColor1,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) { Text(label, fontSize = 15.sp) }
                        }
                    }

                    // Ligne 2
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("Ctrl+Z" to "CTRL+Z", "Ctrl+Y" to "CTRL+Y", "Ctrl+S" to "CTRL+S").forEach { (label, code) ->
                            Button(
                                onClick = { sendData(code) },
                                modifier = Modifier.weight(1f).height(54.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = rowColor2,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) { Text(label, fontSize = 15.sp) }
                        }
                    }

                    // Ligne 3 : End / Tab / Shift
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // End (one-shot)
                        Button(
                            onClick = { sendData("#\$END") },
                            modifier = Modifier.weight(1f).height(54.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = rowColor3,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text("End", fontSize = 15.sp) }

                        // Tab : même style qu'un Button, mais maintenable (KEYDOWN/KEYUP)
                        HoldableArrowButton(
                            label = "Tab",
                            name = "TAB",
                            send = sendData,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .padding(0.dp),                 // optionnel
                            bgColor = rowColor3,                // ⬅️ même fond que End/Shift
                            contentColor = Color.Black,         // ⬅️ même texte
                            corner = 16                         // ⬅️ mêmes coins arrondis
                        )
                        // Shift (hold-only) — inchangé
                        ShiftShortcutHoldOnly(
                            send = sendData,
                            color = rowColor3,
                            modifier = Modifier.weight(1f)
                        )
                    }


                    Spacer(modifier = Modifier.height(10.dp))

                    // ========== 3) Champ IME invisible ==========
                    KeyboardInputWithImeLabel(
                        sendData = sendData,
                        context = context,
                        onProvideFocusHandle = { /* handle optionnel si tu veux un bouton pour ré-ouvrir l'IME */ }
                    )

                    if (!imeVisible) {
                        Button(
                            onClick = { keyboardController?.show() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE0E0E0),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text("Afficher le clavier") }
                    }
                }
            }
        }
    }
}
