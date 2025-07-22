package com.example.viewboard.components.homeScreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.ViewLayout


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewSelectorDropdown(
    viewNames: List<String>,
    selectedView: String?,
    onViewSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    noViewsLabel: String = "No views"
) {
    var expanded by remember { mutableStateOf(false) }

    // Anzeige-Text: entweder der aktuell selektierte View oder der Default-Text
    val displayText = selectedView ?: noViewsLabel

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            // nur öffnen, wenn es Views gibt
            if (viewNames.isNotEmpty()) expanded = !expanded
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        TextField(
            value = displayText,
            onValueChange = { /* readOnly */ },
            readOnly = true,
            shape = RoundedCornerShape(4.dp),
            enabled = viewNames.isNotEmpty(),      // deaktiviert, wenn keine Views
            label = { Text(text = displayText, style = MaterialTheme.typography.titleMedium) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().height(40.dp),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor  = Color.Transparent,
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp),
        ) {
            if (viewNames.isEmpty()) {
                DropdownMenuItem(
                    text = { Text(noViewsLabel) },
                    onClick = { expanded = false }
                )
            } else {
                viewNames.forEach { name ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onViewSelected(name)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun CustomDropdownMenu(
    options: List<ViewLayout>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier   // einziges Style‑Argument
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = if (selectedOption.isNullOrBlank()) {
        "No Views"
    } else {
        selectedOption
    }

    Box(modifier) {
        // 1) Closed Box
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
              //  LocalContentAlpha provides ContentAlpha.high
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
        // 2) Dropdown
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
