package com.example.klavier.ui.components.macros

import androidx.annotation.DrawableRes
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

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
            // Quand un MacroChip est sélectionné les données de la macro associée sont ajoutées au tableau
            selected = !selected
            if (selected) {
                if (!macroLabels.contains(label)) {
                    macroLabels.add(label)
                    macroIcons.add(iconResource)
                    macroFunctions.add(onClick)
                }
            }
            // Quand le MacroChip est déselectionné les données de la macro associée sont retirées du tableau
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