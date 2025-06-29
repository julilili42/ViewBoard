package com.example.viewboard.ui.timetable
import androidx.compose.foundation.background
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
    month: Int,
    issues: List<LocalDate> = dummyIssues,
    modifier: Modifier = Modifier
) {
    // 1) Berechne Prefix (Vormonat), Month, Suffix (Folgemonat) für 5 Wochen (35 Zellen)
    val ym           = YearMonth.of(year, month)
    val firstOfMonth = ym.atDay(1)
    val daysInMonth  = ym.lengthOfMonth()
    val offset       = (firstOfMonth.dayOfWeek.value % 7)

    val prevMonth    = ym.minusMonths(1)
    val daysInPrev   = prevMonth.lengthOfMonth()
    val prefixDates  = (daysInPrev - offset + 1..daysInPrev).map { prevMonth.atDay(it) }

    val monthDates   = (1..daysInMonth).map { ym.atDay(it) }

    val totalCells   = 35
    val nextMonth    = ym.plusMonths(1)
    val suffixNeeded = totalCells - (prefixDates.size + monthDates.size)
    val suffixDates  = (1..suffixNeeded).map { nextMonth.atDay(it) }

    val allDates = prefixDates + monthDates + suffixDates

    // 2) Wochentags‐Kürzel Mo–So
    val weekdays = DayOfWeek.values().map {
        it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    // 3) Grid zeichnen
    LazyVerticalGrid(
        columns               = GridCells.Fixed(7),
        modifier              = modifier,
        contentPadding        = PaddingValues(4.dp),
        verticalArrangement   = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled     = false
    ) {
        // 3a) Header mit Wochentagen
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

        // 3b) Datumszellen
        items(allDates) { date ->
            val isCurrent = date.monthValue == month
            val count     = issues.count { it == date }

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(
                        if (isCurrent)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.TopStart
            ) {
                // Tag-Nummer
                Text(
                    text  = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )

                // Issue-Count in kleinem Kreis unten rechts
                if (count > 0) {
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
                            text  = count.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DemoCalendarScreen() {
    MonthCalendar(
        year    = 2025,
        month   = 6,
        issues  = dummyIssues,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}
