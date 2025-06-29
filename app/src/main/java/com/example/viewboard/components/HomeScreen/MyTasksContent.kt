package com.example.viewboard.components.HomeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.R
import java.time.LocalDateTime

/**
 * Der reine Content-Teil von MyTasksScreen,
 * ohne Scaffold und TopBar – ideal für ein BottomSheet.
 *
 * @param navController Navigation Controller zum Navigieren bei Klick
 * @param myTasks       Liste von (Task-Name, Fälligkeitszeit)-Pairs
 * @param onSortClick   Callback für den Sort/Filter-Button
 */
@Composable
fun MyTasksContent(
    navController: NavController,
    myTasks: List<Pair<String, LocalDateTime>>,
    onSortClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.my_tasks), // Leg dafür in strings.xml <string name="my_tasks">My Tasks</string> an
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onSortClick) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = stringResource(R.string.sort_tasks) // z.B. <string name="sort_tasks">Sort Tasks</string>
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(myTasks) { (name, dueDateTime) ->
                ProjectCardTasks(
                    name = name,
                    dueDateTime = dueDateTime,
                    onClick = { navController.navigate("taskDetail/$name") },
                    onMenuClick = {}
                )
            }
        }
    }
}
