package com.example.viewboard.frontend.components.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import colorFromCode

import generateProjectCodeFromDbId

@Composable
fun EdgeToEdgeRoundedRightItemWithBadge(
    viewName: String,
    modifier: Modifier = Modifier,
    projectId: String? = null,
    parentHorizontalPadding: Dp = 16.dp,
    boxHeight: Dp = 56.dp,

    ) {
    Card(
        shape = RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 0.dp,
            topEnd = 24.dp,
            bottomEnd = 24.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .offset(x = -parentHorizontalPadding)

            .padding(horizontal = 0.dp, vertical = 0.dp),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 30.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(boxHeight)
                .padding(top = 4.dp, bottom = 4.dp, end = 8.dp, start = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = viewName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )

            if (!projectId.isNullOrBlank()) {
                val tagCode = generateProjectCodeFromDbId(projectId)
                val tagColor = colorFromCode(tagCode)
                ProjectNameBadge(
                    text = tagCode,
                    backgroundColor = tagColor,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}