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
    selectedView: String,
    onViewSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelRes: Int = R.string.my_tasks
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        TextField(
            value = selectedView,
            onValueChange = { /* readOnly */ },
            readOnly = true,
            label = {
                Text(
                    text = selectedView,
                    style = MaterialTheme.typography.titleMedium,
                    color =Color.Black //MaterialTheme.colorScheme.surfaceVariant
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.surfaceVariant
            ),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                unfocusedLabelColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
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
