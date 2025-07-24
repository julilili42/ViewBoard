package com.example.viewboard.backend.dataLayout

import com.google.firebase.firestore.DocumentId

/**
 * @property id the id of the user
 * @property name the name of the user
 * @property email the email of the user
 * @property views the views associated with the user
 */
data class UserLayout (
    @DocumentId
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var views: ArrayList<String> = ArrayList<String>(),
)