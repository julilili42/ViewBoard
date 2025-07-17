package com.example.viewboard.backend.dataLayout

import com.google.firebase.firestore.DocumentId

data class UserLayout (
    @DocumentId
    var uid: String = "",
    var name: String = "",
    var email: String = "",
)