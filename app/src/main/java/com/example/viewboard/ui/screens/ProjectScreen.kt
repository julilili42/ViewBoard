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

/**
 * Beispiel-Liste von Projekten für Preview und Tests.
 */

object AppColors {
    // Deine Basisfarben
    val Orange      = Color(0xFFFFB74D)  // kräftiges Orange
    val Green       = Color(0xFF81C784)  // sattes Grün
    val LightBlue   = Color(0xFFBEDBFF)  // helles, aber lebhaftes Blau

    // Ergänzende kräftige Farben in ähnlicher Richtung
    val DeepOrange  = Color(0xFFFF8A65)
    val LimeGreen   = Color(0xFF9CCC65)
    val SkyBlue     = Color(0xFF64B5F6)
    val Teal        = Color(0xFF4DB6AC)
    val Purple      = Color(0xFFBA68C8)
    val Coral       = Color(0xFFFF7043)
    val Mint        = Color(0xFF4CAF50)

    // Die gesamte Palette
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
    columns: Int = 2,
    onSort: () -> Unit = {},
) {
    var showOnlyMyProjects by remember { mutableStateOf(true) }
    val projectLayouts = remember { mutableStateListOf<ProjectLayout>() }
    var error by remember { mutableStateOf<String?>(null) }
    // startet automatisch beim ersten Composable-Aufruf
    LaunchedEffect(showOnlyMyProjects) {
        try {
            val flow = if (showOnlyMyProjects) FirebaseAPI.getProjectsFromUser(AuthAPI.getUid())
            else FirebaseAPI.getAllProjects()
            flow.collect { layouts ->
                projectLayouts.clear()
                projectLayouts.addAll(layouts)
            }
        } catch (e: Exception) {
            error = "Ladefehler: ${e.localizedMessage}"
        }
    }

    Scaffold(
        topBar = {
                ProfileHeader(
                    name = AuthAPI.getCurrentDisplayName() ?: "failed to load username",
                    subtitle = "Welcome back!!",
                    navController =navController,
                    showBackButton = true,
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
                onClick = { navController.navigate(Screen.ProjectCreationScreen.route) },

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
                        backgroundColor = if (showOnlyMyProjects) Color.Green else Color.Gray,
                        iconTint = Color.White,
                        width = 40.dp,
                        height = 40.dp,
                        onClick = { showOnlyMyProjects = !showOnlyMyProjects },
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
                itemsIndexed(projectLayouts) {index, project ->
                    ProjectItem(
                        name                = project.name,
                        phase               = project.phase,
                        startMonth          = project.startMonth,
                        endMonth            = project.endMonth,
                        color               = AppColors.StrongPalette[index % AppColors.StrongPalette.size],
                        totalMilestones     = project.totalMilestones,
                        completedMilestones = project.completedMilestones,
                        avatarUris          = emptyList(),
                        onClick             = {navController.navigate(Screen.IssueScreen.createRoute(project.name,project.id))}
                    )
                }
            }
        }
    }
}
