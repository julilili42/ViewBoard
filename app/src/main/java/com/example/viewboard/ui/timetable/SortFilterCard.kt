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
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier           = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment  = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

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

