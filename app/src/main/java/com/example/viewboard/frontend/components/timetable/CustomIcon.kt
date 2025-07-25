package com.example.viewboard.frontend.components.timetable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min

@Composable
fun CustomIcon(
    @DrawableRes iconRes: Int,
    contentDesc: String,
    backgroundColor: Color,
    iconTint: Color,
    width: Dp = 24.dp,
    height: Dp = 24.dp,
    cornerRadius: Dp = 4.dp,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .width(width)
            .height(height)
    ) {
        val iconSize = min(width, height) * 0.6f
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDesc,
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )
    }
}




