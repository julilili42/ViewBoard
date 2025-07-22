package com.example.viewboard.ui.timetable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.example.viewboard.ui.issue.ProjectViewModel
import com.example.viewboard.R
import com.example.viewboard.ui.issue.IssueViewModel
import com.example.viewboard.ui.issue.IssueViewModel.SortField
import com.example.viewboard.ui.issue.IssueViewModel.SortOrder
import com.example.viewboard.ui.issue.ViewsViewModel

@Composable
fun CustomIcon(
    @DrawableRes iconRes: Int,
    contentDesc: String,
    backgroundColor: Color,
    iconTint: Color,
    width: Dp = 24.dp,              // statt size
    height: Dp = 24.dp,             // statt size
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
        // Icon auf 60% der kleineren Dimension skalieren
        val iconSize = min(width, height) * 0.6f
        Icon(
            painter           = painterResource(id = iconRes),
            contentDescription= contentDesc,
            tint              = iconTint,
            modifier          = Modifier.size(iconSize)
        )
    }
}

@Composable
fun CustomIcon2(
    iconRes: ImageVector,
    contentDesc: String,
    backgroundColor: Color,
    iconTint: Color,
    width: Dp = 24.dp,              // statt size
    height: Dp = 24.dp,             // statt size
    cornerRadius: Dp = 4.dp,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable(onClick = onClick)
    ) {
        // Icon auf 60% der kleineren Dimension skalieren
        val iconSize = min(width, height) * 0.6f
        Icon(
            imageVector = iconRes,
            contentDescription = contentDesc,
            tint              = iconTint,
            modifier          = Modifier.size(iconSize)
        )
    }
}
/**
 * Ein CustomIcon mit eingebautem Dropdown‑Menu.
 *
 * @param options Liste von Pair(Label, Aktion)
 * @param iconRes Resource‑ID des Icons
 * @param contentDesc Content‑Description für Accessibility
 * @param backgroundColor Hintergrundfarbe des Icons
 * @param iconTint Tint‑Farbe des Icons
 * @param width Breite des Icon‑Buttons
 * @param height Höhe des Icon‑Buttons
 * @param cornerRadius Rundung des Button‑Hintergrunds
 * @param modifier Zusätzlicher Modifier
 */
@Composable
fun CustomIconMenu(
    options: List<Pair<String, () -> Unit>>,
    @DrawableRes iconRes: Int,
    contentDesc: String,
    backgroundColor: Color,
    iconTint: Color,
    width: Dp = 24.dp,
    height: Dp = 24.dp,
    cornerRadius: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable { expanded = true }
    ) {
        // 1) Das Icon selbst
        val iconSize = minOf(width, height) * 0.6f
        Icon(
            painter           = painterResource(id = iconRes),
            contentDescription= contentDesc,
            tint              = iconTint,
            modifier          = Modifier
                .width(iconSize)
                .height(iconSize)
        )

        // 2) Das Dropdown‑Menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (label, action) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        expanded = false
                        action()
                    }
                )
            }
        }
    }
}

@Composable
fun ProjectSortMenu(
    options: List<Pair<ProjectViewModel.SortField, ProjectViewModel.SortOrder>>,
    currentField: ProjectViewModel.SortField,
    currentOrder: ProjectViewModel.SortOrder,
    onSort: (ProjectViewModel.SortField, ProjectViewModel.SortOrder) -> Unit,
    // restliche Params wie bei CustomIconMenu:
    @DrawableRes iconRes: Int,
    contentDesc: String,
    backgroundColor: Color,
    iconTint: Color,
    width: Dp = 40.dp,
    height: Dp = 40.dp,
    cornerRadius: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    // Wir wandeln die Tupel in Label/Action-Paare um
    val menuItems = options.map { (field, order) ->
        val fieldName = when(field) {
            ProjectViewModel.SortField.DATE -> "Date"
            ProjectViewModel.SortField.NAME       -> "Name"
        }
        val arrow = if (order == ProjectViewModel.SortOrder.ASC) "↑" else "↓"
        // Label: "Sort by Date ↑" bzw. "Sort by Name ↓"
        "$fieldName $arrow" to { onSort(field, order) }
    }
    // Jetzt nutzen wir dein CustomIconMenu
    CustomIconMenu(
        options = menuItems,
        iconRes         = iconRes,
        contentDesc     = contentDesc,
        backgroundColor = backgroundColor,
        iconTint        = iconTint,
        width           = width,
        height          = height,
        cornerRadius    = cornerRadius,
        modifier        = modifier
    )
}

