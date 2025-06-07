package com.example.viewboard.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.ui.navigation.BackButton
import com.example.viewboard.ui.navigation.Screen


/**
 * The main screen displaying lists of active and recent projects.
 *
 * @param navController Controller used to navigate between screens
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val activeProjects = listOf("Project 1", "Project 2", "Project 3")
    val recentProjects = listOf("Project A", "Project B")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.Projects)) },
                navigationIcon = {
                    BackButton(
                        text = stringResource(R.string.Back),
                        onClick = { navController.popBackStack() })
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            ProjectGrid(
                projects = activeProjects,
                title = stringResource(R.string.ActiveProjects)
            ) { projectName ->
                navController.navigate(Screen.ProjectDetail.createRoute(projectName))
            }

            Spacer(modifier = Modifier.height(80.dp))

            ProjectGrid(
                projects = recentProjects,
                title = stringResource(R.string.RecentProjects)
            ) { projectName ->
                navController.navigate(Screen.ProjectDetail.createRoute(projectName))
            }
        }
    }
}


/**
 * A reusable grid displaying a list of projects.
 *
 * @param projects List of project names to show
 * @param title Header title for this grid section
 * @param onProjectClick Callback invoked when a project is tapped
 */
@Composable
fun ProjectGrid(
    projects: List<String>,
    title: String,
    onProjectClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(projects) { name ->
                ProjectCard(name = name) { onProjectClick(name) }
            }
        }
    }
}


/**
 * A card representing a single project in the grid.
 *
 * @param name The display name of the project
 * @param onClick Action to perform when the card is clicked
 */
@Composable
fun ProjectCard(name: String, onClick: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
