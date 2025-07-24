package com.example.viewboard.backend.dataLayout

import com.google.firebase.firestore.DocumentId
import com.example.viewboard.backend.util.Timestamp

// current progress state of an issue.
enum class IssueState {
    NEW,
    ONGOING,
    DONE
}

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