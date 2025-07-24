package com.example.viewboard.components.homeScreen.issueProgress

import android.util.Log
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.IssueDeadlineFilter
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.IssueProgress
import com.example.viewboard.backend.dataLayout.IssueState
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields

fun IssueDeadlineFilter.next(): IssueDeadlineFilter = when (this) {
    IssueDeadlineFilter.CURRENT_YEAR  -> IssueDeadlineFilter.CURRENT_MONTH
    IssueDeadlineFilter.CURRENT_MONTH -> IssueDeadlineFilter.CURRENT_WEEK
    IssueDeadlineFilter.CURRENT_WEEK  -> IssueDeadlineFilter.CURRENT_YEAR
    IssueDeadlineFilter.ALL_TIME -> IssueDeadlineFilter.ALL_TIME
}

class IssueProgressCalculator {
    /**
     * Returns a Flow emitting overall progress (total, done count, percentage)
     * across all projects of the current user, filtered by the given time span.
     *
     * @param timeSpan Defines the date window for filtering issue deadlines.
     * @return Flow of IssueProgress containing total, completed, and percent.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getProgressFlow(
        timeSpan: IssueDeadlineFilter
    ): Flow<IssueProgress> {
        val userId = AuthAPI.getUid()
        val (fromTs, toTs) = calculateWindow(timeSpan)

        val projectsFlow: Flow<List<ProjectLayout>> =
                FirebaseAPI.getProjectsFromUser(userId)



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

    /**
     * Returns a Flow emitting progress for a single project,
     * filtered by the given time span.
     *
     * @param projectId The ID of the project to analyze.
     * @param timeSpan  Defines the date window for filtering issue deadlines.
     * @return Flow of IssueProgress for that project.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getProjectProgressFlow(
        projectId: String,
        timeSpan: IssueDeadlineFilter
    ): Flow<IssueProgress> {
        val userId = AuthAPI.getUid()
        val (fromTs, toTs) = calculateWindow(timeSpan)

        return FirebaseAPI
            .getIssuesFromUser(userId, projectId)
            .map { list ->
                val filtered = list.filter { issue ->
                    val millis = Instant.parse(issue.deadlineTS).toEpochMilli()
                    millis in fromTs..toTs
                }
                val total     = filtered.size
                val completed = filtered.count { it.state == IssueState.DONE }
                val percent   = if (total > 0) completed * 100f / total else 0f
                IssueProgress(total, completed, percent)
            }
    }

    /**
     * Calculates the start and end timestamps (in ms) for the specified time span.
     *
     * @param timeSpan The desired window (all time, year, month, or week).
     * @return Pair of (fromTimestamp, toTimestamp) in epoch milliseconds.
     */
    private fun calculateWindow(timeSpan: IssueDeadlineFilter): Pair<Long, Long> {
        val zone = ZoneId.systemDefault()
        val now  = ZonedDateTime.now(zone)

        return when (timeSpan) {
            IssueDeadlineFilter.ALL_TIME -> {
                0L to Long.MAX_VALUE
            }
            IssueDeadlineFilter.CURRENT_YEAR -> {
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
            IssueDeadlineFilter.CURRENT_MONTH -> {
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
            IssueDeadlineFilter.CURRENT_WEEK -> {
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


