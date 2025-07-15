package com.example.viewboard.backend.auth.abstraction

import android.content.Context
import androidx.navigation.NavController

abstract class AuthServerAPI () {
    public abstract fun sendPasswordResetMail(email: String, onComplete: (message: String) -> Unit)
    public abstract fun updateEmail(oldPassword: String, newEmail: String, onSuccess: () -> Unit, onError: (String) -> Unit)
    public abstract fun setPassword(newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit)
    public abstract fun verifyPassword(password: String, onSuccess: () -> Unit, onError: (String) -> Unit)
    public abstract fun updatePassword(oldPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit)
    public abstract fun getUid(): String?
    public abstract fun getEmail(): String?
    public abstract fun isLoggedIn(): Boolean
    public abstract fun loginWithEmail(context: Context, email: String, password: String, navController: NavController)
    public abstract fun logout(navController: NavController)
    public abstract fun register(name: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit)
    public abstract suspend fun getDisplayName(userID: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}): String?
    public abstract fun getCurrentDisplayName(): String?
    public abstract fun updateFCMToken(token: String, onComplete: (() -> Unit)? = null)
    public abstract fun fetchAndSaveFcmToken(onComplete: (() -> Unit)? = null)
}