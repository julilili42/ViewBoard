package com.example.viewboard.frontend.components.issue

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
import com.example.viewboard.backend.dataLayout.SortOptionsIssues
import com.example.viewboard.frontend.stateholder.IssueViewModel
import com.example.viewboard.frontend.stateholder.IssueViewModel.SortField
import com.example.viewboard.frontend.stateholder.IssueViewModel.SortOrder

@Composable
public fun IssueSortMenuSimple(
    issueViewModel: IssueViewModel,
    options: List<SortOptionsIssues>,
    @DrawableRes iconRes: Int,
    contentDesc: String,
    modifier: Modifier = Modifier,
    width: Dp = 40.dp,
    height: Dp = 40.dp,
    backgroundColor: Color = Color.LightGray,
    iconTint: Color = Color.Black,
    cornerRadius: Dp = 4.dp,
    arrowSize: Dp = 20.dp,
    @DrawableRes arrowUpRes: Int = R.drawable.ic_arrow_upward,
    @DrawableRes arrowDownRes: Int = R.drawable.ic_arrow_downward
) {
    val currentField by issueViewModel.sortField.collectAsState()
    val currentOrder by issueViewModel.sortOrder.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val displayOptions: List<Triple<String, Int?, SortField>> = options.map { opt ->
        val arrowIcon = when {
            currentField == opt.field && currentOrder == SortOrder.ASC -> arrowUpRes
            currentField == opt.field && currentOrder == SortOrder.DESC -> arrowDownRes
            else -> null
        }
        Triple(opt.label, arrowIcon, opt.field)
    }
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
            displayOptions.forEach { (label, iconResOpt, field) ->
                DropdownMenuItem(
                    text = { Text(label, fontSize = 16.sp) },
                    trailingIcon = iconResOpt?.let { res ->
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
                        if (currentField == field) {
                            issueViewModel.setSortOrder(
                                if (currentOrder == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC
                            )
                        } else {
                            issueViewModel.setSortField(field)
                            issueViewModel.setSortOrder(SortOrder.ASC)
                        }
                    }
                )
            }
        }
    }
}