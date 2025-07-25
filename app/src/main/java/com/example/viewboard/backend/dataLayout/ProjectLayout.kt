package com.example.viewboard.backend.dataLayout

import com.google.firebase.firestore.DocumentId
import com.example.viewboard.backend.time.Timestamp

/**
 * @property id the id of the project
 * @property name the name of the project
 * @property creator the user who creates the project
 * @property issues the issues associated with the project
 * @property users the users associated with the project
 * @property creationTS the creation timestamp of the project
 * @property startTS the start timestamp of the project
 * @property deadlineTS the deadline timestamp of the project
 */
data class ProjectLayout(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var creator: String = "",
    var issues: ArrayList<String> = ArrayList<String>(),
    var users: ArrayList<String> = ArrayList<String>(),
    var creationTS: String = Timestamp().export(),
    var startTS: String = Timestamp().export(),
    var deadlineTS: String = Timestamp().export()
)