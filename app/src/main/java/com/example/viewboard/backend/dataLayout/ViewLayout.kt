package com.example.viewboard.backend.dataLayout

import com.google.firebase.firestore.DocumentId
import com.example.viewboard.backend.Timestamp

data class ViewLayout (
    @DocumentId
    var id: String = "",
    var name: String = "",
    var creator: String = "",
    var issues: ArrayList<String> = ArrayList<String>(),
    var creationTS: String = Timestamp().export()
)