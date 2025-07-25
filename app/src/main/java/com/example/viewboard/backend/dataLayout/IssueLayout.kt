package com.example.viewboard.backend.dataLayout

import com.google.firebase.firestore.DocumentId
import com.example.viewboard.backend.util.Timestamp
import com.example.viewboard.stateholder.IssueViewModel

/**
 * Current progress state of an issue
 *
 * @property NEW new means, the initial state of a created issue
 * @property ONGOING ongoing means, the issue is currently in work
 * @property DONE done means, the issue is done
 */
enum class IssueState {
    NEW,
    ONGOING,
    DONE
}
data class EmailWithId(val userId: String, val mail: String?)

// issue progress tracking
data class IssueProgress(
    val totalIssues: Int,
    val completedIssues: Int,
    val percentComplete: Float
)

// filter for issue deadline time spans
enum class IssueDeadlineFilter(val label: String, val short: String) {
    ALL_TIME("All time", "A"),
    CURRENT_YEAR("Yearly", "Y"),
    CURRENT_MONTH("Monthly", "M"),
    CURRENT_WEEK("Weekly", "W"),
}

// options for sorting
data class SortOptionsIssues(
    val label: String,
    val field: IssueViewModel.SortField
)

/**
 * @property id the id of the project
 * @property title the title of the project
 * @property desc the desc of the project
 * @property creator the user who creates the issue
 * @property projID the project id associated with the issue
 * @property state the state of the issue
 * @property users the users associated with the issue
 * @property creationTS the creation timestamp of the issue
 * @property deadlineTS the deadline timestamp of the issue
 */
data class IssueLayout (
    @DocumentId
    var id: String = "",
    var title: String = "",
    var desc: String = "",
    var creator: String = "",
    var projID: String = "",
    var state: IssueState = IssueState.NEW,
    var labels: ArrayList<String> = ArrayList<String>(),
    var users: ArrayList<String> = ArrayList<String>(),
    var creationTS: String = Timestamp().export(),
    var deadlineTS: String = Timestamp().export()
)