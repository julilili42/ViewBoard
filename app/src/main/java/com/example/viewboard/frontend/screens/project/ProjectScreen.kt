package com.example.viewboard.frontend.screens.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.stringResource
import com.example.viewboard.R
import com.example.viewboard.frontend.components.home.profile.ProfileHeader
import androidx.compose.ui.Alignment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.example.viewboard.frontend.components.project.ProjectItem
import com.example.viewboard.frontend.navigation.BottomBarScreen
import com.example.viewboard.frontend.components.timetable.CustomIcon
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.frontend.navigation.NavScreens
import com.example.viewboard.frontend.components.utils.CustomSearchField
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.runtime.collectAsState
import com.example.viewboard.frontend.stateholder.ProjectViewModel
import com.example.viewboard.frontend.components.project.ProjectSortMenuSimple
import com.example.viewboard.frontend.components.views.EdgeToEdgeRoundedRightItemWithBadge


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    navController: NavController,
    projectName: String,
    projectViewModel: ProjectViewModel,
    onSort: () -> Unit = {},
) {
    // Bestimme den Filter-Modus anhand des Namens
    val filterMode = when (projectName.lowercase()) {
        "created" -> ProjectFilter.CREATED
        "shared"  -> ProjectFilter.SHARED
        else      -> ProjectFilter.CREATED
    }

    LaunchedEffect(filterMode) {
        projectViewModel.setFilter(filterMode)
    }
    val projects by projectViewModel.displayedProjects.collectAsState()
    val editable = filterMode == ProjectFilter.CREATED
    val query by projectViewModel.query.collectAsState()
    val columns = 2
    Scaffold(
        topBar = {
            ProfileHeader(
                name = AuthAPI.getCurrentDisplayName() ?: "Unknown User",
                subtitle = "Welcome back!",
                navController = navController,
                showBackButton = true,
                onProfileClick = {
                    navController.navigate(BottomBarScreen.Profile.route)
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        },
        floatingActionButton = {
            if(editable) {
                CustomIcon(
                    iconRes = R.drawable.plus_large_svgrepo_com,
                    contentDesc = stringResource(R.string.plus_large_svgrepo_com),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    iconTint = Color.White,
                    width = 50.dp,
                    height = 50.dp,
                    modifier = Modifier
                        .offset(y = 40.dp)
                        .padding(16.dp)
                        .clip(CircleShape),
                    onClick = {
                        navController.navigate(NavScreens.ProjectCreationNavScreens.route)
                    }
                )
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EdgeToEdgeRoundedRightItemWithBadge(viewName = "$projectName Projects")
            }
            // Header, Suchfeld und Sort/Filter-Icons
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),

                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomSearchField(
                        query = query,
                        onQueryChange = { projectViewModel.setQuery(it) },
                        modifier = Modifier
                            .height(40.dp)
                            .width(200.dp),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProjectSortMenuSimple(
                            projectViewModel = projectViewModel,
                            iconRes          = R.drawable.sort_desc_svgrepo_com,
                            backgroundColor  = MaterialTheme.colorScheme.primary,
                            iconTint  = Color.White,
                            contentDesc      = stringResource(R.string.filter_svgrepo_com__1),
                            modifier         = Modifier.size(40.dp)
                        )
                    }
                }
            }
            itemsIndexed(projects) { index, project ->
                ProjectItem(
                    project = project,
                    avatarUris = emptyList(),
                    navController = navController,
                    onClick = {
                        navController.navigate(
                            NavScreens.IssueNavScreens.createRoute(project.name, project.id)
                        )
                    },
                    editable = editable
                )
            }
        }
    }
}


enum class ProjectFilter { CREATED, SHARED, ALL }