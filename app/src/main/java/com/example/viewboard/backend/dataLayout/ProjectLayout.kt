package com.example.viewboard.backend.dataLayout

import com.example.viewboard.backend.Timestamp
import com.google.firebase.firestore.DocumentId

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
    var users: ArrayList<String> = ArrayList<String>(),
    var creationTS: Timestamp = Timestamp()
)