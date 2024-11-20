package com.example.klavier.ui.components.macros

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.klavier.R


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
    var tempMacroLabels by remember { mutableStateOf(ArrayList<String>()) }
    var tempMacroIcons by remember { mutableStateOf(ArrayList<Int>()) }
    var tempMacroFunctions by remember { mutableStateOf(ArrayList<() -> Unit>()) }

    tempMacroLabels.addAll(macroLabels)
    tempMacroIcons.addAll(macroIcons)
    tempMacroFunctions.addAll(macroFunctions)

    Dialog(onDismissRequest = { onDismissRequest() })
    {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
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
                        .align(Alignment.CenterHorizontally)
                )
                {
                    Column(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .verticalScroll(rememberScrollState(),)
                    ){
                        MacroChip(
                            "couper", R.drawable.cut_icon, {sendData(context.getString(R.string.id_cut))}, tempMacroLabels, tempMacroIcons, tempMacroFunctions, Modifier.align(
                                Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "tout selectionner", R.drawable.select_all_icon, {sendData(context.getString(
                                R.string.id_select_all))}, tempMacroLabels, tempMacroIcons, tempMacroFunctions, Modifier.align(
                                Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "undo", R.drawable.undo_icon, {sendData(context.getString(R.string.id_undo))}, tempMacroLabels, tempMacroIcons, tempMacroFunctions, Modifier.align(
                                Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "redo", R.drawable.redo_icon, {sendData(context.getString(R.string.id_redo))}, tempMacroLabels, tempMacroIcons, tempMacroFunctions, Modifier.align(
                                Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "chercher", R.drawable.find_icon, {sendData(context.getString(R.string.id_search))}, tempMacroLabels, tempMacroIcons, tempMacroFunctions, Modifier.align(
                                Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "chercher et remplacer", R.drawable.find_replace_icon, {sendData(context.getString(
                                R.string.id_search_replace))}, tempMacroLabels, tempMacroIcons, tempMacroFunctions, Modifier.align(
                                Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "imprimer", R.drawable.print_icon, {sendData(context.getString(R.string.id_print))}, tempMacroLabels, tempMacroIcons, tempMacroFunctions, Modifier.align(
                                Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "gras", R.drawable.bold_icon, {sendData(context.getString(R.string.id_gras))}, tempMacroLabels, tempMacroIcons, tempMacroFunctions, Modifier.align(
                                Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "italique", R.drawable.italic_icon, {sendData(context.getString(R.string.id_italique))}, tempMacroLabels, tempMacroIcons, tempMacroFunctions, Modifier.align(
                                Alignment.CenterHorizontally)
                        )
                        MacroChip(
                            "souligner", R.drawable.underline_icon, {sendData(context.getString(R.string.id_souligne))}, tempMacroLabels, tempMacroIcons, tempMacroFunctions, Modifier.align(
                                Alignment.CenterHorizontally)
                        )
                    }
                }

                Row(modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                ){
                    TextButton(
                        onClick = {
                            macroLabels.clear()
                            macroIcons.clear()
                            macroFunctions.clear()

                            macroLabels.addAll(tempMacroLabels)
                            macroIcons.addAll(tempMacroIcons)
                            macroFunctions.addAll(tempMacroFunctions)

                            onConfirmation()
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Ajouter")
                    }
                    TextButton(
                        onClick = {
                            tempMacroLabels.clear()
                            tempMacroIcons.clear()
                            tempMacroFunctions.clear()

                            onDismissRequest()
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Annuler")
                    }
                }
            }
        }
    }
}