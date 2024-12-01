package com.example.klavier.ui.components.macros

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.klavier.R

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
            // macro copier (macro présente par défaut)
            item{
                GridItem(
                    onClick = {
                        sendData(context.getString(R.string.id_copy))
                    },
                    painter = R.drawable.copy_icon,
                    contentDescription = "copier"
                )
            }
            // macro coller (macro présente par défaut)
            item{
                GridItem(
                    onClick = {
                        sendData(context.getString(R.string.id_paste))
                    },
                    painter = R.drawable.paste_icon,
                    contentDescription = "coller"
                )
            }
            // Ajout d'autres macros selon le dialogue de sélection
            // Pour chaque élément du tableau "macroLabels" on crée un nouveau bouton avec pour fonction et icone les éléments au même indice que le label dans leur tableau respectif
            items(macroLabels) {arrayItem ->
                GridItem(
                    onClick = macroFunctions[macroLabels.indexOf(arrayItem)],
                    painter = macroIcons[macroLabels.indexOf(arrayItem)],
                    contentDescription = arrayItem
                )
            }
        }
        // Bouton pour ouvrir le dialogue d'ajout de macros
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