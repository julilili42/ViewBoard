package com.example.viewboard.frontend.components.views

import OptionsMenuButton
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import colorFromCode
import com.example.viewboard.backend.dataLayout.ViewLayout
import generateProjectCodeFromDbId

/**
 * @param name is the view name
 * @param creator is the creator name
 * @param count is the issue count from the view
 * @param color is the color of the ui element
 * @param onClick is the click event of the ui element
 */
@Composable
fun ViewItem(
    view: ViewLayout,
    creator: String,
    color: Color,
    onClick: () -> Unit,
    onDelete: (String) -> Unit

) {
    val viewNameCode = generateProjectCodeFromDbId(view.name)
    val viewNameColor = colorFromCode(viewNameCode)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            viewNameColor,
                            viewNameColor.copy(alpha = 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OptionsMenuButton(
                options = listOf(
                    "Delete" to { Log.d("ViewItem", "Delete clicked for ${view.id}")
                        onDelete(view.id) }
                ),
                modifier = Modifier.align(Alignment.TopEnd),
                icon = Icons.Default.MoreVert
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = view.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

