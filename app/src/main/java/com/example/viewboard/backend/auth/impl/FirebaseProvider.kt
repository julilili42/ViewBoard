package com.example.viewboard.backend.auth.impl

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging

object FirebaseProvider {
    val auth by lazy { FirebaseAuth.getInstance() }
    val firestore by lazy { Firebase.firestore }
    fun messaging() = FirebaseMessaging.getInstance()
}

