package com.example.viewboard.ui.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search…",
    textStyle: TextStyle = TextStyle(
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 16.sp
    ),
    placeholderStyle: TextStyle = textStyle.copy(
        color = textStyle.color.copy(alpha = 0.4f)
    ),
    leadingIcon: @Composable (() -> Unit)? = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    },
    trailingIcon: @Composable (() -> Unit)? = null,
    backgroundColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    borderColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    borderWidth: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(28.dp),
    cursorColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    suggestionContent: @Composable (String) -> Unit = {}
) {
    Column(modifier = modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .border(borderWidth, borderColor, shape)
                .background(backgroundColor, shape)
                .padding(contentPadding),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                cursorBrush = SolidColor(cursorColor),
                textStyle = textStyle,
                decorationBox = { inner ->
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (leadingIcon != null) {
                            leadingIcon()
                            Spacer(Modifier.width(8.dp))
                        }
                        if (query.isEmpty()) {
                            Text(placeholder, style = placeholderStyle)
                        }
                        inner()
                        Spacer(Modifier.weight(1f))
                        if (trailingIcon != null) {
                            Spacer(Modifier.width(8.dp))
                            trailingIcon()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        suggestionContent(query)
    }
}
