package com.example.viewboard.backend.auth.impl

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.example.viewboard.backend.auth.abstraction.AuthServerAPI
import com.example.viewboard.ui.navigation.Screen
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest


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
        val name = UserDisplayNameCache.get(userID)
        return if (name != null) {
            onSuccess(name)
            name
        } else {
            onFailure("Display name not found")
            null
        }
    }


    override fun logout(navController: NavController) {
        FirebaseProvider.auth.signOut()

        navController.navigate(Screen.LoginScreen.route) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }
}

