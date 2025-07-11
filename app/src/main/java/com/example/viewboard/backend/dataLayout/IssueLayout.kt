package com.example.viewboard.backend.dataLayout

import com.google.firebase.firestore.DocumentId
import com.example.viewboard.backend.Timestamp

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
    var state: IssueState = IssueState.NEW,
    var assignments: ArrayList<String> = ArrayList<String>(),
    var labels: ArrayList<String> = ArrayList<String>(),
    var creationTS: Timestamp = Timestamp(),
    var deadlineTS: Timestamp = Timestamp()
)