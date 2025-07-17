import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.IssueState
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 1) Gesamt‑Fortschritt eines Projekts:
 *    Zählt alle Issues und wie viele davon den State DONE haben.
 *
 * @param projectId die Projekt‑ID
 * @return Pair(totalIssues, completedIssues)
 */
suspend fun calculateProjectProgress(
    projectId: String
): Pair<Int, Int> {
    // Flow aller Issue‑Listen im Projekt, onSuccess/onFailure hier leer
    val issues: List<IssueLayout> = FirebaseAPI
        .getIssuesFromProject(
            projID    = projectId,
        )
        .first()                // entnimmt die erste (aktuelle) Liste

    val total     = issues.size
    val completed = issues.count { it.state == IssueState.DONE }
    return total to completed
}

/**
 * 2) Projekt‑Progress in einem Zeitfenster:
 *    Zählt nur Issues, deren createdTS in [from, to] fällt.
 *
 * @param projectId    die Projekt‑ID
 * @param fromInclusive Beginn (inklusive)
 * @param toInclusive   Ende (inklusive)
 * @return Pair(totalInRange, completedInRange)
 */
suspend fun calculateProjectProgressInPeriod(
    projectId: String,
    fromInclusive: Instant,
    toInclusive: Instant
): Pair<Int, Int> {
    val issues: List<IssueLayout> = FirebaseAPI
        .getIssuesFromProject(
            projID    = projectId,
        )
        .first()

    // parse createdTS (ISO‑8601) und filtere
    val inRange = issues.filter { issue ->
        val createdInstant = Instant.parse(issue.creationTS)
            .atZone(ZoneId.systemDefault())
            .toInstant()
        !createdInstant.isBefore(fromInclusive) && !createdInstant.isAfter(toInclusive)
    }

    val total     = inRange.size
    val completed = inRange.count { it.state == IssueState.DONE }
    return total to completed
}
