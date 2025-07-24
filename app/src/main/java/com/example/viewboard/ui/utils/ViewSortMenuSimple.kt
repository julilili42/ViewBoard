package com.example.viewboard.ui.utils

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewboard.R
import com.example.viewboard.stateholder.ViewsViewModel

@Composable
fun ViewSortMenuSimple(
    viewViewModel: ViewsViewModel,
    @DrawableRes iconRes: Int,
    contentDesc: String,
    modifier: Modifier = Modifier,
    width: Dp = 40.dp,
    height: Dp = 40.dp,
    backgroundColor: Color = Color.Gray,
    iconTint: Color = Color.White,
    cornerRadius: Dp = 4.dp,
    arrowSize: Dp = 20.dp
) {
    val currentField by viewViewModel.sortField.collectAsState()
    val currentOrder by viewViewModel.sortOrder.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        "Sort by Date" to (
                if (currentField == ViewsViewModel.SortField.CREATED)
                    if (currentOrder == ViewsViewModel.SortOrder.ASC)
                        R.drawable.ic_arrow_upward
                    else
                        R.drawable.ic_arrow_downward
                else null
                ),
        "Sort by Name" to (
                if (currentField == ViewsViewModel.SortField.NAME)
                    if (currentOrder == ViewsViewModel.SortOrder.ASC)
                        R.drawable.ic_arrow_upward
                    else
                        R.drawable.ic_arrow_downward
                else null
                )
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable { expanded = true }
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDesc,
            tint = iconTint,
            modifier = Modifier
                .width(width * 0.6f)
                .height(height * 0.6f)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (label, optIcon) ->
                DropdownMenuItem(
                    text = {
                        Text(label, fontSize = 16.sp)
                    },
                    trailingIcon = optIcon?.let { res ->
                        {
                            Icon(
                                painter = painterResource(res),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(arrowSize)
                                    .height(arrowSize)
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        when (label) {
                            "Sort by Date" -> {
                                if (currentField == ViewsViewModel.SortField.CREATED) {
                                    viewViewModel.setSortOrder(
                                        if (currentOrder == ViewsViewModel.SortOrder.ASC)
                                            ViewsViewModel.SortOrder.DESC
                                        else
                                            ViewsViewModel.SortOrder.ASC
                                    )
                                } else {
                                    viewViewModel.setSortField(ViewsViewModel.SortField.CREATED)
                                    viewViewModel.setSortOrder(ViewsViewModel.SortOrder.ASC)
                                }
                            }
                            "Sort by Name" -> {
                                if (currentField == ViewsViewModel.SortField.NAME) {
                                    viewViewModel.setSortOrder(
                                        if (currentOrder == ViewsViewModel.SortOrder.ASC)
                                            ViewsViewModel.SortOrder.DESC
                                        else
                                            ViewsViewModel.SortOrder.ASC
                                    )
                                } else {
                                    viewViewModel.setSortField(ViewsViewModel.SortField.NAME)
                                    viewViewModel.setSortOrder(ViewsViewModel.SortOrder.ASC)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

