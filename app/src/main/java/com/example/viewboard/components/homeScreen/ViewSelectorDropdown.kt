package com.example.viewboard.components.homeScreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.viewboard.R


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
            enabled = viewNames.isNotEmpty(),      // deaktiviert, wenn keine Views
            label = { Text(text = displayText, style = MaterialTheme.typography.titleMedium) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
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
