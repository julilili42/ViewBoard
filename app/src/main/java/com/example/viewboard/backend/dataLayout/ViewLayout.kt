package com.example.viewboard.backend.dataLayout

import com.example.viewboard.backend.Timestamp
import com.google.firebase.firestore.DocumentId

data class ViewLayout (
    @DocumentId
    var id: String = "",
    var name: String = "",
    var creator: String = "",
    var issues: ArrayList<String> = ArrayList<String>(),
    var creationTS: Timestamp = Timestamp()
)