package com.example.viewboard.dataLayout

import com.google.firebase.firestore.DocumentId
import com.viewBoard.commonModule.Timestamp

data class LabelLayout (
    @DocumentId
    var id: String,
    var name: String,
    var creator: String,
    var refCounter: UInt = 0u,
    var creationTS: Timestamp = Timestamp(),
)