package com.example.klavier.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KeyboardInput(
    sendData: (String) -> Unit,
    context: Context,
    onProvideFocusHandle: ((() -> Unit) -> Unit)? = null
) {
    var input by remember { mutableStateOf("") }
    val backspaceKeyCode = Key.Backspace.nativeKeyCode
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Permettre à l’écran parent de rappeler facilement le focus + IME
    LaunchedEffect(Unit) {
        onProvideFocusHandle?.invoke {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    TextField(
        value = input,
        onValueChange = { newText ->
            val addedText = if (newText.length > input.length) newText.substring(input.length) else ""

            // Détection de suppression
            if (newText.length < input.length) {
                repeat(input.length - newText.length) { sendData("#\$BACKSPACE") }
            }

            input = ""

            // Envoi des caractères ajoutés
            if (addedText.isNotEmpty()) {
                when (addedText) {
                    " "  -> sendData("#\$SPACE")
                    "\n" -> sendData("#\$ENTER")
                    "\t" -> sendData("#\$TAB")
                    else -> sendData(addedText)
                }
            }
        },
        modifier = Modifier
            .size(1.dp)
            .focusRequester(focusRequester)
            .onKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.keyCode == backspaceKeyCode && input.isEmpty()) {
                    sendData("#\$BACKSPACE")
                    true
                } else false
            },
        singleLine = false,
        textStyle = TextStyle(fontSize = 1.sp),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.None
        )
    )

    // Afficher le clavier au montage
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(modifier = Modifier) {}
}
