package com.example.viewboard.backend.auth.impl

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.example.viewboard.backend.auth.abstraction.AuthServerAPI
import com.example.viewboard.ui.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object AuthAPI : AuthServerAPI() {
    public override fun register(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
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
                    Firebase.firestore.collection("users")
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
                val user = FirebaseAuth.getInstance().currentUser!!
                user.updateEmail(newEmail)
                    .addOnSuccessListener {
                        Firebase.firestore
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
        FirebaseAuth.getInstance()
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
        val user = FirebaseAuth.getInstance().currentUser
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
        val user = FirebaseAuth.getInstance().currentUser
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
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    public override fun getEmail(): String? {
        return FirebaseAuth.getInstance().currentUser?.email
    }

    public override fun isLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    public override fun getDisplayName(): String? {
        return FirebaseAuth.getInstance().currentUser?.displayName
    }

    public override fun updateFCMToken(token: String, onComplete: (() -> Unit)?) {
        val uid = getUid() ?: return
        Firebase.firestore
            .collection("users")
            .document(uid)
            .update("fcmToken", token)
            .addOnSuccessListener { onComplete?.invoke() }
    }

    public override fun fetchAndSaveFcmToken(onComplete: (() -> Unit)?) {
        val uid = getUid() ?: return
        FirebaseMessaging.getInstance().token
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

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show()

                    fetchAndSaveFcmToken {
                        navController.navigate(Screen.HomeScreen.route)
                    }

                } else {
                    Toast.makeText(
                        context,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    public override suspend fun getDisplayName(
        userID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ): String? {
        return Firebase.firestore
            .collection("users")
            .document(userID)
            .get()
            .addOnSuccessListener {
                println("successfully retrieved display name: $userID")
                onSuccess(userID)
            }
            .addOnFailureListener {
                println("failed to retrieved display name:: $userID")
                onFailure(userID)
            }
            .await()
            .getString("name")
    }
}