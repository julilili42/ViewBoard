package com.example.viewboard.ui.screens


import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.components.HomeScreen.ProfileHeader
import com.example.viewboard.ui.issue.IssueItemCard
import com.example.viewboard.ui.issue.IssueUiItem
import com.example.viewboard.ui.issue.MainViewModel
import com.example.viewboard.ui.navigation.BottomBarScreen
import com.example.viewboard.ui.navigation.Screen
import com.example.viewboard.ui.project.CustomSearchField
import com.example.viewboard.ui.timetable.CustomIcon




@Composable
fun IssueScreen(mainViewModel: MainViewModel, navController: NavController,projectName:String,projectId : String, modifier: Modifier = Modifier)  {

    LaunchedEffect(projectId) {
        mainViewModel.loadMyIssues(projectId)
    }

    val categories = listOf("New", "Ongoing", "Completed")
    var selectedTab by remember { mutableStateOf(0) }


    Scaffold(
        topBar = {
            ProfileHeader(
                name = "Raoul",
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
                onClick = {navController.navigate(Screen.IssueCreationScreen.route)},

                )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Text(
                text = projectName +" - Issues", // z.B. "My Projects"
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
            // Aktionsleiste unter der Überschrift
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                var query by remember { mutableStateOf("") }
                val items = listOf("Apple", "Banana", "Cherry").filter {
                    it.contains(query, ignoreCase = true)
                }
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
                        onClick = {},
                        modifier = Modifier

                    )
                    CustomIcon(
                        iconRes = R.drawable.filter_svgrepo_com__1,
                        contentDesc = stringResource(R.string.filter_svgrepo_com__1),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        iconTint = Color.White,
                        width = 40.dp,
                        height = 40.dp,
                        onClick = {},
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
            // Tabs als Drop-Ziele
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                categories.forEachIndexed { idx, label ->
                    DropItem<IssueUiItem>(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(horizontal = 4.dp),
                        onDrop = { item ->
                            // einzig relevante Logik: Kategorie ändern
                            mainViewModel.moveItemToCategory(item, idx)
                        }
                    ) { isOver, _ ->
                        Box(
                            Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isOver -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
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
               mainViewModel.getItemsForCategory(selectedTab).forEach { item ->
                    key(item.id) {      // <-- HIER der wichtigste Schritt
                        DragTarget(
                            dataToDrop = item,
                            viewModel = mainViewModel
                        ) {
                            IssueItemCard(
                                title = item.title,
                                priority = item.priority,
                                status = item.status,
                                date = item.date,
                                attachments = item.attachments,
                                comments = item.comments,
                                avatarUris = dummyAvatarUris,
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

