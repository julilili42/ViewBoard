package com.example.viewboard.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.data.IssueLayout
import com.example.viewboard.backend.data.IssueState
import com.example.viewboard.backend.data.SortOptionsIssues
import com.example.viewboard.components.homeScreen.ProfileHeader
import com.example.viewboard.ui.issue.IssueItemCard
import com.example.viewboard.stateholder.IssueViewModel
import com.example.viewboard.ui.navigation.BottomBarScreen
import com.example.viewboard.ui.navigation.Screen
import com.example.viewboard.ui.utils.CustomSearchField
import com.example.viewboard.ui.timetable.CustomIcon
import com.example.viewboard.ui.utils.IssueSortMenuSimple
import com.example.viewboard.ui.views.EdgeToEdgeRoundedRightItemWithBadge


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueScreen(
    projectName: String,
    projectId: String,
    issueViewModel: IssueViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(selectedTab) {
        val state = stateFromIndex(selectedTab)
        issueViewModel.setFilter(state)
        Log.d("IssueScreen", "Tab switched: $selectedTab → setFilter($state)")
    }
    val query by issueViewModel.query.collectAsState()
    val onlyMine by issueViewModel.showOnlyMyIssues.collectAsState()
    LaunchedEffect(projectId) {
        issueViewModel.setProject(projectId)
    }
    val categories = listOf("New", "Ongoing", "Completed")
    var filterMode by remember { mutableStateOf<IssueState?>(null) }
    val issues by issueViewModel.displayedIssues.collectAsState()
    val email by issueViewModel.emailsForIssue.collectAsState()

    Scaffold(
        topBar = {
            ProfileHeader(
                name           = AuthAPI.getCurrentDisplayName() ?: "…",
                subtitle       = "Welcome back!!",
                navController  = navController,
                showBackButton = true,
                onProfileClick = { navController.navigate(BottomBarScreen.Profile.route) },
                onBackClick    = { navController.navigateUp() }
            )
        },
        floatingActionButton = {
            CustomIcon(
                iconRes       = R.drawable.plus_large_svgrepo_com,
                contentDesc   = stringResource(R.string.plus_large_svgrepo_com),
                backgroundColor = MaterialTheme.colorScheme.primary,
                iconTint        = Color.White,
                width           = 50.dp,
                height          = 50.dp,
                modifier        = Modifier
                    .offset(y = 40.dp)
                    .padding(16.dp)
                    .clip(CircleShape),
                onClick       = { navController.navigate(Screen.IssueCreationScreen.createRoute(projectName,projectId)) }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding     = PaddingValues(16.dp),


        ) {
            item {
                EdgeToEdgeRoundedRightItemWithBadge(
                    viewName = projectName,
                    projectId = projectId,
                )
            }
            // search & filter row
            val sortOptions = listOf(
                SortOptionsIssues("Sort by Date", IssueViewModel.SortField.DATE),
                SortOptionsIssues("Sort by Name", IssueViewModel.SortField.NAME),
                )
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment   = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomSearchField(
                        query           = query,
                        onQueryChange   = { issueViewModel.setQuery(it)},
                        modifier        = Modifier
                            .height(40.dp)
                            .width(200.dp)
                    )
                    val iconRes = if (onlyMine)
                        R.drawable.profile_svgrepo_com__2_
                    else
                        R.drawable.profile_group_svgrepo_com
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CustomIcon(
                            iconRes         = iconRes,
                            contentDesc     = "",
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            iconTint   = Color.White,
                            width           = 40.dp,
                            height          = 40.dp,
                            onClick         = { issueViewModel.setShowOnlyMine() },
                            modifier        = Modifier.padding(end = 8.dp)
                        )
                        IssueSortMenuSimple(issueViewModel,
                            options = sortOptions,
                            iconTint = Color.White,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            iconRes = R.drawable.sort_desc_svgrepo_com,
                            contentDesc = "Sort")
                    }
                }
            }
            // tabs row
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    categories.forEachIndexed { idx, label ->
                        DropItem<IssueLayout>(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .padding(horizontal = 4.dp),
                            onDrop = { item ->
                                filterMode = stateFromIndex(idx)
                                issueViewModel.setState( stateFromIndex(idx))
                                selectedTab  = idx
                                issueViewModel.moveItemToState(item, stateFromIndex(idx))
                            }
                        ) { isOver, _ ->
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isOver            -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                            selectedTab == idx -> MaterialTheme.colorScheme.primary
                                            else               -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (isOver)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedTab = idx
                                        issueViewModel.setState( stateFromIndex(idx))},
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text  = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selectedTab == idx)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
            // issue list
            items(issues) { item ->
                val mails: List<String?> = email[item.id].orEmpty()
                DragTarget(
                    dataToDrop = item,
                    viewModel  = issueViewModel
                ) {
                    IssueItemCard(
                        title        = item.title,
                        date         = item.deadlineTS,
                        emailsState = mails,
                        projectId    = projectId,
                        issueId      = item.id,
                        navController= navController,
                        issuelabels = emptyList(), // TODO remove
                        modifier     = Modifier.clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        }
    }
}

fun stateFromIndex(idx: Int): IssueState = when (idx) {
    0    -> IssueState.NEW
    1    -> IssueState.ONGOING
    2    -> IssueState.DONE
    else -> throw IllegalArgumentException("Invalid Index for IssueState: $idx")
}


