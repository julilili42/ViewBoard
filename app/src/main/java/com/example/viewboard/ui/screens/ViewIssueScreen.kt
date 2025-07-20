package com.example.viewboard.ui.screens

import android.net.Uri
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
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.components.homeScreen.ProfileHeader
import com.example.viewboard.ui.issue.IssueItemCard
import com.example.viewboard.ui.issue.IssueViewModel
import com.example.viewboard.ui.issue.MainViewModel
import com.example.viewboard.ui.navigation.BottomBarScreen
import com.example.viewboard.ui.timetable.CustomIcon
import com.example.viewboard.ui.timetable.CustomIcon2
import kotlinx.coroutines.launch

@Composable
fun ViewIssueScreen(mainViewModel: IssueViewModel, navController: NavController, viewID: String, projID: String) {
    var showDialog by remember { mutableStateOf(false) }

    val categories = listOf("New", "Ongoing", "Done")
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(viewID) {
        mainViewModel.loadIssuesFromView(viewID)
    }

    if (showDialog) {
        val scope = rememberCoroutineScope()

        AlertDialog(
            title = { Text("Delete View") },
            onDismissRequest = {
                showDialog = false
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    scope.launch {
                        FirebaseAPI.rmView(projID, viewID)

                        navController.navigateUp()
                    }
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                }) { Text("Cancel") }
            }
        )
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
                onClick = {

                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "View - Issues",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CustomIcon2(
                        iconRes = Icons.Default.Delete,
                        contentDesc = "Delete",
                        backgroundColor = Color.Transparent,
                        iconTint = Color.Black,
                        width = 40.dp,
                        height = 40.dp,
                        onClick = { showDialog = true },
                        modifier = Modifier
                    )
                }
            }

            DragableScreen {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        categories.forEachIndexed { idx, label ->
                            DropItem<IssueLayout>(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .padding(horizontal = 4.dp),
                                onDrop = { item ->
                                    val state = stateFromIndex(idx)
                                    mainViewModel.moveItemToState(item, state)
                                }
                            ) { isOver, _ ->
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when {
                                                isOver -> MaterialTheme.colorScheme.primary.copy(
                                                    alpha = 0.3f
                                                )

                                                selectedTab == idx -> MaterialTheme.colorScheme.primary
                                                else -> Color.Transparent
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            if (isOver) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedTab = idx },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (selectedTab == idx)
                                            MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val dummyAvatarUris = listOf(
                            Uri.parse("https://picsum.photos/seed/1/64"),
                            Uri.parse("https://picsum.photos/seed/2/64"),
                            Uri.parse("https://picsum.photos/seed/3/64"),
                            Uri.parse("https://picsum.photos/seed/4/64"),
                            Uri.parse("https://picsum.photos/seed/5/64")
                        )
                        mainViewModel.getItemsForCategory(stateFromIndex(selectedTab)).forEach { item ->
                            key(item.id) {
                                DragTarget(
                                    dataToDrop = item,
                                    viewModel = mainViewModel
                                ) {
                                    IssueItemCard(
                                        title = item.title,
                                        state = stateToString(item.state),
                                        projectId = projID,
                                        issueId = item.id,
                                        date = item.deadlineTS,
                                        attachments = 5,
                                        avatarUris = dummyAvatarUris,
                                        navController = navController,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


