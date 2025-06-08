package com.example.viewboard.ui.project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.ui.navigation.BackButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.viewboard.ui.navigation.Screen

/**
 * Only temporary
 */
class Issue(
    val id: Int,
    val title: String,
    val description: String,
    val label: ArrayList<String>,
    initialDone: Boolean = false
) {
    var isDone by mutableStateOf(initialDone)
}


/**
 * Displays the detail screen for a project, including a list of its issues.
 *
 * @param modifier Optional [Modifier] for layout customization
 * @param projectName Name of the selected project
 * @param navController Navigation controller used to navigate to other screens
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    modifier: Modifier = Modifier,
    projectName: String,
    navController: NavController,
) {

    val sampleIssues = remember {
        mutableStateListOf(
            Issue(1, "Issue 1", "Example description", arrayListOf("Label 1", "Label 2", "Label 3")),
            Issue(2, "Issue 2", "Example description", arrayListOf("Label 1")),
            Issue(3, "Issue 3", "Example description", arrayListOf("Label 2", "Label 3")),
            Issue(4, "Issue 4", "Example description", arrayListOf()),
            Issue(5, "Issue 5", "Example description", arrayListOf("Label 3"))
        )
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(projectName) },
                navigationIcon = {
                    BackButton(
                        text = stringResource(R.string.Back),
                        onClick = { navController.popBackStack() })
                }
            )
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CategoryBoxRow()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 65.dp)
            ) {
                items(sampleIssues, key = { issue: Issue -> issue.id }) { item ->
                    IssueRow(issue = item)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            AddIssueButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = {
                    navController.navigate(
                        Screen.IssueCreationScreen.createRoute(
                            projectName = projectName
                        )
                    )
                }
            )
        }
    }
}

/**
 * Floating action button for adding a new issue to the current project.
 *
 * @param modifier Optional [Modifier] for styling
 * @param onClick Callback triggered when the button is pressed
 */
@Composable
fun AddIssueButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.AddIssue))
    }
}


/**
 * Displays a row for a single issue with title, description, and a completion checkbox.
 *
 * @param todo The [Issue] to display
 */
@Composable
fun IssueRow(
    issue: Issue,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,

        ) {
            Column {
                Text(
                    text = issue.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = issue.description,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(2.dp))
                IssueLabelRow(labels = issue.label)
            }
        }
        Checkbox(
            checked = issue.isDone,
            onCheckedChange = { isChecked: Boolean -> issue.isDone = isChecked }
        )

    }
}

/**
 * Displays a horizontal list of labels as styled chips.
 *
 * @param labels List of label strings to display
 */
@Composable
fun IssueLabelRow(labels: List<String>) {
    val labelColors = getHighlightColors()

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        labels.forEachIndexed { index, label ->
            Surface(
                tonalElevation = 4.dp,
                shape = RoundedCornerShape(10.dp),
                color = labelColors[index % labelColors.size]
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                )
            }
        }
    }
}

/**
 * Displays a row of colored category boxes and an add button.
 *
 * Uses colors from [getHighlightColors] and fixed category names.
 */
@Composable
fun CategoryBoxRow() {
    val categories = listOf("Category 1", "Category 2", "Category 3")
    val categoryColors = getHighlightColors();

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        categories.forEachIndexed { index, category ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = categoryColors[index % categoryColors.size],
                tonalElevation = 2.dp,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .size(50.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Category"
                )
            }
        }
    }
}

/**
 * Returns a list of soft highlight colors for UI elements.
 *
 * @return List of highlight [Color]s
 */
@Composable
fun getHighlightColors(): List<Color> = listOf(
    MaterialTheme.colorScheme.primaryContainer,
    MaterialTheme.colorScheme.tertiaryContainer,
    MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
)
