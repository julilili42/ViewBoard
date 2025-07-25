package com.example.viewboard.backend.dataLayout

import com.google.firebase.firestore.DocumentId
import com.example.viewboard.backend.time.Timestamp

/**
 * @property id the id of the view
 * @property name the name of the view
 * @property creator the user who creates the view
 * @property issues the issues associated with the view
 * @property creationTS the creation timestamp of the view
 */
data class ViewLayout(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var creator: String = "",
    var issues: ArrayList<String> = ArrayList<String>(),
    var creationTS: String = Timestamp().export()
)