@Composable
fun ProjectSortCycler(
    projectViewModel: ProjectViewModel,
    modifier: Modifier = Modifier
) {
    // 1) Aktuellen Sort-Status holen
    val field by projectViewModel.sortField.collectAsState()
    val order by projectViewModel.sortOrder.collectAsState()

    // 2) Zyklus: Date↑ → Date↓ → Name↑ → Name↓
    val modes = listOf(
        ProjectViewModel.SortField.DATE to ProjectViewModel.SortOrder.ASC,
        ProjectViewModel.SortField.DATE to ProjectViewModel.SortOrder.DESC,
        ProjectViewModel.SortField.NAME       to ProjectViewModel.SortOrder.ASC,
        ProjectViewModel.SortField.NAME       to ProjectViewModel.SortOrder.DESC
    )

    // 3) Finde aktuelle Position im Zyklus (default auf 0)
    val currentIndex = modes.indexOfFirst { it.first == field && it.second == order }
        .takeIf { it >= 0 } ?: 0

    // 4) Nächstes Paar aus dem Zyklus
    val (nextField, nextOrder) = modes[(currentIndex + 1) % modes.size]

    // 5) Icon auswählen (nur Pfeile, egal ob Name oder Date)
    //    Du kannst hier auch unterschiedliche Icons für Name nehmen, wenn Du magst.
    val iconRes = if (order == ProjectViewModel.SortOrder.ASC)
        R.drawable.ic_arrow_upward
    else
        R.drawable.ic_arrow_downward

    CustomIcon(
        iconRes         = iconRes,
        contentDesc     = when(field) {
            ProjectViewModel.SortField.DATE -> "Sort by Date"
            ProjectViewModel.SortField.NAME       -> "Sort by Name"
        },
        backgroundColor = MaterialTheme.colorScheme.primary,
        iconTint        = Color.White,
        width           = 36.dp,
        height          = 36.dp,
        onClick         = {
            // auf Klick wechsle direkt auf den nächsten Modus
            projectViewModel.setSortField(nextField)
            projectViewModel.setSortOrder(nextOrder)
        },
        modifier        = modifier
    )
}

