package com.example.viewboard.backend.data

import com.google.firebase.firestore.DocumentId
import com.example.viewboard.backend.util.Timestamp
import com.example.viewboard.stateholder.IssueViewModel

// current progress state of an issue.
enum class IssueState {
    NEW,
    ONGOING,
    DONE
}

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

data class IssueLayout (
    @DocumentId
    var id: String = "",
    var title: String = "",
    var desc: String = "",
    var creator: String = "",
    var projectid: String = "",
    var state: IssueState = IssueState.NEW,
    // List of Assignees
    var assignments: ArrayList<String> = ArrayList<String>(),
    var labels: ArrayList<String> = ArrayList<String>(),
    // Timestamp Creation: Time of creation
    var creationTS: String = Timestamp().export(),
    // Timestamp Deadline: Time of deadline
    var deadlineTS: String = Timestamp().export()
)