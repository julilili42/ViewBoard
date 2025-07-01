package com.example.viewboard.ui.timetable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
val dummyIssues = listOf(
    LocalDate.of(2025, 6,  3),
    LocalDate.of(2025, 6,  3),
    LocalDate.of(2025, 6, 15),
    LocalDate.of(2025, 7,  1)
)

@Composable
fun MonthCalendar(
    year: Int,
    month: Int, // 1..12
    issues: List<LocalDate> = dummyIssues,
    modifier: Modifier = Modifier,
    selectedDate: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit = {}
) {
    val safeMonth = month.coerceIn(1, 12)
    val ym = YearMonth.of(year, safeMonth)
    val firstOfMonth = ym.atDay(1)
    val daysInMonth = ym.lengthOfMonth()

    val offset = (firstOfMonth.dayOfWeek.value % 7)
    val prevMonth = ym.minusMonths(1)
    val daysInPrev = prevMonth.lengthOfMonth()
    val prefixDates = (daysInPrev - offset + 1..daysInPrev).map { prevMonth.atDay(it) }

    val monthDates = (1..daysInMonth).map { ym.atDay(it) }

    val totalCellsSoFar = prefixDates.size + monthDates.size
    val rowsNeeded = (totalCellsSoFar + 6) / 7
    val totalCells = rowsNeeded * 7

    val nextMonth = ym.plusMonths(1)
    val suffixNeeded = totalCells - totalCellsSoFar
    val suffixDates = (1..suffixNeeded).map { nextMonth.atDay(it) }

    // ✅ Beschränke auf maximal 35 Zellen (= 5 Wochen)
    val maxDateCells = 35
    val allDates = (prefixDates + monthDates + suffixDates).take(maxDateCells)

    val weekdays = DayOfWeek.values().map {
        it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    val today = LocalDate.now()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier,
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false
    ) {
        // Wochentags-Kopf
        items(weekdays) { wd ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(wd, style = MaterialTheme.typography.bodySmall)
            }
        }

        // Maximal 35 Tage-Zellen
        items(allDates) { date ->
            val isCurrent = date.monthValue == safeMonth
            val issueCount = issues.count { it == date }
            val isTodayDate = date == today
            val isSelected = date == selectedDate

            val backgroundColor = when {
                isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                isTodayDate -> Color.Red.copy(alpha = 0.3f)
                isCurrent -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
            }

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Transparent,
                    )
                    .background(backgroundColor)
                    .padding(4.dp)
                    .clickable { onDateSelected(date) },
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )

                if (issueCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(20.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = issueCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}