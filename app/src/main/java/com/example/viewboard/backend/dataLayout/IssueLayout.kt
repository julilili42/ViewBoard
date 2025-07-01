package com.example.viewboard.backend.dataLayout

import com.google.firebase.firestore.DocumentId
import com.viewBoard.commonModule.Timestamp
import com.viewBoard.issueModule.IssueState

data class IssueLayout (
    @DocumentId
    var id: String = "",
    var title: String = "",
    var desc: String = "",
    var creator: String = "",
    var state: IssueState = IssueState.NEW,
    var assignments: ArrayList<String> = ArrayList<String>(),
    var labels: ArrayList<LabelLayout> = ArrayList<LabelLayout>(),
    var creationTS: Timestamp = Timestamp(),
    var deadlineTS: Timestamp = Timestamp()
)