package com.example.viewboard.ui.timetable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthYearPicker(
    year: Int,
    month: Int,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                if (month == 1) {
                    onYearChange(year - 1)
                    onMonthChange(12)
                } else {
                    onMonthChange(month - 1)
                }
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous month")
            }
            Text(
                text = Month.of(month)
                    .getDisplayName(TextStyle.FULL, Locale.getDefault()),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(onClick = {
                if (month == 12) {
                    onYearChange(year + 1)
                    onMonthChange(1)
                } else {
                    onMonthChange(month + 1)
                }
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next month")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onYearChange(year - 1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous year")
            }
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(onClick = { onYearChange(year + 1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next year")
            }
        }
    }
}
