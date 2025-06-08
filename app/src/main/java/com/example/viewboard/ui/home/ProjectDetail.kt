package com.example.viewboard.ui.home

import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.viewboard.ui.theme.uiColor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip

class Issue(
    val id: Int,
    val title: String,
    val description: String,
    initialDone: Boolean = false
) {
    var isDone by mutableStateOf(initialDone)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    modifier: Modifier = Modifier,
    projectName: String,
    navController: NavController
) {

    val sampleIssues = remember {
        mutableStateListOf(
            Issue(1, "Milch und Brot einkaufen", "das ist eine test beschreibung"),
            Issue(2, "Milch und Brot einkaufen", "das ist eine test beschreibung"),
            Issue(3, "Milch und Brot einkaufen", "das ist eine test beschreibung"),
            Issue(4, "Milch und Brot einkaufen", "das ist eine test beschreibung"),
            Issue(5, "Milch und Brot einkaufen", "das ist eine test beschreibung"),
            Issue(6, "Milch und Brot einkaufen", "das ist eine test beschreibung"),
            Issue(7, "Milch und Brot einkaufen", "das ist eine test beschreibung"),
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

            Text(
                text = "Issues",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                items(sampleIssues, key = { issue: Issue -> issue.id }) { item ->
                    IssueRow(todo = item)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            AddIssueButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = { /* TODO */ }
            )
        }
    }
}

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
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Issue")
    }
}


@Composable
fun IssueRow(
    todo: Issue,
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
            modifier = Modifier
                .padding(horizontal = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = uiColor(), shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(15.dp))

            Column {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = todo.description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Checkbox(
            checked = todo.isDone,
            onCheckedChange = { isChecked: Boolean -> todo.isDone = isChecked }
        )

    }
}