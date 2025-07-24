package com.example.viewboard.ui.screens.view

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
import com.example.viewboard.components.homeScreen.ProfileHeader
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import com.example.viewboard.ui.navigation.BottomBarScreen
import com.example.viewboard.ui.timetable.CustomIcon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import com.example.viewboard.components.view.ViewItem
import androidx.compose.foundation.lazy.grid.items
import com.example.viewboard.stateholder.IssueViewModel
import com.example.viewboard.stateholder.ProjectViewModel
import com.example.viewboard.stateholder.ViewsViewModel
import com.example.viewboard.ui.navigation.Screen
import com.example.viewboard.ui.utils.CustomSearchField
import com.example.viewboard.ui.utils.ViewSortMenuSimple
import com.example.viewboard.ui.views.EdgeToEdgeRoundedRightItemWithBadge
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewScreen(
    navController: NavController,
    viewsViewModel: ViewsViewModel,
    issueViewModel: IssueViewModel,
    projectViewModel: ProjectViewModel,
) {

    val viewLayouts by viewsViewModel.displayedViews.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val uid = AuthAPI.getUid() ?: return
    val query by viewsViewModel.query.collectAsState()
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
                EdgeToEdgeRoundedRightItemWithBadge(
                    viewName = "Views",
                )
            }
            // Header, Search, Filter
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    query?.let {
                        CustomSearchField(
                            query = it,
                            onQueryChange = { viewsViewModel.setQuery(it) },
                            modifier = Modifier
                                .height(40.dp)
                                .width(200.dp),
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ViewSortMenuSimple(
                            viewViewModel = viewsViewModel,
                            iconRes          = R.drawable.sort_desc_svgrepo_com,
                            backgroundColor  = MaterialTheme.colorScheme.primary,
                            iconTint  = Color.White,
                            contentDesc      = stringResource(R.string.filter_svgrepo_com__1),
                            modifier         = Modifier.size(40.dp)
                        )
                    }
                }
            }
            items(viewLayouts) { view ->
                ViewItem(
                    view = view,
                    creator = view.creator,
                    color = Color.Gray,
                    onClick = {
                        navController.navigate(
                            Screen.ViewIssueScreen.createRoute(view.id, "", view.name)
                        )
                    },
                    onDelete = { viewId ->
                        coroutineScope.launch {
                            FirebaseAPI.rmView(AuthAPI.getUid(), viewId)
                        }
                    }
                )
            }
        }

        // Dialog to create new View
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("New View") },
                text = {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("View Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        if (newName.isNotBlank()) {
                            coroutineScope.launch {
                                AuthAPI.getUid()?.let {
                                    FirebaseAPI.addView(
                                        AuthAPI.getUid(),
                                        ViewLayout(
                                            name = newName,
                                            creationTS = System.currentTimeMillis().toString(),
                                            creator = uid,
                                            issues = ArrayList()
                                        ),
                                        onSuccess = { _ ->  },
                                        onFailure = { err -> /* TODO handle error*/ }
                                    )
                                }
                            }
                        }
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}



