package com.example.viewboard.ui.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.ui.navigation.BackButton

/**
 * A screen that allows users to create a new issue for the selected project.
 *
 * @param modifier Optional [Modifier] for layout adjustments
 * @param projectName The name of the project for which the issue is being created
 * @param navController Navigation controller used to go back or forward
 * @param onCreateIssue Callback invoked when the user creates a new issue
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueCreationScreen(
    modifier: Modifier = Modifier,
    projectName: String,
    navController: NavController,
    onCreateIssue: (Issue) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val assignees = remember { mutableStateListOf<String>() }
    val labels = remember { mutableStateListOf<String>() }

    // Example categories
    val possibleUsers = listOf("Person 1", "Person 2", "Person 3")
    val possibleLabels = listOf("Label 1", "Label 2", "Label 3")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = projectName) },
                navigationIcon = {
                    BackButton(
                        text = stringResource(R.string.Back),
                        onClick = { navController.popBackStack() }
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {/*TODO*/},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = stringResource(R.string.Create))
            }
        },
        content = { padding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.AddIssue),
                    style = MaterialTheme.typography.headlineSmall
                )

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.Title)) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.Description)) },
                    placeholder = { Text(text = stringResource(R.string.Optional)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    shape = RoundedCornerShape(8.dp)
                )

                // Assignees
                SectionLabel(text = stringResource(R.string.Assignees))
                ChipSelectionRow(
                    options = possibleUsers,
                    selected = assignees,
                    onSelect = { user ->
                        if (assignees.contains(user)) assignees.remove(user)
                        else if (assignees.size < 255) assignees.add(user)
                    }
                )

                // Labels
                SectionLabel(text = stringResource(R.string.Labels))
                ChipSelectionRow(
                    options = possibleLabels,
                    selected = labels,
                    onSelect = { label ->
                        if (labels.contains(label)) labels.remove(label)
                        else if (labels.size < 255) labels.add(label)
                    }
                )
            }
        }
    )
}

/**
 * Displays a section title in uppercase with spacing below it.
 *
 * @param text The title to display
 */
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

/**
 * Selectable chip row with colored highlights.
 *
 * @param options All options to show
 * @param selected Currently selected items
 * @param onSelect Called when an item is clicked
 */
@Composable
private fun ChipSelectionRow(
    options: List<String>,
    selected: List<String>,
    onSelect: (String) -> Unit
) {
    val labelColors = getHighlightColors()
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(options) { idx, item->
            val item = options[idx]
            val isSelected = selected.contains(item)
            Surface(
                tonalElevation = if (isSelected) 4.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected)
                    labelColors[idx % labelColors.size]
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clickable { onSelect(item) }
            ) {
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

