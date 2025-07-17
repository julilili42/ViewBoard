package com.example.viewboard.ui.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChipInputField(
    entries: List<String>,
    newEntry: String,
    inhaltText: String,
    suggestions: List<String> = emptyList(),                       // jetzt String-Liste
    onSuggestionClick: (String) -> Unit={},             // Callback mit String
    onNewEntryChange: (String) -> Unit={},
    onEntryConfirmed: () -> Unit={},
    onEntryRemove: (String) -> Unit={},
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = newEntry,
        onValueChange = onNewEntryChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onEntryConfirmed() }),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
            .padding(4.dp),
        decorationBox = { innerTextField ->
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1) Chips für bestehende Einträge
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    entries.forEach { entry ->
                        AssistChip(
                            onClick = { onEntryRemove(entry) },
                            label = { Text(entry) },
                            modifier = Modifier
                                .height(32.dp)
                                .padding(start = 4.dp, bottom = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 2) Eingabefeld mit Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (newEntry.isEmpty()) {
                        Text(
                            text = inhaltText,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp)
                    ) {
                        innerTextField()
                    }
                }

                // 3) Suggestion‑Liste als String-Array
                if (suggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                    ) {
                        suggestions.forEach { suggestion ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSuggestionClick(suggestion) }
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = suggestion,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
