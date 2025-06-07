package com.example.viewboard.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.ui.navigation.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    modifier: Modifier = Modifier,
    projectName: String,
    navController: NavController
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(projectName) },
                navigationIcon = {
                    BackButton(
                        text = stringResource(R.string.Back),
                        onClick = { navController.popBackStack() })
                }
            )
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center
        ) {
            Text(projectName)
        }
    }
}