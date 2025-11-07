package com.example.klavier

import java.text.Normalizer
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.klavier.data.USBController

class USBViewModel(private val usbController: USBController, context: Context) : ViewModel() {

    private var _hasPermission = MutableLiveData(false)
    val hasPermission: LiveData<Boolean> = _hasPermission

    private var _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected

    init {
        ManageUSB(context)
    }

    fun ManageUSB(context: Context) {
        usbController.ManageUSB(context)
        checkConnected()
        checkUSBPermission()
    }

    fun connectToDevice() {
        usbController.connectToDevice()
        checkConnected()
        checkUSBPermission()
    }

    fun disconnectUSB() {
        usbController.disconnectFromDevice()
        checkConnected()
        checkUSBPermission()
    }

    fun writeUSB(data: String) {
        val raw0 = data.trim()
        val raw = Normalizer.normalize(raw0, Normalizer.Form.NFC)
        val u = raw.uppercase()

        val alias = u
            .replace("KEYCODE_", "")
            .replace("KEY_", "")
            .replace("KEY ", "_")
            .replace("NUMPAD_", "")
            .trim()

        fun send(tok: String) {
            val out = if (tok.endsWith("\n")) tok else tok + "\n"
            usbController.USBWrite(out)
        }

        fun ctrlTok(letter: Char): String = when (letter) {
            'C' -> "#\$001"
            'V' -> "#\$002"
            'X' -> "#\$003"
            'Z' -> "#\$005"
            'Y' -> "#\$006"
            'S' -> "#\$007"
            'W' -> "#\$008"
            'P' -> "#\$009"
            'B' -> "#\$010"
            'I' -> "#\$011"
            'D' -> "#\$012"
            'A' -> "#\$013"
            else -> ""
        }

        // Pass-through des tokens du protocole (bien écrire "#\$...")
        if (raw.startsWith("#\$")) {
            send(raw)
            return
        }

        // Backspace icônes
        if (raw == "⌫" || raw == "␈" || raw == "←" || raw.equals("BACKSPACE_ICON", true)) {
            send("#\$BACKSPACE"); return
        }

        // × visuelle -> '*'
        if (raw == "×" || raw == "✕" || raw == "✖") {
            send("*"); return
        }

        val translated = when {
            // CTRL+lettre -> token macro
            alias.startsWith("CTRL+") || alias.startsWith("CONTROL+") -> {
                val ch = alias.removePrefix("CTRL+").removePrefix("CONTROL+").firstOrNull()
                val tok = ch?.let { ctrlTok(it) } ?: ""
                if (tok.isNotEmpty()) tok else raw
            }

            // Contrôles de base
            alias == "ENTER" || raw == "\n" -> "#\$ENTER"
            alias == "TAB" || raw == "\t" -> "#\$TAB"
            alias == "SPACE" || alias == "SPACEBAR" || raw == " " -> "#\$SPACE"

            // Suppressions
            // - Backspace (retour arrière)
            alias == "BACKSPACE" || alias == "DEL" || raw == "\u0008" -> "#\$BACKSPACE"
            // - Delete (Supprimer / Suppr) -> forward delete
            alias == "FORWARD_DEL" || alias == "DELETE" ||
                    alias == "SUPPRIMER" || alias == "SUPPR" ||
                    raw.equals("Suppr", true) -> "#\$DELETE"

            // Modifiers explicites
            alias == "SHIFT_DOWN" || raw == "SHIFT_DOWN" -> "#\$SHIFT_DOWN"
            alias == "SHIFT_UP"   || raw == "SHIFT_UP"   -> "#\$SHIFT_UP"
            alias == "CTRL_DOWN"  || raw == "CTRL_DOWN"  -> "#\$CTRL_DOWN"
            alias == "CTRL_UP"    || raw == "CTRL_UP"    -> "#\$CTRL_UP"
            alias == "ALT_DOWN"   || raw == "ALT_DOWN"   -> "#\$ALT_DOWN"
            alias == "ALT_UP"     || raw == "ALT_UP"     -> "#\$ALT_UP"
            alias == "META_DOWN"  || raw == "META_DOWN"  -> "#\$META_DOWN"
            alias == "META_UP"    || raw == "META_UP"    -> "#\$META_UP"

            // Shift tap (one-shot)
            alias == "SHIFT" -> "#\$K_SHIFT"

            // Flèches (one-shot)
            alias == "ARROW_UP" || alias == "DPAD_UP" || alias == "UP" -> "#\$ARROW_UP"
            alias == "ARROW_DOWN" || alias == "DPAD_DOWN" || alias == "DOWN" -> "#\$ARROW_DOWN"
            alias == "ARROW_LEFT" || alias == "DPAD_LEFT" || alias == "LEFT" -> "#\$ARROW_LEFT"
            alias == "ARROW_RIGHT" || alias == "DPAD_RIGHT" || alias == "RIGHT" -> "#\$ARROW_RIGHT"

            // Navigation
            alias == "END" || alias == "MOVE_END" -> "#\$END"
            alias == "HOME" || alias == "MOVE_HOME" -> "#\$HOME"
            alias == "PAGE_UP" || alias == "PGUP" || alias == "PAGEUP" -> "#\$PGUP"
            alias == "PAGE_DOWN" || alias == "PGDN" || alias == "PAGEDOWN" -> "#\$PGDN"

            // Layout switch passthrough
            raw == "#\$L0" || raw == "#\$L1" -> raw

            // Opérateurs & symboles
            alias == "MULTIPLY" || raw == "*" -> "*"
            alias == "DIVIDE"   || raw == "÷" -> "/"
            alias == "ADD" || alias == "PLUS" -> "+"
            alias == "SUBTRACT" || alias == "MINUS" -> "-"
            alias == "DOT" || alias == "PERIOD" -> "."
            alias == "COMMA" -> ","
            alias == "EQUALS" || raw == "=" -> "="
            alias == "QUOTE" || raw == "''" || raw == "“" || raw == "”" -> "\""
            alias == "APOSTROPHE" || raw == "’" || raw == "‘" -> "'"
            alias == "BACKSLASH" || raw == "¥" -> "\\"
            alias == "SLASH" || alias == "FORWARD_SLASH" -> "/"
            alias == "BRACKET_LEFT"  || raw == "[" -> "["
            alias == "BRACKET_RIGHT" || raw == "]" -> "]"
            alias == "BRACE_LEFT"    || raw == "{" -> "{"
            alias == "BRACE_RIGHT"   || raw == "}" -> "}"
            alias == "AT" || raw.equals("arobase", true) -> "@"
            alias == "GREATER" || raw == ">" -> ">"
            alias == "LESS"    || raw == "<" -> "<"
            alias == "COLON"   || raw == ":" -> ":"
            alias == "SEMICOLON" || raw == ";" -> ";"
            alias == "UNDERSCORE" || raw == "_" -> "_"
            alias == "PIPE" || raw == "|" -> "|"
            alias == "CARET" || raw == "^" -> "^"
            alias == "TILDE" || raw == "~" -> "~"
            alias == "BACKTICK" || raw == "`" -> "`"
            alias == "PERCENT" || raw == "%" -> "%"
            alias == "HASH" || raw == "#" -> "#"
            alias == "DOLLAR" || raw == "$" -> "$"
            alias == "AMPERSAND" || raw == "&" -> "&"
            alias == "EXCLAMATION" || raw == "!" -> "!"
            alias == "QUESTION" || raw == "?" -> "?"
            else -> raw
        }

        send(translated)
    }

    fun askPermission(context: Context) {
        usbController.askPermission(context)
    }

    private fun checkConnected() {
        _isConnected.value = usbController.USBConnected
    }

    private fun checkUSBPermission() {
        _hasPermission.value = usbController.hasDevicePermission
    }
}