// 1) Definiere oben in der Datei
public data class SortOption(
    val label: String,
    @DrawableRes val iconRes: Int,
    val action: () -> Unit
)
public data class SortOptions(
    val label: String,
    val field: ProjectViewModel.SortField
)
public data class SortOptions2(
    val label: String,
    val field: IssueViewModel.SortField
)
@Composable
fun ProjectSortMenuSimple(
    projectViewModel: ProjectViewModel ,
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
    // 1) Aktueller Sort-Status
    val currentField by projectViewModel.sortField.collectAsState()
    val currentOrder by projectViewModel.sortOrder.collectAsState()

    // 2) Optionen mit optionalen Pfeil-Icons
    val options = listOf(
        "Sort by Date" to (
                if (currentField == ProjectViewModel.SortField.DATE)
                    if (currentOrder == ProjectViewModel.SortOrder.ASC)
                        R.drawable.ic_arrow_upward
                    else
                        R.drawable.ic_arrow_downward
                else null
                ),
        "Sort by Name" to (
                if (currentField == ProjectViewModel.SortField.NAME)
                    if (currentOrder == ProjectViewModel.SortOrder.ASC)
                        R.drawable.ic_arrow_upward
                    else
                        R.drawable.ic_arrow_downward
                else null
                )
    )

    var expanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable { expanded = true }
    ) {
        // 3) Haupt-Icon
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDesc,
            tint = iconTint,
            modifier = Modifier
                .width(width * 0.6f)
                .height(height * 0.6f)
        )

        // 4) Dropdown-Menü mit trailingIcon
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
                                if (currentField == ProjectViewModel.SortField.DATE) {
                                    projectViewModel.setSortOrder(
                                        if (currentOrder == ProjectViewModel.SortOrder.ASC)
                                            ProjectViewModel.SortOrder.DESC
                                        else
                                            ProjectViewModel.SortOrder.ASC
                                    )
                                } else {
                                    projectViewModel.setSortField(ProjectViewModel.SortField.DATE)
                                    projectViewModel.setSortOrder(ProjectViewModel.SortOrder.ASC)
                                }
                            }
                            "Sort by Name" -> {
                                if (currentField == ProjectViewModel.SortField.NAME) {
                                    projectViewModel.setSortOrder(
                                        if (currentOrder == ProjectViewModel.SortOrder.ASC)
                                            ProjectViewModel.SortOrder.DESC
                                        else
                                            ProjectViewModel.SortOrder.ASC
                                    )
                                } else {
                                    projectViewModel.setSortField(ProjectViewModel.SortField.NAME)
                                    projectViewModel.setSortOrder(ProjectViewModel.SortOrder.ASC)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}


@Composable
public fun ProjectSortMenuSimple(
    projectViewModel: ProjectViewModel,
    options: List<SortOptions>,
    @DrawableRes iconRes: Int,
    contentDesc: String,
    modifier: Modifier = Modifier,
    width: Dp = 40.dp,
    height: Dp = 40.dp,
    backgroundColor: Color = Color.Gray,
    iconTint: Color = Color.White,
    cornerRadius: Dp = 4.dp,
    arrowSize: Dp = 20.dp,
    @DrawableRes arrowUpRes: Int = R.drawable.ic_arrow_upward,
    @DrawableRes arrowDownRes: Int = R.drawable.ic_arrow_downward
) {
    // Current sort state
    val currentField by projectViewModel.sortField.collectAsState()
    val currentOrder by projectViewModel.sortOrder.collectAsState()

    // Build options with optional arrow icon: List of Triple(label, iconRes, field)
    val displayOptions: List<Triple<String, Int?, ProjectViewModel.SortField>> = options.map { opt ->
        val arrowIcon = when {
            currentField == opt.field && currentOrder == ProjectViewModel.SortOrder.ASC -> arrowUpRes
            currentField == opt.field && currentOrder == ProjectViewModel.SortOrder.DESC -> arrowDownRes
            else -> null
        }
        Triple(opt.label, arrowIcon, opt.field)
    }

    var expanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable { expanded = true }
    ) {
        // Main icon
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDesc,
            tint = iconTint,
            modifier = Modifier
                .width(width * 0.6f)
                .height(height * 0.6f)
        )

        // Dropdown menu
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
                            // toggle order
                            projectViewModel.setSortOrder(
                                if (currentOrder == ProjectViewModel.SortOrder.ASC)
                                    ProjectViewModel.SortOrder.DESC
                                else
                                    ProjectViewModel.SortOrder.ASC
                            )
                        } else {
                            projectViewModel.setSortField(field)
                            projectViewModel.setSortOrder(ProjectViewModel.SortOrder.ASC)
                        }
                    }
                )
            }
        }
    }
}



@Composable
public fun IssueSortMenuSimple(
    issueViewModel: IssueViewModel,
    options: List<SortOptions2>,
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
    // Aktueller Sortierzustand
    val currentField by issueViewModel.sortField.collectAsState()
    val currentOrder by issueViewModel.sortOrder.collectAsState()

    // Nur für Issues: Optionen mit optionalem Pfeil-Icon aufbauen
    val displayOptions: List<Triple<String, Int?, SortField>> = options.map { opt ->
        val arrowIcon = when {
            currentField == opt.field && currentOrder == SortOrder.ASC -> arrowUpRes
            currentField == opt.field && currentOrder == SortOrder.DESC -> arrowDownRes
            else -> null
        }
        Triple(opt.label, arrowIcon, opt.field)
    }

    var expanded by remember { mutableStateOf(false) }

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
    // 1) Aktueller Sort-Status aus dem ViewModel
    val currentField by viewViewModel.sortField.collectAsState()
    val currentOrder by viewViewModel.sortOrder.collectAsState()

    // 2) Optionen mit optionalem Pfeil-Icon je nach aktuellem Feld & Richtung
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

    var expanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable { expanded = true }
    ) {
        // 3) Haupt-Icon
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDesc,
            tint = iconTint,
            modifier = Modifier
                .width(width * 0.6f)
                .height(height * 0.6f)
        )

        // 4) Dropdown-Menü
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


