package com.example.viewboard.backend.dataLayout

import com.google.firebase.firestore.DocumentId
import com.example.viewboard.backend.util.Timestamp

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
    var users: ArrayList<String> = ArrayList<String>(),
    var creationTS: String = Timestamp().export(),
    var deadlineTS: String = Timestamp().export()
)