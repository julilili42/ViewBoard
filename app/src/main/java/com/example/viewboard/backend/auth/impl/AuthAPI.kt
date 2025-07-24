package com.example.viewboard.backend.auth.impl

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.example.viewboard.backend.auth.abstraction.AuthServerAPI
import com.example.viewboard.backend.data.UserLayout
import com.example.viewboard.ui.navigation.Screen
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldPath
import kotlinx.coroutines.tasks.await


object AuthAPI : AuthServerAPI() {
    public override fun register(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        FirebaseProvider.auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    onError(task.exception?.message ?: "Unknown error")
                    return@addOnCompleteListener
                }
                val user = task.result.user!!
                // set display name
                user.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                ).addOnCompleteListener { updTask ->
                    if (!updTask.isSuccessful) {
                        onError(updTask.exception?.message ?: "Failed to set name")
                        return@addOnCompleteListener
                    }
                    FirebaseProvider
                        .firestore
                        .collection("users")
                        .document(user.uid)
                        .set(mapOf("name" to name, "email" to email,"notificationsEnabled" to true))
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onError(e.message ?: "Firestore error") }
                }
            }
    }

    public override fun updateEmail(
        oldPassword: String,
        newEmail: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        verifyPassword(
            oldPassword,
            {
                val user = FirebaseProvider.auth.currentUser!!
                user.updateEmail(newEmail)
                    .addOnSuccessListener {
                        FirebaseProvider.firestore
                            .collection("users")
                            .document(user.uid)
                            .update("email", newEmail)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e ->
                                onError(e.message ?: "Firestore-Update failed")
                            }
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "E-Mail-Update failed")
                    }
            },
            onError
        )
    }

    public override fun sendPasswordResetMail(
        email: String,
        onComplete: (message: String) -> Unit
    ) {
        FirebaseProvider.auth
            .sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful)
                    "Reset email sent to $email"
                else
                    "Error sending reset email: ${task.exception?.message}"
                onComplete(msg)
            }
    }


    public override fun setPassword(
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseProvider.auth.currentUser
        if (user == null) {
            onError("No logged in user")
            return
        }

        user.updatePassword(newPassword)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Password-Update failed")
            }
    }

    public override fun verifyPassword(
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseProvider.auth.currentUser
        val email = user?.email
        if (user == null || email.isNullOrBlank()) {
            onError("No logged in user or missing email")
            return
        }

        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Password is incorrect")
            }
    }

    public override fun updatePassword(
        oldPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        verifyPassword(
            oldPassword,
            { setPassword(newPassword, onSuccess, onError) },
            onError
        )
    }

    public override fun getUid(): String? {
        return FirebaseProvider.auth.currentUser?.uid
    }

    public override fun getEmail(): String? {
        return FirebaseProvider.auth.currentUser?.email
    }

    public override fun isLoggedIn(): Boolean {
        return FirebaseProvider.auth.currentUser != null
    }

    public override suspend fun getListOfAllUsers(): Result<List<UserLayout>> = runCatching {
        val ref = FirebaseProvider.firestore
            .collection("users")
            .get()
            .await()

        ref.documents.mapNotNull { user ->
            user.toObject(UserLayout::class.java)
                ?.copy(id = user.id)
        }
    }

    public override fun updateFCMToken(token: String, onComplete: (() -> Unit)?) {
        val uid = getUid() ?: return
        FirebaseProvider.firestore
            .collection("users")
            .document(uid)
            .update("fcmToken", token)
            .addOnSuccessListener { onComplete?.invoke() }
    }

    public override fun fetchAndSaveFcmToken(onComplete: (() -> Unit)?) {
        val uid = getUid() ?: return
        FirebaseProvider.messaging().token
            .addOnSuccessListener { token ->
                updateFCMToken(token) {
                    onComplete?.invoke()
                }
            }
    }


    public override fun loginWithEmail(
        context: Context,
        email: String,
        password: String,
        navController: NavController
    ) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseProvider.auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate(Screen.HomeScreen.route)
                    Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show()
                    fetchAndSaveFcmToken()
                } else {
                    Toast.makeText(
                        context,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    public override fun getCurrentDisplayName(): String? {
        return FirebaseProvider.auth.currentUser?.displayName
    }


    public override suspend fun getDisplayName(
        userID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ): String? {
        // use cache s.t. unnecessary queries can be avoided
        val name = UserDisplayNameCache.get(userID)
        return if (name != null) {
            onSuccess(name)
            name
        } else {
            onFailure("Display name not found")
            null
        }
    }
    override fun ensureUserProfileExists(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = getUid()
        val currentUser = FirebaseProvider.auth.currentUser

        if (uid.isNullOrBlank() || currentUser == null) {
            onError("No logged-in user")
            return
        }

        val userRef = FirebaseProvider.firestore
            .collection("users")
            .document(uid)

        userRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // profile exists
                    onSuccess()
                } else {
                    // create Profile
                    val profile = UserLayout(
                        id = uid,
                        name = currentUser.displayName ?: "",
                        email = currentUser.email ?: "",
                    )

                    userRef.set(profile)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e ->
                            onError(e.message ?: "Error while creating profile")
                        }
                }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error while retrieving profile")
            }
    }

    override suspend fun getEmailsByIds(userIds: List<String>): Result<List<String?>> = runCatching {
        // split userIds in size 10 chunks, s.t. less queries are needed
        userIds.chunked(10).flatMap { chunk ->

            val snapshot = FirebaseProvider.firestore
                .collection("users")
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()

            // extract list of emails from snapshot
            snapshot.documents.map { it.getString("email") }
        }
    }

    override suspend fun getUserById(userID: String): Result<UserLayout> = runCatching {
        val doc = FirebaseProvider.firestore
            .collection("users")
            .document(userID)
            .get()
            .await()

        // check if document exists
        if (!doc.exists()) {
            throw NoSuchElementException("User with ID $userID not found")
        }

        // convert to UserLayout and set uid
        doc.toObject(UserLayout::class.java)
            ?.copy(id = doc.id)
            ?: throw IllegalStateException("Failed to parse UserLayout for ID $userID")
    }

    override fun logout(navController: NavController) {
        FirebaseProvider.auth.signOut()

        navController.navigate(Screen.LoginScreen.route) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }


}

