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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.backend.util.filterViewsByCreator
import com.example.viewboard.backend.util.filterViewsByProjects
import com.example.viewboard.backend.util.getProjectByView
import com.example.viewboard.components.view.ViewItem
import com.example.viewboard.ui.navigation.Screen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewScreen(modifier: Modifier = Modifier, navController: NavController) {
    var doUpdate by remember { mutableStateOf(true) }
    var showOnlyMyViews by rememberSaveable { mutableStateOf(true) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    val viewLayouts = remember { mutableStateListOf<ViewLayout>() }
    val allProjLayouts = remember { mutableStateListOf<ProjectLayout>() }
    val selectedProjLayouts = rememberSaveable { mutableStateOf(setOf<String>()) }
    val creators = remember { mutableStateListOf<String>() }
    var error by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val allProjFlow = remember { mutableStateListOf(FirebaseAPI.getAllProjects()) }

    LaunchedEffect (doUpdate, allProjFlow) {
        doUpdate = false

        try {
//            val allProjFlow = FirebaseAPI.getAllProjects()

            allProjLayouts.clear()
            allProjLayouts.addAll(allProjFlow.first().first()) // TODO updating bug fixed ???

            val allViewFlow = FirebaseAPI.getAllViews()

            var viewFlow = filterViewsByProjects(allViewFlow, selectedProjLayouts.value)

            if (showOnlyMyViews)
                viewFlow = filterViewsByCreator(viewFlow, AuthAPI.getUid())

            viewLayouts.clear()
            viewLayouts.addAll(viewFlow.first())

            creators.clear()

            viewLayouts.forEach { view ->
                val name = AuthAPI.getDisplayName(view.creator)

                if (name != null)
                    creators.add(name)
                else
                    creators.add("not found")
            }
        } catch (e: Exception) {
            error = "loading error: ${e.localizedMessage}"
        }
    }

    if (showDialog) {
        var name by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()

        if (selectedProjLayouts.value.size != 1) {
            AlertDialog(
                title = { Text("Exactly one project must be selected!") },
                onDismissRequest = {
                    showDialog = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                    }) { Text("OK") }
                }
            )
        }
        else {
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
                                FirebaseAPI.addView(selectedProjLayouts.value.elementAt(0), ViewLayout(name = name, creator = AuthAPI.getUid()!!))
                                doUpdate = !doUpdate
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

                    ExposedDropdownMenuBox(
                        expanded = showDropdownMenu,
                        onExpandedChange = { showDropdownMenu = it },
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = "Select Projects",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDropdownMenu) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = showDropdownMenu,
                            onDismissRequest = {
                                showDropdownMenu = false
                                doUpdate = !doUpdate
                            }
                        ) {
                            allProjLayouts.forEach { projLayout ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(
                                                checked = projLayout.id in selectedProjLayouts.value,
                                                onCheckedChange = null
                                            )
                                            Spacer(Modifier.width(10.dp))
                                            Text(projLayout.name)
                                        }
                                    },
                                    onClick = {
                                        selectedProjLayouts.value = if(projLayout.id in selectedProjLayouts.value) {
                                            selectedProjLayouts.value - projLayout.id
                                        }
                                        else {
                                            selectedProjLayouts.value + projLayout.id
                                        }
                                    }
                                )
                            }
                        }
                    }
                    CustomIcon(
                        iconRes = R.drawable.filter_svgrepo_com__1,
                        contentDesc = stringResource(R.string.filter_svgrepo_com__1),
                        backgroundColor = if (showOnlyMyViews) Color.Green else Color.Gray,
                        iconTint = Color.White,
                        width = 50.dp,
                        height = 50.dp,
                        onClick = {
                            showOnlyMyViews = !showOnlyMyViews
                            doUpdate = !doUpdate
                        },
                        modifier = Modifier
                    )

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
                        view = view,
                        creator = creators.getOrNull(index) ?: "loading...",
                        color   = AppColors.StrongPalette[index % AppColors.StrongPalette.size],
                        onClick = {
                            val projID = getProjectByView(view.id, allProjLayouts)

                            if(projID != null)
                                navController.navigate(Screen.ViewIssueScreen.createRoute(view.id, projID))
                        }
                    )
                }
            }
        }
    }
}
