package com.example.viewboard.frontend.components.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.draw.clip

import com.example.viewboard.backend.dataLayout.ViewLayout


@Composable
fun CustomDropdownMenu(
    options: List<ViewLayout>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier   
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = if (selectedOption.isNullOrBlank()) {
        "No Views"
    } else {
        selectedOption
    }

    Box(modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp,),
            contentAlignment = Alignment.CenterStart
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurface,
            ) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
        // Dropdown
        if(!selectedOption.isNullOrBlank()){
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.37f)
                .clip(MaterialTheme.shapes.small)
                .heightIn(max = 200.dp)
                .padding(horizontal = 12.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            containerColor = MaterialTheme.colorScheme.surfaceVariant

        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                    onClick = {
                        onOptionSelected(option.id)
                        expanded = false
                    }
                )
            } }
        }
    }
}
