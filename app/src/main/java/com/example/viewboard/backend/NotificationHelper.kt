package com.example.viewboard.backend

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.viewboard.R
import com.example.viewboard.backend.auth.impl.FirebaseProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import kotlin.random.Random
import java.time.Instant
import java.time.format.DateTimeFormatter


object NotificationHelper {

    private const val CHANNEL_ID = "default"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Benachrichtigungen",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun sendNotification(context: Context, title: String, message: String) {
        // Android 13+ → Permission prüfen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
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
            notify(Random.nextInt(), builder.build())
        }

    }

    suspend fun checkUpcomingDeadlines(context: Context) {
        val uid = FirebaseProvider.auth.currentUser?.uid ?: return

        val userDoc = Firebase.firestore.collection("users").document(uid).get().await()
        val notificationsEnabled = userDoc.getBoolean("notificationsEnabled") ?: false
        if (!notificationsEnabled) return

        val now = LocalDate.now()
        val tomorrow = now.plusDays(1)
        val inTwoDays = now.plusDays(2)

        val issuesSnap = Firebase.firestore.collection("Issues").get().await()

        for (issueDoc in issuesSnap.documents) {
            val issue = issueDoc.data ?: continue
            val creator = issue["creator"] as? String
            val assignments = (issue["assignments"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()


            if (creator != uid && uid !in assignments) continue

            val deadlineStr = issue["deadlineTS"] as? String ?: continue
            val deadline = Instant.parse(deadlineStr).atZone(ZoneId.systemDefault()).toLocalDate()
            val deadlineFormatted = deadline.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

            if (deadline == inTwoDays || deadline == tomorrow ) {
                sendNotification(
                    context,
                    title = "Deadline incoming!",
                    message = "The Issue '${issue["title"]}' deadline is due $deadlineFormatted."
                )
            }
        }
    }

    suspend fun checkNewIssueAssignments(context: Context) {
        val notifiedIssueIds = mutableSetOf<String>()

        val uid = FirebaseProvider.auth.currentUser?.uid ?: return

        val userDoc = Firebase.firestore.collection("users").document(uid).get().await()
        val notificationsEnabled = userDoc.getBoolean("notificationsEnabled") ?: false
        if (!notificationsEnabled) return

        val issuesSnap = Firebase.firestore.collection("Issues").get().await()
        for (doc in issuesSnap.documents) {
            val issueId = doc.id
            val data = doc.data ?: continue
            val assignments = (data["assignments"] as? List<*>)?.mapNotNull { it as? String } ?: continue

            if (uid in assignments && issueId !in notifiedIssueIds) {
                sendNotification(
                    context,
                    title = "Neue Aufgabe!",
                    message = "Du wurdest dem Issue '${data["title"]}' zugeteilt."
                )
                notifiedIssueIds.add(issueId)
            }
        }
    }


    suspend fun checkNewProjectAssignments(context: Context) {
        val uid = FirebaseProvider.auth.currentUser?.uid ?: return
        val notifiedProjectIds = mutableSetOf<String>()

        val projectsSnap = Firebase.firestore.collection("Projects").get().await()
        for (doc in projectsSnap.documents) {
            val projectId = doc.id
            val data = doc.data ?: continue
            val members = (data["members"] as? List<*>)?.mapNotNull { it as? String } ?: continue

            if (uid in members && projectId !in notifiedProjectIds) {
                sendNotification(
                    context,
                    title = "Neues Projekt!",
                    message = "Du wurdest dem Projekt '${data["title"]}' zugewiesen."
                )
                notifiedProjectIds.add(projectId)
            }
        }
    }


}
