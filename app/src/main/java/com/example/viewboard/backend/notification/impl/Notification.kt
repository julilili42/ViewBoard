package com.example.viewboard.backend.notification.impl

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.example.viewboard.R
import com.example.viewboard.backend.auth.impl.FirebaseProvider
import com.example.viewboard.backend.notification.abstraction.NotificationServerAPI
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object Notification : NotificationServerAPI() {
    private const val CHANNEL_ID = "default"

    override fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun saveSeenProject(context: Context, projectId: String) {
        val prefs = context.getSharedPreferences("seen_projects", Context.MODE_PRIVATE)
        prefs.edit() { putBoolean(projectId, true) }
    }

    override fun hasSeenProject(context: Context, projectId: String): Boolean {
        val prefs = context.getSharedPreferences("seen_projects", Context.MODE_PRIVATE)
        return prefs.getBoolean(projectId, false)
    }

    override fun sendNotification(context: Context, title: String, message: String) {
        // for Android 13+ check permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(Random.Default.nextInt(), builder.build())
        }

    }

    override suspend fun checkUpcomingDeadlines(context: Context) {
        val uid = FirebaseProvider.auth.currentUser?.uid ?: return

        val userDoc = Firebase.firestore.collection("users").document(uid).get().await()
        val notificationsEnabled = userDoc.getBoolean("notificationsEnabled") ?: false
        if (!notificationsEnabled) return

        val now = LocalDate.now()
        val tomorrow = LocalDate.now().plusDays(1)
        Log.d("NOTIFY", "Now: ${LocalDate.now()} → Tomorrow: $tomorrow")
        val inTwoDays = now.plusDays(2)

        val issuesSnap = Firebase.firestore.collection("Issues").get().await()

        for (issueDoc in issuesSnap.documents) {
            val issue = issueDoc.data ?: continue
            val creator = issue["creator"] as? String
            val assignments =
                (issue["assignments"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()


            if (creator != uid && uid !in assignments) continue

            val deadlineStr = issue["deadlineTS"] as? String ?: continue
            val deadline = Instant.parse(deadlineStr).atZone(ZoneId.systemDefault()).toLocalDate()
            val deadlineFormatted = deadline.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            Log.d("NOTIFY", "Parsed deadlineTS: $deadlineStr → $deadline (local)")

            if (deadline == inTwoDays || deadline == tomorrow) {
                Log.d(
                    "NOTIFY",
                    "sendNotification called: Deadline incoming! - The Issue '${issue["title"]}' deadline is due $deadlineFormatted."
                )

                sendNotification(
                    context,
                    title = "Deadline incoming!",
                    message = "The Issue '${issue["title"]}' deadline is due $deadlineFormatted."
                )
            }

        }
    }

    override fun saveSeenIssue(context: Context, issueId: String) {
        val prefs = context.getSharedPreferences("seen_issues", Context.MODE_PRIVATE)
        prefs.edit { putBoolean(issueId, true) }
    }

    override fun hasSeenIssue(context: Context, issueId: String): Boolean {
        val prefs = context.getSharedPreferences("seen_issues", Context.MODE_PRIVATE)
        return prefs.getBoolean(issueId, false)
    }


    override suspend fun checkNewIssueAssignments(context: Context) {
        val uid = FirebaseProvider.auth.currentUser?.uid ?: return

        val userDoc = Firebase.firestore.collection("users").document(uid).get().await()
        val notificationsEnabled = userDoc.getBoolean("notificationsEnabled") ?: false
        if (!notificationsEnabled) return

        val issuesSnap = Firebase.firestore.collection("Issues").get().await()
        for (doc in issuesSnap.documents) {
            val issueId = doc.id
            val data = doc.data ?: continue
            val assignments =
                (data["assignments"] as? List<*>)?.mapNotNull { it as? String } ?: continue

            if (uid in assignments && !hasSeenIssue(context, issueId)) {
                sendNotification(
                    context,
                    title = "New Issue!",
                    message = "You have been assigned to the issue '${data["title"]}'."
                )
                saveSeenIssue(context, issueId)
            }
        }
    }

    override suspend fun checkNewProjectAssignments(context: Context) {
        val uid = FirebaseProvider.auth.currentUser?.uid ?: return

        val projectsSnap = Firebase.firestore.collection("Projects").get().await()
        for (doc in projectsSnap.documents) {
            val projectId = doc.id
            val data = doc.data ?: continue
            val members = (data["users"] as? List<*>)?.mapNotNull { it as? String } ?: continue

            Log.d("NOTIFY", "Checking project: $projectId - members: $members - uid: $uid")

            if (uid in members && !hasSeenProject(context, projectId)) {
                Log.d("NOTIFY", "Triggering notification for project $projectId")
                sendNotification(
                    context,
                    title = "New Project!",
                    message = "You have been assigned to a new Project '${data["name"]}'."
                )
                saveSeenProject(context, projectId)
            }
        }
    }
}