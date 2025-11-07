package com.example.klavier.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klavier.USBViewModel

@Composable
fun CustomKeyboard(
    vm: USBViewModel,
) {
    val buttonModifier = Modifier
        .padding(horizontal = 3.dp, vertical = 4.dp)
        .height(50.dp)

    val rowModifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 2.dp)

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp)) {

        // Ligne chiffres
        SimpleKeyRow(
            keys = listOf("1","2","3","4","5","6","7","8","9","0"),
            rowModifier = rowModifier, buttonModifier = buttonModifier
        ) { vm.writeUSB(it) }

        // Ligne qwerty
        SimpleKeyRow(
            keys = listOf("q","w","e","r","t","y","u","i","o","p"),
            rowModifier = rowModifier, buttonModifier = buttonModifier
        ) { vm.writeUSB(it) }

        // Ligne asdfghjkl
        SimpleKeyRow(
            keys = listOf("a","s","d","f","g","h","j","k","l"),
            rowModifier = rowModifier, buttonModifier = buttonModifier
        ) { vm.writeUSB(it) }

        // Ligne Shift + zxcvbnm + ⌫ + Suppr
        Row(rowModifier) {
            // SHIFT hold-only (DOWN / UP)
            ShiftHoldOnlyKey(
                send = vm::writeUSB,
                modifier = Modifier.weight(1f).then(buttonModifier)
            )

            // z x c v b n m
            listOf("z","x","c","v","b","n","m").forEach {
                KeyboardKey(
                    label = it,
                    modifier = Modifier.weight(1f).then(buttonModifier)
                ) { vm.writeUSB(it) }
            }

            // ⌫ (backspace)
            KeyboardKey(
                label = "⌫",
                modifier = Modifier.weight(1f).then(buttonModifier)
            ) { vm.writeUSB("#\$BACKSPACE") }

            // Suppr (forward delete)
            KeyboardKey(
                label = "Suppr",
                modifier = Modifier.weight(1f).then(buttonModifier)
            ) { vm.writeUSB("#\$DELETE") }
        }

        // Ligne ponctuation
        SimpleKeyRow(
            keys = listOf("'", ",", ".", "?", "!", ":", ";", "\"", "-", "/"),
            rowModifier = rowModifier, buttonModifier = buttonModifier
        ) { vm.writeUSB(it) }

        // Ligne symboles
        SimpleKeyRow(
            keys = listOf("(", ")", "[", "]", "{", "}", "@", "#", "$", "%"),
            rowModifier = rowModifier, buttonModifier = buttonModifier
        ) { vm.writeUSB(it) }

        // Tab / Espace / Entrée
        Row(rowModifier) {
            KeyboardKey("Tab",    Modifier.weight(2f).then(buttonModifier)) { vm.writeUSB("#\$TAB") }
            KeyboardKey("Espace", Modifier.weight(6f).then(buttonModifier)) { vm.writeUSB("#\$SPACE") }
            KeyboardKey("Entrée", Modifier.weight(2f).then(buttonModifier)) { vm.writeUSB("#\$ENTER") }
        }
    }
}

@Composable
private fun SimpleKeyRow(
    keys: List<String>,
    rowModifier: Modifier,
    buttonModifier: Modifier,
    onKeyPress: (String) -> Unit
) {
    Row(rowModifier) {
        keys.forEach {
            KeyboardKey(it, Modifier.weight(1f).then(buttonModifier)) {
                onKeyPress(it)
            }
        }
    }
}

@Composable
fun KeyboardKey(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE0E0E0),
            contentColor = Color.Black
        ),
        modifier = modifier
    ) { Text(text = label) }
}

/**
 * Touche Shift HOLD-ONLY pour le clavier custom :
 * - Appui    -> envoie "#$SHIFT_DOWN"
 * - Relâche  -> envoie "#$SHIFT_UP"
 * Aucun "#$K_SHIFT", aucun texte.
 */
@Composable
private fun ShiftHoldOnlyKey(
    send: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFECECF1),
        contentColor = Color.Black,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // SHIFT down immédiat
                        send("#\$SHIFT_DOWN")
                        try {
                            awaitRelease()
                        } finally {
                            // SHIFT up au relâchement
                            send("#\$SHIFT_UP")
                        }
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Shift", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}