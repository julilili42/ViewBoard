package com.example.viewboard.ui.screens

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import com.example.viewboard.ui.navigation.BottomBarScreen
import com.example.viewboard.ui.timetable.CustomIcon
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.components.view.ViewItem
import com.example.viewboard.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun ViewScreen(modifier: Modifier = Modifier, navController: NavController) {
    var showOnlyMyViews by remember { mutableStateOf(true) }
    val viewLayouts = remember { mutableStateListOf<ViewLayout>() }
    val creators = remember { mutableStateListOf<String>() }
    var error by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect (showOnlyMyViews) {
        try {
            val flow = if (showOnlyMyViews) FirebaseAPI.getViewsFromUser(AuthAPI.getUid())
            else FirebaseAPI.getAllViews()
            flow.collect { layouts ->
                viewLayouts.clear()
                viewLayouts.addAll(layouts)

                creators.clear()
                viewLayouts.forEach { view ->
                    val name = AuthAPI.getDisplayName(view.creator)
                    if (name != null)
                        creators.add(name)
                    else
                        creators.add("not found")
                }
            }
        } catch (e: Exception) {
            error = "loading error: ${e.localizedMessage}"
        }
    }

    if (showDialog) {
        var name by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()

        AlertDialog(
            title = { Text("Add View") },
            text = {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true,
                    )
                }
            },
            onDismissRequest = {
                showDialog = false
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    if (name.isNotBlank()) {
                        scope.launch {
                            FirebaseAPI.addView("Nql3AyhixcN2RN7M77Bm", ViewLayout(name = name, creator = AuthAPI.getUid()!!))
                        }
                    }
                }) { Text("Add") }
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
                name = AuthAPI.getDisplayName() ?: "failed to load username",
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
                    showDialog = true
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
                    text = "Views",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CustomIcon(
                        iconRes = R.drawable.filter_svgrepo_com__1,
                        contentDesc = stringResource(R.string.filter_svgrepo_com__1),
                        backgroundColor = if (showOnlyMyViews) Color.Green else Color.Gray,
                        iconTint = Color.White,
                        width = 40.dp,
                        height = 40.dp,
                        onClick = { showOnlyMyViews = !showOnlyMyViews },
                        modifier = Modifier
                    )
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                if (viewLayouts.isEmpty()) {
                    emptyList<ViewLayout>()
                } else {
                    viewLayouts
                }

                itemsIndexed(viewLayouts) { index, view ->
                    ViewItem (
                        name    = view.name,
                        creator = creators.getOrNull(index) ?: "loading...",
                        count   = view.issues.size,
                        color   = AppColors.StrongPalette[index % AppColors.StrongPalette.size],
                        onClick = {
                            navController.navigate(Screen.ViewIssueScreen.createRoute(view.name, view.id))
                        }
                    )
                }
            }
        }
    }
}
