package com.example.viewboard.backend.data

import com.google.firebase.firestore.DocumentId

data class UserLayout (
    @DocumentId
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    // views are connected to User
    var views: ArrayList<String> = ArrayList<String>(),
)