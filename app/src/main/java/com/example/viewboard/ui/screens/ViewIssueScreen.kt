package com.example.viewboard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.components.homeScreen.ProfileHeader
import com.example.viewboard.ui.issue.IssueItemCard
import com.example.viewboard.stateholder.IssueViewModel
import com.example.viewboard.stateholder.ProjectViewModel
import com.example.viewboard.ui.navigation.BottomBarScreen
import com.example.viewboard.ui.timetable.CustomIcon
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import com.example.viewboard.ui.utils.CustomSearchField
import com.example.viewboard.ui.views.EdgeToEdgeRoundedRightItemWithBadge


@Composable
fun ViewIssueScreen(
    viewId: String,
    projectId: String,
    viewName: String,
    issueViewModel: IssueViewModel,
    projectViewModel: ProjectViewModel,
    navController: NavController,

) {
    val projects by projectViewModel.displayedViewProjects.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val query by issueViewModel.query.collectAsState(initial = "")
    LaunchedEffect(viewId) {
        issueViewModel.setCurrentViewId(viewId)
        issueViewModel.loadIssuesFromView(viewId)
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
            // Dialog new issues
            if (showDialog) {
                item {
                    ProjectIssueDialog(
                        viewId = viewId,
                        projects = projects,
                        issueViewModel = issueViewModel,
                        onDismiss = { showDialog = false }
                    )
                }
            }

            item {
                EdgeToEdgeRoundedRightItemWithBadge(
                    viewName = viewName,
                    projectId=projectId,
                )
            }
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
                    }
                }
            }

            items(issues) { issue ->
                val emailsState by produceState<List<String?>>(
                    initialValue = emptyList(),
                    key1 = issue .users
                ) {
                    val result = runCatching { AuthAPI.getEmailsByIds(issue .users) }
                        .getOrNull()
                        ?.getOrNull()
                    value = result ?: emptyList()
                }
                IssueItemCard(
                    title        = issue .title,
                    date         = issue .deadlineTS,
                    emailsState =emailsState,
                    projectId    = projectId,
                    issueId      = issue .id,
                    navController= navController,
                    issuelabels = issue.labels,
                    modifier     = Modifier.clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}






