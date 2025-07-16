package com.example.viewboard.backend.dataLayout

import com.google.firebase.firestore.DocumentId
import com.example.viewboard.backend.Timestamp

data class LabelLayout (
    @DocumentId
    var id: String = "",
    var name: String = "",
    var creator: String = "",
    var refCounter: Int = 0, // TODO not used
    var creationTS: String = Timestamp().export()
)