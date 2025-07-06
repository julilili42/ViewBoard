package com.example.viewboard.backend.auth.impl

import com.example.viewboard.backend.auth.abstraction.AuthServerAPI
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.firestore

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
                user.updateProfile(UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                ).addOnCompleteListener { updTask ->
                    if (!updTask.isSuccessful) {
                        onError(updTask.exception?.message ?: "Failed to set name")
                        return@addOnCompleteListener
                    }
                    Firebase.firestore.collection("users")
                        .document(user.uid)
                        .set(mapOf("name" to name, "email" to email))
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
        verifyPassword(oldPassword,
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
                                onError(e.message ?: "Firestore-Update fehlgeschlagen")
                            }
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "E-Mail-Update fehlgeschlagen")
                    }
            },
            onError
        )
    }

    public override fun setPassword(
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onError("Kein eingeloggter Nutzer")
            return
        }

        user.updatePassword(newPassword)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Passwort-Update fehlgeschlagen")
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
            onError("Kein eingeloggter Nutzer oder fehlende E-Mail")
            return
        }

        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Passwort stimmt nicht")
            }
    }

    public override fun updatePassword(
        oldPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        verifyPassword(oldPassword,
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

    public override fun getDisplayName(): String? {
        return FirebaseAuth.getInstance().currentUser?.displayName
    }

    public override fun isLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}