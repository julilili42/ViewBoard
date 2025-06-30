package com.example.viewboard.ui.screens
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.dataclass.Project
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.stringResource
import com.example.viewboard.R
import com.example.viewboard.components.HomeScreen.ProfileHeader
import androidx.compose.ui.Alignment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import com.example.viewboard.components.HomeScreen.ProjectItem
import com.example.viewboard.ui.navigation.BottomBarScreen
import com.example.viewboard.ui.timetable.CustomIcon
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.viewboard.ui.navigation.Screen
import com.example.viewboard.ui.project.CustomSearchField


/**
 * Beispiel-Liste von Projekten für Preview und Tests.
 */
val sampleProjectList = listOf(
    Project(
        name = "Fintech App",
        description = "Mobile Banking & Investing",
        phase = "#A23",
        startMonth = 12,
        endMonth = 1,
        color = Color(0xFFBEDBFF),
        totalMilestones = 8,
        completedMilestones = 5f
    ),
    Project(
        name = "E-Commerce Platform",
        description = "Online-Marktplatz für Kleinunternehmen",
        phase = "#B17",
        startMonth = 2,
        endMonth = 5,
        color = Color(0xFF81C784),
        totalMilestones = 10,
        completedMilestones = 3f
    ),
    Project(
        name = "Social Media App",
        description = "Chat, Stories & Feed",
        phase = "#C09",
        startMonth = 3,
        endMonth = 7,
        color = Color(0xFFFFB74D),
        totalMilestones = 12,
        completedMilestones = 7f
    ),
    Project(
        name = "Health Tracker",
        description = "Fitness & Wellness Monitoring",
        phase = "#D34",
        startMonth = 6,
        endMonth = 9,
        color = Color(0xFF64B5F6),
        totalMilestones = 6,
        completedMilestones = 2.5f
    ),
    Project(
        name = "Fintech App",
        description = "Mobile Banking & Investing",
        phase = "#A23",
        startMonth = 12,
        endMonth = 1,
        color = Color(0xFFBEDBFF),
        totalMilestones = 8,
        completedMilestones = 5f
    ),
    Project(
        name = "E-Commerce Platform",
        description = "Online-Marktplatz für Kleinunternehmen",
        phase = "#B17",
        startMonth = 2,
        endMonth = 5,
        color = Color(0xFF81C784),
        totalMilestones = 10,
        completedMilestones = 3f
    ),
    Project(
        name = "Social Media App",
        description = "Chat, Stories & Feed",
        phase = "#C09",
        startMonth = 3,
        endMonth = 7,
        color = Color(0xFFFFB74D),
        totalMilestones = 12,
        completedMilestones = 7f
    ),
    Project(
        name = "Health Tracker",
        description = "Fitness & Wellness Monitoring",
        phase = "#D34",
        startMonth = 6,
        endMonth = 9,
        color = Color(0xFF64B5F6),
        totalMilestones = 6,
        completedMilestones = 2.5f
    )
)

private val dummyAvatarUris = listOf(
    Uri.parse("https://picsum.photos/seed/1/64"),
    Uri.parse("https://picsum.photos/seed/2/64"),
    Uri.parse("https://picsum.photos/seed/3/64"),
    Uri.parse("https://picsum.photos/seed/4/64"),
    Uri.parse("https://picsum.photos/seed/5/64")
)
/**
 * Zeigt eine anpassbare Grid-Liste von Projekten.
 *
 * @param navController Navigation-Controller für Klicks
 * @param projects      Liste der Projekte
 * @param columns       Anzahl der Spalten im Grid (z. B. 1, 2, 3…)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    navController: NavController,
    projectName: String,
    projects: List<Project> = sampleProjectList,
    columns: Int = 2,
    onAddProject: () -> Unit = {},
    onSort: () -> Unit = {},
    onFilter: () -> Unit = {}
) {
    Scaffold(
        topBar = {
                ProfileHeader(
                    name = "Raoul",
                    subtitle = "Welcome back!!",
                    onProfileClick = {
                        navController.navigate(BottomBarScreen.Profile.route)
                    },
                    onBackClick = {navController.navigateUp()}
                )

        },
        floatingActionButton = {
            CustomIcon(
                iconRes = R.drawable.plus_large_svgrepo_com,
                contentDesc = stringResource(R.string.plus_large_svgrepo_com),
                backgroundColor = MaterialTheme.colorScheme.primary,
                iconTint = Color.White,
                width = 50.dp,
                height = 50.dp,
                modifier = Modifier
                    .offset(y = 40.dp)     // verschiebt den FAB 24dp weiter nach unten
                    .padding(16.dp)
                    .clip(CircleShape), // behält rechts 16dp Abstand,
                onClick = onAddProject,

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Text(
                text = projectName +" Projects", // z.B. "My Projects"
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                var query by remember { mutableStateOf("") }
                val items = listOf("Apple", "Banana", "Cherry").filter {
                    it.contains(query, ignoreCase = true)    }
                    CustomSearchField(
                    query = query,
                    onQueryChange = { query = it },
                    modifier = Modifier
                        .height(40.dp)
                        .width(200.dp),
                    suggestionContent = { q ->
                        Column {
                            items.forEach { item ->
                                Text(
                                    text = item,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            query = item
                                        }
                                        .padding(12.dp)
                                )
                            }
                        }
                    }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CustomIcon(
                        iconRes = R.drawable.sort_desc_svgrepo_com,
                        contentDesc = stringResource(R.string.sort_desc_svgrepo_com),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        iconTint = Color.White,
                        width = 40.dp,
                        height = 40.dp,
                        onClick = onSort,
                        modifier = Modifier

                    )
                    CustomIcon(
                        iconRes = R.drawable.filter_svgrepo_com__1,
                        contentDesc = stringResource(R.string.filter_svgrepo_com__1),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        iconTint = Color.White,
                        width = 40.dp,
                        height = 40.dp,
                        onClick = onFilter,
                        modifier = Modifier
                    )
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                items(projects) { project ->
                    ProjectItem(
                        name                = project.name,
                        phase               = project.phase,
                        startMonth          = project.startMonth,
                        endMonth            = project.endMonth,
                        color               = project.color,
                        totalMilestones     = project.totalMilestones,
                        completedMilestones = project.completedMilestones,
                        avatarUris          = dummyAvatarUris,
                        onClick             = {navController.navigate(Screen.IssueCreationScreen.createRoute(project.name))}
                    )
                }
            }
        }
    }
}
