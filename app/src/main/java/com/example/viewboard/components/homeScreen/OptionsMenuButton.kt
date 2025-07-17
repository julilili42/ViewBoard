import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * @param options Liste von Pair(Label, Aktion)
 */
@Composable
fun OptionsMenuButton(
    options: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.MoreVert
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = icon,
                contentDescription = "Options",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.14f))
        ) {
            options.forEach { (label, action) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.14f)),
                    onClick = {
                        expanded = false
                        action()
                    }
                )
            }
        }
    }
}
