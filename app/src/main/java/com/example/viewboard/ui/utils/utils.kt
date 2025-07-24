package com.example.viewboard.ui.utils

import android.content.res.Resources
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.components.homeScreen.TimeSpanFilter
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun formatGermanShortDate(input: String): String {
    val datePart = input
        .substringBefore('T')
        .substringBefore(' ')
        .trim()

    val date = LocalDate.parse(datePart)
    val formatter = DateTimeFormatter.ofPattern("dd. MMM yy", Locale.GERMAN)

    return date.format(formatter)
}

fun formatRemaining(isoTimestamp: String): String {
    val instantUtc = Instant.parse(isoTimestamp).plus(1, ChronoUnit.HOURS)

    val now = Instant.now()
    if (instantUtc.isBefore(now)) {
        return "expired"
    }

    val duration = Duration.between(now, instantUtc)

    return if (duration.toHours() < 24) {
        val hours = duration.toHours().toInt().coerceAtLeast(0)
        when (hours) {
            0    -> "expired"
            1    -> "1 hour"
            else -> "$hours hours"
        }
    } else {
        val days = duration.toDays().toInt()
        when (days) {
            0    -> "expired"
            1    -> "1 day"
            else -> "$days days"
        }
    }
}

fun emailToInitials(email: String): String {
    val local = email.substringBefore('@', "").lowercase()
    val separators = arrayOf(".", "_", "-")
    val parts = local.split(
        *separators,
        ignoreCase = true
    ).filter { it.isNotBlank() }
    val initials = when {
        parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}"
        local.length >= 2 -> "${local[0]}${local[1]}"
        local.length == 1 -> "${local[0]}"
        else -> "??"
    }

    return initials.uppercase()
}

fun String.capitalizeWords(): String =
    this
        .split(Regex("\\s+"))
        .joinToString(" ") { word ->
            word
                .lowercase()
                .replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
        }

fun Float.toDp(): Dp =
    (this / Resources.getSystem().displayMetrics.density).dp

fun extractIssueDateTimes(issues: List<IssueLayout>): List<OffsetDateTime> {
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    return issues.mapNotNull { issue ->
        runCatching {
            OffsetDateTime.parse(issue.deadlineTS, formatter)
        }.getOrNull()
    }
}

fun gradientColorList(
    startColor: Color,
    endColor: Color,
    steps:      Int
): List<Color> {
    require(steps > 0) { "steps must be > 0" }
    return List(steps + 1) { i ->
        val fraction = i / steps.toFloat()      // 0f .. 1f
        lerp(startColor, endColor, fraction)
    }
}

fun dayOfYearFromIso(
    dateTimeStr: String,
    monthOffset: Long = 0L
): Int {
    // Only use date part
    val datePart = dateTimeStr
        .substringBefore('T')
        .substringBefore(' ')
        .trim()

    // parse and month offset
    val adjusted = LocalDate.parse(datePart)
        .plusMonths(monthOffset)

    return adjusted.dayOfYear
}

fun TimeSpanFilter.next(): TimeSpanFilter = when (this) {
    TimeSpanFilter.CURRENT_YEAR  -> TimeSpanFilter.CURRENT_MONTH
    TimeSpanFilter.CURRENT_MONTH -> TimeSpanFilter.CURRENT_WEEK
    TimeSpanFilter.CURRENT_WEEK  -> TimeSpanFilter.CURRENT_YEAR
    TimeSpanFilter.ALL_TIME -> TimeSpanFilter.ALL_TIME
}
