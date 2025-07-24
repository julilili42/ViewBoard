package com.example.viewboard.backend.data

import com.google.firebase.firestore.DocumentId
import com.example.viewboard.backend.util.Timestamp

data class ViewLayout (
    @DocumentId
    var id: String = "",
    var name: String = "",
    var creator: String = "",
    // issues connected to view
    var issues: ArrayList<String> = ArrayList<String>(),
    // Timestamp Creation: Time of creation
    var creationTS: String = Timestamp().export()
)