package com.example.viewboard.components.homeScreen
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.IssueState
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
class IssueProgressAggregator(
    private val projId: String,
    private val userId: String
) {
    private val db = Firebase.firestore.collection("Issues")
        .whereEqualTo("projectId", projId)

    // Öffentlich beobachtbare StateFlows
    val totalCount     = MutableStateFlow(0)
    val completedCount = MutableStateFlow(0)

    private val listener = db.addSnapshotListener { snaps, err ->
        if (err != null || snaps == null) return@addSnapshotListener

        for (change in snaps.documentChanges) {
            val issue = change.document.toObject(IssueLayout::class.java)

            when (change.type) {
                DocumentChange.Type.ADDED -> {
                    totalCount.value += 1
                    if (issue.assignments.contains(userId) && issue.state == IssueState.DONE)
                        completedCount.value += 1
                }
                DocumentChange.Type.MODIFIED -> {
                    // altes Objekt ließe sich optional aus change.oldIndex holen,
                    // hier nehmen wir einfach immer neu:
                    val old = change.oldIndex.takeIf { it >= 0 }?.let {
                        snaps.documents[it].toObject(IssueLayout::class.java)
                    }
                    if (old != null) {
                        // Statuswechsel ins DONE
                        if (old.state != IssueState.DONE && issue.state == IssueState.DONE)
                            completedCount.value += 1
                        // Statuswechsel raus aus DONE
                        else if (old.state == IssueState.DONE && issue.state != IssueState.DONE)
                            completedCount.value -= 1
                    }
                }
                DocumentChange.Type.REMOVED -> {
                    totalCount.value -= 1
                    if (issue.assignments.contains(userId) && issue.state == IssueState.DONE)
                        completedCount.value -= 1
                }
            }
        }
    }

    fun stop() {
        listener.remove()
    }
}
