package com.example.viewboard.backend.dataLayout

import com.google.firebase.auth.FirebaseAuth

object UserHelper {
    fun getUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun getEmail(): String? {
        return FirebaseAuth.getInstance().currentUser?.email
    }

    fun getDisplayName(): String? {
        return FirebaseAuth.getInstance().currentUser?.displayName
    }

    fun isLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}
