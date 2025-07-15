package com.example.viewboard.backend.auth.impl
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object DisplayNameCache {
    private val cache = mutableMapOf<String, String>()

    suspend fun get(userID: String): String? {
        cache[userID]?.let { return it }

        val name = try {
            Firebase.firestore
                .collection("users")
                .document(userID)
                .get()
                .await()
                .getString("name")
        } catch (e: Exception) {
            Log.e("DisplayNameCache", "Error loading name for $userID", e)
            null
        }

        name?.let { put(userID, it) }
        return name
    }

    fun put(userID: String, name: String) {
        cache[userID] = name
    }
}

