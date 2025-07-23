package com.example.viewboard.ui.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.IssueState
import com.example.viewboard.components.homeScreen.ProfileHeader
import com.example.viewboard.ui.issue.IssueItemCard
import com.example.viewboard.ui.issue.IssueViewModel
import com.example.viewboard.ui.issue.MainViewModel
import com.example.viewboard.ui.issue.ProjectViewModel
import com.example.viewboard.ui.navigation.BottomBarScreen
import com.example.viewboard.ui.navigation.Screen
import com.example.viewboard.ui.project.CustomSearchField
import com.example.viewboard.ui.timetable.CustomIcon
import com.example.viewboard.ui.timetable.IssueSortMenuSimple
import com.example.viewboard.ui.timetable.SortOptions
import com.example.viewboard.ui.timetable.SortOptions2
import generateProjectCodeFromDbId


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueScreen(
    mainViewModel: MainViewModel,
    navController: NavController,
    projectName: String,
    issueViewModel: IssueViewModel,
    projectId: String,
    modifier: Modifier = Modifier
) {

    var showOnlyMyIssues by rememberSaveable { mutableStateOf(true) }
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    // Lokaler State für die Such-Query
    val query by issueViewModel.query.collectAsState()
    val onlyMine by issueViewModel.showOnlyMyIssues.collectAsState()
    LaunchedEffect(projectId) {
        issueViewModel.setProject(projectId)
    }
    val categories = listOf("New", "Ongoing", "Completed")
    var filterMode by remember { mutableStateOf<IssueState?>(null) }

    LaunchedEffect(selectedTab) {
        val state = stateFromIndex(selectedTab)
        issueViewModel.setFilter(state)
        Log.d("IssueScreen", "Tab gewechselt: $selectedTab → setFilter($state)")
    }
    //val displayed = issueViewModel.getItemsForCategory(stateFromIndex(selectedTab))
    val displayed by issueViewModel.displayedIssues.collectAsState()
    val email by issueViewModel.emailsByIssue.collectAsState()

    /*val displayed = remember(baseList, query) {
        if (query.isBlank()) baseList
        else baseList.filter { it.title.contains(query, ignoreCase = true) }
    }*/

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
            // 1) Title

            item {

                EdgeToEdgeRoundedRightItemWithBadge(
                    viewName = projectName,
                    projectId = projectId,
                )
            }
            // 2) Search & filter row
            val sortOptions = listOf(
                SortOptions2("Sort by Date", IssueViewModel.SortField.DATE),
                SortOptions2("Sort by Name", IssueViewModel.SortField.NAME),
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
                           // z. B. ein gefülltes Icon
                    else
                        R.drawable.profile_group_svgrepo_com // z. B. ein Outline-Icon
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CustomIcon(
                            iconRes         = iconRes,
                            contentDesc     = "",
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            iconTint   = MaterialTheme.colorScheme.onSurface,
                            width           = 40.dp,
                            height          = 40.dp,
                            onClick         = { issueViewModel.setShowOnlyMine() },
                            modifier        = Modifier.padding(end = 8.dp)
                        )
                        IssueSortMenuSimple(issueViewModel,
                            sortOptions,
                            iconTint = MaterialTheme.colorScheme.onSurface,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            iconRes = R.drawable.sort_desc_svgrepo_com,
                            contentDesc = "Sort")
                    }
                }
            }
            // 3) Tabs row
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
            // 4) Issue list
            items(displayed, key = { it.id }) { item ->
                val mails: List<String?> = email[item.id].orEmpty()
                DragTarget(
                    dataToDrop = item,
                    viewModel  = issueViewModel
                ) {
                    IssueItemCard(
                        title        = item.title,
                        state        = stateToString(item.state),
                        date         = item.deadlineTS,
                        emailsState = mails,
                        assignments = item.assignments,
                        attachments  = 3,
                        projectId    = projectId,
                        issueId      = item.id,
                        avatarUris   = listOf(), // dummy or real
                        navController= navController,
                        issuelabels = item.labels,
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
    else -> throw IllegalArgumentException("Ungültiger Index für IssueState: $idx")
}

fun stateToString(state: IssueState): String = when (state) {
    IssueState.NEW     -> "New"
    IssueState.ONGOING -> "Ongoing"
    IssueState.DONE    -> "Done"
}


