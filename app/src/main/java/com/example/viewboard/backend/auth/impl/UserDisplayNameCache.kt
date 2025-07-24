package com.example.viewboard.backend.auth.impl
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object UserDisplayNameCache {
    private val cache = mutableMapOf<String, String>()

    /**
     * Retrieves the display name for the given userID, using an in-memory cache to avoid repeated network calls.
     *
     * @param userID The UID of the user whose name should be fetched.
     * @return The user's display name if found, or null on error or if the field is missing.
     */
    suspend fun get(userID: String): String? {
        // check cache first
        cache[userID]?.let { return it }

        // cache was empty -> Query needed
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

