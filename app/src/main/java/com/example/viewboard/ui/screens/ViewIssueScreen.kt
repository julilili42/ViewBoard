package com.example.viewboard.ui.screens

import android.graphics.drawable.shapes.Shape
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.backend.Timestamp
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.components.homeScreen.ProfileHeader
import com.example.viewboard.ui.issue.IssueItemCard
import com.example.viewboard.ui.issue.IssueViewModel

import com.example.viewboard.ui.issue.ProjectViewModel
import com.example.viewboard.ui.issue.ViewsViewModel
import com.example.viewboard.ui.navigation.BottomBarScreen
import com.example.viewboard.ui.timetable.CustomIcon
import androidx.compose.foundation.lazy.grid.GridItemSpan
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import android.content.res.Resources
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import colorFromCode


import com.example.viewboard.ui.project.CustomSearchField
import generateProjectCodeFromDbId


@Composable
fun ViewIssueScreen(
    issueViewModel: IssueViewModel,
    projectViewModel: ProjectViewModel,
    viewsViewModel: ViewsViewModel,
    navController: NavController,
    viewID: String,
    projID: String,
    viewName: String
) {
    val projects by projectViewModel.displayedviewProjects.collectAsState()
    Log.d("ViewIssueScreen", "Issue loaded: id=$projects")
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Observe query and filter state from ViewModel
    val query by issueViewModel.query.collectAsState(initial = "")

    // Set current view and load issues
    LaunchedEffect(viewID) {
        issueViewModel.setCurrentViewId(viewID)
        issueViewModel.loadIssuesFromView(viewID)
    }
    val issues by issueViewModel.displayedIssuesFromViews.collectAsState()
    Scaffold(
        topBar = {
            ProfileHeader(
                name = AuthAPI.getCurrentDisplayName() ?: "Unknown User",
                subtitle = "Welcome back!",
                navController = navController,
                showBackButton = true,
                onProfileClick = { navController.navigate(BottomBarScreen.Profile.route) },
                onBackClick = { navController.navigateUp() }
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
                onClick = { showDialog = true }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Dialog für neue Issue
            if (showDialog) {
                item {
                    ProjectIssueDialog(
                        viewId = viewID,
                        projects = projects,
                        issueViewModel = issueViewModel,
                        onDismiss = { showDialog = false }
                    )
                }
            }


            // View-Name
            item {
                EdgeToEdgeRoundedRightItemWithBadge(
                    viewName = viewName,
                    projectId=projID,
                )
            }

            // Such- und Sort-Leiste
            item {
                Row(
                    Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomSearchField(
                        query = query,
                        onQueryChange = { issueViewModel.setQuery(it) },
                        modifier = Modifier
                            .height(40.dp)
                            .width(200.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // hier ggf. dein Toggle-Icon und ViewSortMenuSimple
                    }
                }
            }

            // Die Issues als Lazy Items
            items(issues) { issue ->
                val emailsState by produceState<List<String?>>(
                    initialValue = emptyList(),
                    key1 = issue .assignments
                ) {
                    // Lade die E‑Mails; bei Fehler oder leerem Ergebnis bleibt es bei emptyList
                    val result = runCatching { AuthAPI.getEmailsByIds(issue .assignments) }
                        .getOrNull()
                        ?.getOrNull()
                    value = result ?: emptyList()
                }
                IssueItemCard(
                    title        = issue .title,
                    state        = stateToString(issue .state),
                    date         = issue .deadlineTS,
                    attachments  = 3,
                    emailsState =emailsState,
                    assignments = issue.assignments,
                    projectId    = projID,
                    issueId      = issue .id,
                    avatarUris   = listOf(), // dummy or real
                    navController= navController,
                    issuelabels = issue.labels,
                    modifier     = Modifier.clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}




@Composable
fun EdgeToEdgeRoundedRightItemWithBadge(
    viewName: String,
    projectId: String? = null,
    parentHorizontalPadding: Dp = 16.dp,
    boxHeight: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 0.dp,
            topEnd = 24.dp,
            bottomEnd = 24.dp
        ),
        modifier = modifier
            // Card selbst füllt die Breite
            .fillMaxWidth()
            .offset(x = -parentHorizontalPadding)

            // entferne inneres Card-Padding
            .padding(horizontal = 0.dp, vertical = 0.dp),
        colors = CardDefaults.cardColors(), // Standard‑Hintergrund
        elevation = CardDefaults.cardElevation(defaultElevation = 30.dp) // Standard‑Elevation
    ) {
        // Inhalt der Card: deine edge‑to‑edge Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(boxHeight)
                .padding(top = 4.dp, bottom = 4.dp, end = 8.dp, start = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = viewName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                        .align(Alignment.CenterStart)
            )

            if (!projectId.isNullOrBlank()) {
                val tagCode= generateProjectCodeFromDbId(projectId)
                val tagColor = colorFromCode(tagCode)
                ProjectNameBadge(
                    text = tagCode,
                    backgroundColor = tagColor,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
fun ProjectNameBadge(
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}