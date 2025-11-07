package com.example.klavier.ui.components

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Champ invisible basé sur EditText, avec interception de l'InputConnection
 * pour capter Backspace (retour arrière) ET Forward Delete (Suppr) envoyés par l'IME.
 *
 * Envoi des tokens :
 *  - "#$ENTER"  sur Entrée (IME action ou '\n')
 *  - "#$TAB"    sur TAB
 *  - "#$SPACE"  sur espace
 *  - "#$BACKSPACE" sur retour arrière (geste ou deleteSurroundingText avant le curseur)
 *  - "#$DELETE"    sur Suppr (deleteSurroundingText après le curseur ou KEYCODE_FORWARD_DEL)
 *  - sinon : caractère brut.
 *
 * Expose un handle de focus via onProvideFocusHandle (optionnel).
 */
@Composable
fun KeyboardInputWithImeLabel(
    sendData: (String) -> Unit,
    context: Context,
    onProvideFocusHandle: ((() -> Unit) -> Unit)? = null,
    onEchoAction: ((EchoAction) -> Unit)? = null // optionnel si tu utilises un écho visuel
) {
    val composeContext = LocalContext.current

    // Garde une référence à l'EditText pour pouvoir rouvrir l’IME depuis l’extérieur
    val editRef = remember { mutableStateOf<EditText?>(null) }

    LaunchedEffect(editRef.value) {
        onProvideFocusHandle?.invoke {
            editRef.value?.let { et ->
                et.requestFocus()
                val imm = composeContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    AndroidView(
        modifier = Modifier.size(1.dp), // invisible mais focusable
        factory = { ctx ->

            // --- Sous-classe pour intercepter l’InputConnection ---
            class InterceptEditText(c: Context) : EditText(c) {
                override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
                    val base = super.onCreateInputConnection(outAttrs)

                    // Nous voulons un vrai mode texte multi-ligne (garder Entrée réelle)
                    outAttrs.imeOptions = EditorInfo.IME_ACTION_UNSPECIFIED
                    setImeActionLabel("Entrée", EditorInfo.IME_ACTION_UNSPECIFIED)

                    return object : InputConnectionWrapper(base, true) {

                        // deleteSurroundingText est la voie la plus fiable pour IME soft
                        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
                            // beforeLength > 0 -> Backspace (efface avant le curseur)
                            if (beforeLength > 0) {
                                sendData("#\$BACKSPACE")
                                onEchoAction?.invoke(EchoAction.Backspace)
                            }
                            // afterLength > 0 -> Forward Delete (efface après le curseur)
                            if (afterLength > 0) {
                                sendData("#\$DELETE")
                                // (en général pas d’écho visuel à faire ici)
                            }
                            // On laisse l’IME continuer son cycle
                            return super.deleteSurroundingText(beforeLength, afterLength)
                        }

                        // Certains claviers enverront quand même des KeyEvent
                        override fun sendKeyEvent(event: KeyEvent): Boolean {
                            if (event.action == KeyEvent.ACTION_DOWN) {
                                when (event.keyCode) {
                                    KeyEvent.KEYCODE_DEL -> { // Backspace hardware
                                        sendData("#\$BACKSPACE")
                                        onEchoAction?.invoke(EchoAction.Backspace)
                                        return true
                                    }
                                    KeyEvent.KEYCODE_FORWARD_DEL -> { // Suppr hardware
                                        sendData("#\$DELETE")
                                        return true
                                    }
                                    KeyEvent.KEYCODE_TAB -> {
                                        sendData("#\$TAB")
                                        return true
                                    }
                                    KeyEvent.KEYCODE_ENTER -> {
                                        sendData("#\$ENTER")
                                        onEchoAction?.invoke(EchoAction.Insert("\n"))
                                        return true
                                    }
                                }
                            }
                            return super.sendKeyEvent(event)
                        }
                    }
                }
            }

            InterceptEditText(ctx).apply {
                // Invisible mais focusable
                textSize = 1f
                setBackgroundColor(0x00000000) // transparent
                isCursorVisible = false

                // Multi-ligne (conserver Entrée réelle)
                isSingleLine = false
                maxLines = Int.MAX_VALUE
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE

                // Focus initial + show IME
                post {
                    requestFocus()
                    val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                }

                editRef.value = this

                // Watcher pour caractères tapés (ESPACE, ENTER, TAB, et caractères bruts)
                var old = ""
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        val newText = s?.toString() ?: ""

                        // Cas "suppression" visible par le watcher (rare ici car on vide le champ)
                        if (newText.length < old.length) {
                            repeat(old.length - newText.length) {
                                sendData("#\$BACKSPACE")
                                onEchoAction?.invoke(EchoAction.Backspace)
                            }
                            setText("")
                            setSelection(0)
                            old = ""
                            return
                        }

                        // Cas "ajouts"
                        if (newText.length > old.length) {
                            val added = newText.substring(old.length)
                            for (ch in added) {
                                when (ch) {
                                    ' '  -> sendData("#\$SPACE")
                                    '\n' -> {
                                        sendData("#\$ENTER")
                                        onEchoAction?.invoke(EchoAction.Insert("\n"))
                                    }
                                    '\t' -> sendData("#\$TAB")
                                    else -> {
                                        sendData(ch.toString())
                                        onEchoAction?.invoke(EchoAction.Insert(ch.toString()))
                                    }
                                }
                            }
                            // On vide pour rester invisible
                            setText("")
                            setSelection(0)
                            old = ""
                            return
                        }

                        old = newText
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                // Action IME “Entrée”
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                        sendData("#\$ENTER")
                        onEchoAction?.invoke(EchoAction.Insert("\n"))
                        true
                    } else {
                        false
                    }
                }

                // Accessibilité : n'agrandit pas l'UI
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
        },
        update = { /* no-op */ }
    )

    // Boîte vide pour respecter la signature Compose
    Box {}
}

/** Petites actions pour un éventuel panneau d’écho (facultatif). */
sealed class EchoAction {
    data class Insert(val text: String) : EchoAction()
    data object Backspace : EchoAction()
}
