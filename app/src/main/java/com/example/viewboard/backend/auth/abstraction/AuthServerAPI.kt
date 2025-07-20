package com.example.viewboard.backend.auth.abstraction

import android.content.Context
import androidx.navigation.NavController
import com.example.viewboard.backend.dataLayout.UserLayout

abstract class AuthServerAPI {

    /**
     * Send a password reset email to the given address.
     *
     * @param email the user's email address
     * @param onComplete callback invoked with a status message when the operation finishes
     */
    public abstract fun sendPasswordResetMail(
        email: String,
        onComplete: (message: String) -> Unit
    )

    /**
     * Change the user's email address after verifying their old password.
     *
     * @param oldPassword the current password
     * @param newEmail the new email address to set
     * @param onSuccess called when the email was updated successfully
     * @param onError called with an error message if the update fails
     */
    public abstract fun updateEmail(
        oldPassword: String,
        newEmail: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Set a new password for the current account.
     *
     * @param newPassword the password to set
     * @param onSuccess called when the password is set successfully
     * @param onError called with an error message if setting fails
     */
    public abstract fun setPassword(
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Verify the user's password without changing it.
     *
     * @param password the password to verify
     * @param onSuccess called if verification succeeds
     * @param onError called with an error message if verification fails
     */
    public abstract fun verifyPassword(
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Update the current password after verifying the old one.
     *
     * @param oldPassword the current password
     * @param newPassword the new password to set
     * @param onSuccess called when the update succeeds
     * @param onError called with an error message if the update fails
     */
    public abstract fun updatePassword(
        oldPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Retrieve the unique identifier of the current user.
     *
     * @return the user's UID, or null if not logged in
     */
    public abstract fun getUid(): String?

    /**
     * Retrieve the email address of the current user.
     *
     * @return the user's email, or null if not available
     */
    public abstract fun getEmail(): String?

    /**
     * Check if a user is currently authenticated.
     *
     * @return true if logged in, false otherwise
     */
    public abstract fun isLoggedIn(): Boolean

    /**
     * Perform email/password login and navigate on success.
     *
     * @param context the Android context
     * @param email the user's email
     * @param password the user's password
     * @param navController controller to handle post-login navigation
     */
    public abstract fun loginWithEmail(
        context: Context,
        email: String,
        password: String,
        navController: NavController
    )

    /**
     * Log out the current user and navigate accordingly.
     *
     * @param navController controller to handle post-logout navigation
     */
    public abstract fun logout(navController: NavController)

    /**
     * Register a new user account.
     *
     * @param name the display name for the new user
     * @param email the user's email address
     * @param password the user's chosen password
     * @param onSuccess called when registration succeeds
     * @param onError called with an error message if registration fails
     */
    public abstract fun register(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Fetch another user's display name.
     *
     * @param userID the ID of the user to query
     * @param onSuccess called with the display name if retrieval succeeds
     * @param onFailure called with an error message if retrieval fails
     * @return the fetched display name, or null on failure
     */
    public abstract suspend fun getDisplayName(
        userID: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    ): String?

    /**
     * Get the display name of the currently authenticated user.
     *
     * @return the current user's display name, or null if unavailable
     */
    public abstract fun getCurrentDisplayName(): String?

    /**
     * Update the Firebase Cloud Messaging token for push notifications.
     *
     * @param token the new FCM token
     * @param onComplete optional callback when the update is done
     */
    public abstract fun updateFCMToken(
        token: String,
        onComplete: (() -> Unit)? = {}
    )
    /**
     * Legt das User‑Profil in Firestore an, falls es noch nicht existiert.
     *
     * @param onSuccess Wird aufgerufen, wenn das Profil bereits existierte oder erfolgreich angelegt wurde.
     * @param onError   Wird aufgerufen mit einer Fehlermeldung, wenn etwas schiefgeht.
     */
    abstract fun ensureUserProfileExists(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    /**
     * Retrieve and persist the FCM token for the current device.
     *
     * @param onComplete optional callback when the token is fetched and saved
     */
    public abstract fun fetchAndSaveFcmToken(onComplete: (() -> Unit)? = {})

    public abstract suspend fun getListOfAllUsers(): Result<List<UserLayout>>
}
