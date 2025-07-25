package com.example.viewboard.backend.notification.abstraction

import android.content.Context

abstract class NotificationServerAPI {
    /**
     * Creates (or updates) the notification channel with the standard settings.
     *
     * @param context Application context used to register the channel.
     */
    public abstract fun createNotificationChannel(context: Context)

    /**
     * Sends a local push notification with the given title and message.
     *
     * @param context Application context used to build and dispatch the notification.
     * @param title   The notification title to display.
     * @param message The notification body text.
     */
    public abstract fun sendNotification(context: Context, title: String, message: String)

    /**
     * Marks a project as "seen" by the current user, preventing duplicate notifications.
     *
     * @param context   Application context for accessing shared preferences.
     * @param projectId The ID of the project to mark as seen.
     */
    public abstract fun saveSeenProject(context: Context, projectId: String)

    /**
     * Checks whether the given project has already been marked as seen.
     *
     * @param context   Application context for accessing shared preferences.
     * @param projectId The ID of the project to check.
     * @return True if the project was previously marked as seen; false otherwise.
     */
    public abstract fun hasSeenProject(context: Context, projectId: String): Boolean

    /**
     * Marks an issue as "seen" by the current user, preventing duplicate notifications.
     *
     * @param context Application context for accessing shared preferences.
     * @param issueId The ID of the issue to mark as seen.
     */
    public abstract fun saveSeenIssue(context: Context, issueId: String)

    /**
     * Checks whether the given issue has already been marked as seen.
     *
     * @param context Application context for accessing shared preferences.
     * @param issueId The ID of the issue to check.
     * @return True if the issue was previously marked as seen; false otherwise.
     */
    public abstract fun hasSeenIssue(context: Context, issueId: String): Boolean

    /**
     * Scans for upcoming deadlines (e.g., tomorrow or in two days)
     * and sends notifications for issues whose deadlines are approaching.
     *
     * @param context Application context for sending notifications.
     */
    public abstract suspend fun checkUpcomingDeadlines(context: Context)

    /**
     * Scans for new issue assignments for the current user
     * and sends a notification for each newly assigned issue.
     *
     * @param context Application context for sending notifications.
     */
    public abstract suspend fun checkNewIssueAssignments(context: Context)

    /**
     * Scans for new project assignments for the current user
     * and sends a notification for each newly assigned project.
     *
     * @param context Application context for sending notifications.
     */
    public abstract suspend fun checkNewProjectAssignments(context: Context)
}