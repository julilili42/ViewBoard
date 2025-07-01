package com.example.viewboard.ui.account

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.viewboard.ui.theme.uiColor

/**
 * A reusable text field for login forms that shows a label and an optional trailing action.
 *
 * @param modifier   Optional [Modifier] for layout adjustments.
 * @param label      The label to display inside the text field (e.g., "Email" or "Password").
 * @param trailing   The text to show in the trailing icon button (e.g., "Forgot?"); pass an empty
 *                   string if no trailing action is needed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    trailing: String = ""
) {
    val uiColor = uiColor()

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = uiColor
            )
        },
        trailingIcon = {
            if (trailing.isNotEmpty()) {
                TextButton(onClick = { /* TODO: implement trailing action */ }) {
                    Text(
                        text = trailing,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = uiColor
                    )
                }
            }
        }
    )
}
