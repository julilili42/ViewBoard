package com.example.viewboard.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.ui.navigation.Screen


@Composable
fun HomeScreenTopSection(modifier: Modifier = Modifier) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    Column(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.Projects),
            style = MaterialTheme.typography.headlineSmall,
            color = uiColor
        )
        Text(
            text = stringResource(R.string.User),
            style = MaterialTheme.typography.titleSmall,
            color = uiColor
        )
    }
}


@Composable
fun temp(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { navController.navigate(Screen.LoginScreen.route) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Back",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
fun ProjectGrid(
    projects: Map<Int, String>,
    title: String,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val texts = projects.toSortedMap().values.toList()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = textColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        texts.chunked(2).forEachIndexed { rowIndex, rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                when (rowItems.size) {
                    2 -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .border(2.dp, borderColor, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = rowItems[0],
                                style = MaterialTheme.typography.bodyLarge,
                                color = borderColor
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .border(2.dp, borderColor, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = rowItems[1],
                                style = MaterialTheme.typography.bodyLarge,
                                color = borderColor
                            )
                        }
                    }
                    1 -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .border(2.dp, borderColor, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = rowItems[0],
                                style = MaterialTheme.typography.bodyLarge,
                                color = borderColor
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
            if (rowIndex < texts.size / 2) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}



@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    val BlueGray = Color(0xFF1E293B)

    val uiColor = if (isSystemInDarkTheme()) BlueGray else Color.White

    val activeProjects = mapOf(
        1 to "Project 1",
        2 to "Project 2",
        3 to "Project 3",
    )

    val recentProjects = mapOf(
        1 to "Project 1",
        2 to "Project 2",
    )


    Surface {
        Column(modifier = Modifier.fillMaxSize().background(color = uiColor)) {
            HomeScreenTopSection()
            Spacer(modifier = Modifier.height(20.dp))
            ProjectGrid(projects = activeProjects, title = stringResource(R.string.ActiveProjects))
            Spacer(modifier = Modifier.height(20.dp))
            ProjectGrid(projects = recentProjects, title = stringResource(R.string.RecentProjects))
            temp(navController = navController)
        }
    }
}