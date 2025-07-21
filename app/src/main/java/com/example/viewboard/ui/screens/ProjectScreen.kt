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
import com.example.viewboard.components.homeScreen.ProfileHeader
import androidx.compose.ui.Alignment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import com.example.viewboard.components.project.ProjectItem
import com.example.viewboard.ui.navigation.BottomBarScreen
import com.example.viewboard.ui.timetable.CustomIcon
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.Screen
import com.example.viewboard.ui.project.CustomSearchField
import kotlinx.coroutines.flow.map

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.runtime.collectAsState
import com.example.viewboard.ui.issue.ProjectViewModel
import com.example.viewboard.ui.timetable.CustomIconMenu
import com.example.viewboard.ui.timetable.ProjectSortMenu
import com.example.viewboard.ui.timetable.ProjectSortMenuSimple
import com.example.viewboard.ui.timetable.SortOptions


/**
 * Beispiel-Liste von Projekten für Preview und Tests.
 */

object AppColors {
    // Deine Basisfarben
    val Orange      = Color(0xFFFFB74D)  // kräftiges Orange
    val Green       = Color(0xFF81C784)  // sattes Grün
    val LightBlue   = Color(0xFFBEDBFF)  // helles, aber lebhaftes Blau
    val DeepOrange  = Color(0xFFFF8A65)
    val LimeGreen   = Color(0xFF9CCC65)
    val SkyBlue     = Color(0xFF64B5F6)
    val Teal        = Color(0xFF4DB6AC)
    val Purple      = Color(0xFFBA68C8)
    val Coral       = Color(0xFFFF7043)
    val Mint        = Color(0xFF4CAF50)

    val StrongPalette = listOf(
        Orange,
        Green,
        LightBlue,
        DeepOrange,
        LimeGreen,
        SkyBlue,
        Teal,
        Purple,
        Coral,
        Mint
    )
}

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
    @Suppress("UNUSED_PARAMETER")
    projectViewModel: ProjectViewModel,
    columns: Int = 2,
    onSort: () -> Unit = {},
) {
    // Bestimme den Filter-Modus anhand des Namens
    val filterMode = when (projectName.lowercase()) {
        "created" -> ProjectFilter.CREATED
        "shared"  -> ProjectFilter.SHARED
        "all"     -> ProjectFilter.ALL
        else      -> ProjectFilter.CREATED
    }

    // Sobald filterMode sich ändert: Filter im ViewModel setzen und neu laden
    LaunchedEffect(filterMode) {
        projectViewModel.setFilter(filterMode)
    }

    // Beobachte die gefilterten & gesuchten Projekte aus dem ViewModel
    val projects by projectViewModel.displayedProjects.collectAsState()

    // Lokaler State für die Such-Query
    val query by projectViewModel.query.collectAsState()
    val currentField by projectViewModel.sortField.collectAsState()
    val currentOrder by projectViewModel.sortOrder.collectAsState()
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
                    navController.navigate(Screen.ProjectCreationScreen.route)
                }
            )
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
            // Header, Suchfeld und Sort/Filter-Icons
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "$projectName Projects",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
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
                      /*  CustomIcon(
                            iconRes = R.drawable.filter_svgrepo_com__1,
                            contentDesc = stringResource(R.string.sort_desc_svgrepo_com),
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            iconTint = Color.White,
                            width = 40.dp,
                            height = 40.dp,
                            onClick = onSort,
                            modifier = Modifier.padding(end = 8.dp)
                        )*/// Usage example:
                         val sortOptions = listOf(
                             SortOptions("Sort by Date", ProjectViewModel.SortField.DATE),
                            SortOptions("Sort by Name", ProjectViewModel.SortField.NAME),

                         )
                         ProjectSortMenuSimple(projectViewModel, sortOptions, iconRes = R.drawable.sort_desc_svgrepo_com, contentDesc = "Sort")
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

            // Grid-Items für jedes Projekt
            itemsIndexed(projects) { index, project ->
                ProjectItem(
                    project = project,
                    avatarUris = emptyList(),
                    navController = navController,
                    onClick = {
                        navController.navigate(
                            Screen.IssueScreen.createRoute(project.name, project.id)
                        )
                    }
                )
            }
        }
    }
}


enum class ProjectFilter { CREATED, SHARED, ALL }