package com.example.viewboard.components.homeScreen

import android.util.Log
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.IssueState
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields

/**
 * Mögliche Zeitspannen für das Deadline‑Filter.
 */
enum class TimeSpanFilter(val label: String, val short: String) {
    ALL_TIME("All time", "A"),
    CURRENT_YEAR("Yearly", "Y"),
    CURRENT_MONTH("Monthly", "M"),
    CURRENT_WEEK("Weekly", "W"),
}


fun TimeSpanFilter.next(): TimeSpanFilter = when (this) {
    TimeSpanFilter.CURRENT_YEAR  -> TimeSpanFilter.CURRENT_MONTH
    TimeSpanFilter.CURRENT_MONTH -> TimeSpanFilter.CURRENT_WEEK
    TimeSpanFilter.CURRENT_WEEK  -> TimeSpanFilter.CURRENT_YEAR
    TimeSpanFilter.ALL_TIME -> TimeSpanFilter.ALL_TIME
}
data class IssueProgress(
    val totalIssues: Int,
    val completedIssues: Int,
    val percentComplete: Float // 0.0 .. 100.0
)

class IssueProgressCalculator {

    /**
     * Liefert den Fortschritt für alle Issues, deren deadlineTS
     * innerhalb der angegebenen TimeSpanFilter liegt.
     *
     * @param filterMode: CREATED, SHARED oder ALL
     * @param timeSpan: CURRENT_YEAR, CURRENT_MONTH oder CURRENT_WEEK
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getProgressFlow(
        timeSpan: TimeSpanFilter
    ): Flow<IssueProgress> {
        val userId = AuthAPI.getUid()

        // 1) Berechne Zeitfenster in Millisekunden
        val (fromTs, toTs) = calculateWindow(timeSpan)

        // 2) Projekte‑Flow je nach FilterMode
        val projectsFlow: Flow<List<ProjectLayout>> =
                FirebaseAPI.getProjectsFromUser(userId)



        // 3) FlatMap und combine wie gehabt, aber mit Deadline‑Filter
        return projectsFlow
            .flatMapLatest { projects ->
                val issueFlows = projects.map { project ->
                    FirebaseAPI.getIssuesFromUser(userId, project.id)
                }

                if (issueFlows.isEmpty()) {
                    flowOf(IssueProgress(0, 0, 0.0f))

                } else {
                    combine(issueFlows) { lists ->
                        val filtered = lists
                            .asList()
                            .flatten()
                        val filteredIssues: List<IssueLayout> = filtered.filter { issue ->
                            // denk dran: deadlineTS ist ein String, daher vorher parsen
                            val millis = Instant.parse(issue.deadlineTS).toEpochMilli()
                            Log.d(
                                "IssueProgressFilter",
                                "fromTs=$fromTs, toTs=$toTs, issueId=${issue.id}, millis=$millis"
                            )
                            millis in fromTs..toTs

                        }
                        val total = filteredIssues.size
                        val completed = filteredIssues.count { it.state == IssueState.DONE }
                        val percent = if (total > 0.0f) (completed * 100.0f / total) else 0.0f

                        IssueProgress(total, completed, percent)
                    }
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getProjectProgressFlow(
        projectId: String,
        timeSpan: TimeSpanFilter
    ): Flow<IssueProgress> {
        val userId = AuthAPI.getUid()
        val (fromTs, toTs) = calculateWindow(timeSpan)

        // 1) Nur dieser eine Flow<List<IssueLayout>>
        return FirebaseAPI
            .getIssuesFromUser(userId, projectId)
            .map { list ->
                // 2) Filter direkt nach Deadline
                val filtered = list.filter { issue ->
                    val millis = Instant.parse(issue.deadlineTS).toEpochMilli()
                    millis in fromTs..toTs
                }
                // 3) Zähle Gesamt und DONE
                val total     = filtered.size
                val completed = filtered.count { it.state == IssueState.DONE }
                val percent   = if (total > 0) completed * 100f / total else 0f
                IssueProgress(total, completed, percent)
            }
    }
    /**
     * Erzeugt aus dem TimeSpanFilter das (fromTs, toTs)-Tupel in Epoch‑Millis.
     */
    private fun calculateWindow(timeSpan: TimeSpanFilter): Pair<Long, Long> {
        val zone = ZoneId.systemDefault()
        val now  = ZonedDateTime.now(zone)

        return when (timeSpan) {
            TimeSpanFilter.ALL_TIME -> {
                // Unbegrenzte Zeitspanne: von der Unix‑Epoch bis ins Unendliche
                0L to Long.MAX_VALUE
            }
            TimeSpanFilter.CURRENT_YEAR -> {
                val start = now
                    .with(TemporalAdjusters.firstDayOfYear())
                    .truncatedTo(ChronoUnit.DAYS)
                    .toInstant()
                    .toEpochMilli()
                val end = now
                    .with(TemporalAdjusters.lastDayOfYear())
                    .with(LocalTime.MAX)
                    .toInstant()
                    .toEpochMilli()
                start to end
            }
            TimeSpanFilter.CURRENT_MONTH -> {
                val start = now
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .truncatedTo(ChronoUnit.DAYS)
                    .toInstant()
                    .toEpochMilli()
                val end = now
                    .with(TemporalAdjusters.lastDayOfMonth())
                    .with(LocalTime.MAX)
                    .toInstant()
                    .toEpochMilli()
                start to end
            }
            TimeSpanFilter.CURRENT_WEEK -> {
                val firstDayOfWeek = WeekFields.ISO.firstDayOfWeek
                val startOfWeek = now
                    .with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
                    .truncatedTo(ChronoUnit.DAYS)
                val endOfWeek = startOfWeek
                    .plusDays(6)
                    .with(LocalTime.MAX)
                startOfWeek.toInstant().toEpochMilli() to endOfWeek.toInstant().toEpochMilli()
            }
        }
    }
}


