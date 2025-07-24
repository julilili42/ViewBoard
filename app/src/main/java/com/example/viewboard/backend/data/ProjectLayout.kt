package com.example.viewboard.backend.data

import com.google.firebase.firestore.DocumentId
import com.example.viewboard.backend.util.Timestamp

data class ProjectLayout (
    @DocumentId
    var id: String = "",
    var name: String = "",
    var desc: String = "",
    var creator: String = "",
    var phase: String = "",
    var startMonth: Int = 0, // 1–12
    var endMonth: Int = 0,   // 1–12
    var totalMilestones: Int = 0,
    var completedMilestones: Float = 0f,
    var issues: ArrayList<String> = ArrayList<String>(),
    var labels: ArrayList<String> = ArrayList<String>(),
    var views: ArrayList<String> = ArrayList<String>(),
    var users: ArrayList<String> = ArrayList<String>(),
    // Timestamp Creation: Time of creation
    var creationTS: String = Timestamp().export(),
    // Timestamp Start: Time of project start
    var startTS: String = Timestamp().export(),
    // Timestamp Deadline: Time of project deadline
    var deadlineTS: String = Timestamp().export()
)