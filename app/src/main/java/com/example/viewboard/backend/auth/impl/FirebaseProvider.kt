package com.example.viewboard.backend.auth.impl

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Singleton object providing configured Firebase services for authentication,
 * Firestore database access, and cloud messaging.
 */
object FirebaseProvider {
    // Firebase Authentication instance
    val auth by lazy { FirebaseAuth.getInstance() }

    // Firestore database instance
    val firestore by lazy { Firebase.firestore }

    // Firebase Cloud Messaging (FCM) instance
    fun messaging() = FirebaseMessaging.getInstance()
}

