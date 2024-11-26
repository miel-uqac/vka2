package com.example.klavier.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.klavier.data.Layout

// Composant permettant de changer le layout du clavier
@Composable
fun ClaviersTab(setLayout: (Layout) -> Unit, layout: Layout) {
    val isDropDownExpanded = remember { mutableStateOf(false) }
    val layoutOptions = Layout.entries.toTypedArray()

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choissisez le clavier que votre ordinateur utilise:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        // Main dropdown button with centered text and arrow
        Box(
            Modifier
                .clickable { isDropDownExpanded.value = true }
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Layout: $layout",
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.width(8.dp)) // Space between text and arrow
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown arrow",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            // Dropdown menu qui contient les options de layout
            DropdownMenu(
                expanded = isDropDownExpanded.value,
                onDismissRequest = { isDropDownExpanded.value = false },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Full width for centered items
            ) {
                layoutOptions.forEach { option ->
                    DropdownMenuItem(
                        modifier = Modifier.padding(1.dp), // Full width for alignment
                        text = {
                            Text(
                                text = option.toString(),
                                modifier = Modifier.fillMaxWidth(), // Full width to ensure centering
                                textAlign = TextAlign.Center, // Center the text within the item
                                style = MaterialTheme.typography.bodyMedium

                            )
                        },
                        onClick = {
                            isDropDownExpanded.value = false
                            setLayout(option)
                        }
                    )
                }
            }
        }


    }
}