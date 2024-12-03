package com.example.klavier.ui.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import com.example.klavier.R


// composant qui gère l'entrée du clavier
@Composable
fun KeyboardInput(sendData: (String) -> Unit, context : Context,) {
    var input by remember { mutableStateOf("") }
    val backspaceKeyCode = Key.Backspace.nativeKeyCode
    val backspace = """\b"""
    val focusRequester = remember { FocusRequester() }
    TextField(
        value = input,
        //Lorsque que la valeur de l'input change, on regarde ce qui doit être envoyé au microcontrôleur
        onValueChange = { newText ->

            val addedText =
                if (newText.isNotEmpty()) {
                    findFirstDifferenceIndex(newText, input).let {
                        // on regarde si l'ancien input a gardé la même base que le nouveau
                        if (it >= 0) {
                            // it correspond à la position du premier caractère qui a changé
                            // Si des caractères ont changé, on supprime les charactères modifiés
                            for (i in it..input.length-1) {
                                sendData(backspace)
                            }
                            // Et on réecrit par dessus les nouveaux caractères
                            newText.substring(it)
                        } else {
                            // si ils partagent la même base
                            if(newText.length > input.length){
                                // Si on a ajouté des caractères, on les envoie
                                newText.substring(input.length)
                            }
                            else{
                                // Si on a supprimé des caractères, on les supprime sur l'ordinateur
                                for (i in 1..input.length-newText.length) {
                                    sendData(backspace)
                                }
                                ""
                            }
                        }
                    }
                }else{
                    // Si la valeur de l'input est vide, on supprime le dernier caractère sur l'ordinateur
                    sendData(backspace)
                    ""
                }

            // Si le dernier caractère est un point, on vide l'input
            input = if (addedText != ".") newText else ""

            // Si l'input n'est pas vide, on envoie le texte au microcontrôleur
            if (addedText.isNotEmpty()) {
                sendData(addedText)
            }

        },
        label = { Text("Label") },

        modifier = Modifier
            .onKeyEvent { keyEvent ->
                // Si l'utilisateur appuie sur le bouton backspace, on supprime le dernier caractère
                if (keyEvent.nativeKeyEvent.keyCode == backspaceKeyCode && input.isEmpty()) {
                    sendData(backspace)
                    true
                } else {
                    false
                }
            }
            .fillMaxWidth()
            .alpha(0f)
            .focusRequester(focusRequester),

        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done // Action Key
        ),
        // Si on appuie sur le bouton enter, la touche enter au microcontrôleur
        keyboardActions = KeyboardActions(
            onDone = {
                input = ""
                sendData(context.getString(R.string.id_enter))
            }
        ),
        // on rend l'input invisible
        textStyle = TextStyle(color = Color.Transparent), // Le texte est également invisible,


    )
    // On s'assure de garder le focus sur l'input
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

}

// Fonction qui cherche la première différence entre deux chaînes de caractères
fun findFirstDifferenceIndex(str1: String, str2: String): Int {
    return str1.zip(str2).indexOfFirst { (char1, char2) -> char1 != char2 }
}