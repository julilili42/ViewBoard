package com.example.viewboard.ui.timetable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.viewboard.R
import androidx.compose.runtime.remember

import androidx.compose.material3.Text

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.setValue

import androidx.compose.runtime.saveable.rememberSaveable
import com.example.viewboard.components.Timetable.SegmentedSwitch


@Composable
fun SortFilterCard(
    onSearchClick: () -> Unit,
    onSortClick:   () -> Unit,
    onFilterClick: () -> Unit,
    modifier:      Modifier = Modifier
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()      // volle Breite
            .height(60.dp),      // höhere Card

       // shape     = MaterialTheme.shapes.medium,
        //elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier           = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment  = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Such-Icon ganz links
            /*IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(32.dp)    // größere Icons
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    imageVector        = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search),
                    modifier           = Modifier.fillMaxSize(),
                    tint               = MaterialTheme.colorScheme.surfaceVariant
                )
            }*/
            // 1) State für das Textfeld
            var query by rememberSaveable { mutableStateOf("") }

            // 2) Beispiel-Ergebnisse (könnten aus ViewModel kommen)
            val allItems = listOf("Apple","Banana","Cherry","Date","Elderberry")
            // einfache Filter-Logik
            val searchResults = remember(query) {
                if (query.isBlank()) emptyList()
                else allItems.filter { it.contains(query, ignoreCase = true) }
            }
          /*  CustomizableSearchBar(
                query           = query,
                onQueryChange   = { query = it },
                onSearch        = { /* z.B. Keyboard-Search: hier könntest du ein ViewModel triggern */ },
                searchResults   = searchResults,
                onResultClick   = { selected ->

                },
                // Du kannst hier auch placeholder & Icons anpassen:
                placeholder     = { Text("Zutaten durchsuchen") },
                leadingIcon     = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon    = null,
                supportingContent = { item ->
                    Text("Extra Info zu $item")
                },
                leadingContent  = null,
                modifier = Modifier
                    .fillMaxWidth(0.6f)   // füllt nur 80% der übergeordneten Breite
                    .height(48.dp)        // auf 48dp Höhe begrenzt
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            )*/



            Spacer(modifier = Modifier.weight(1f))

            // Sort-Icon
            IconButton(
                onClick = onSortClick,
                modifier = Modifier
                    .size(14.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    painter           = painterResource(id = R.drawable.sort_desc_svgrepo_com),
                    contentDescription= stringResource(R.string.sort_desc_svgrepo_com),
                    modifier          = Modifier.fillMaxSize(),
                    tint              = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(1.dp))
            // Filter-Icon
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier
                    .size(14.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    painter           = painterResource(id = R.drawable.filter_svgrepo_com__1),
                    contentDescription= stringResource(R.string.filter_svgrepo_com__1),
                    modifier          = Modifier.fillMaxSize(),
                    tint              = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.width(11.dp))
    }
}

