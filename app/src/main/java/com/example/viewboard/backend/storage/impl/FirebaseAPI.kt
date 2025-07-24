package com.example.viewboard.backend.storage.impl

import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.auth.impl.FirebaseProvider
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.UserLayout
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.backend.storage.abstraction.StorageServerAPI
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.tasks.await

object FirebaseAPI : StorageServerAPI() {
    public override fun init() {
        val db = Firebase.firestore

        projectTable = db.collection("Projects")
        projects = projectTable.snapshots().map { it.toObjects<ProjectLayout>() }

        issueTable = db.collection("Issues")
        issues = issueTable.snapshots().map { it.toObjects<IssueLayout>() }

        viewTable = db.collection("Views")
        views = viewTable.snapshots().map { it.toObjects<ViewLayout>() }

        userTable = db.collection("users")
        users = userTable.snapshots().map { it.toObjects<UserLayout>() }
    }

    public override fun addProject(projectLayout: ProjectLayout, onSuccess: (String) -> Unit, onFailure: (ProjectLayout) -> Unit) {
        val uid = AuthAPI.getUid() ?: return

        val updatedUsers = ArrayList(projectLayout.users).apply {
            if (!contains(uid)) {
                add(uid)
            }
        }
        val projectWithUser = projectLayout.copy(
            creator = uid,
            users = updatedUsers
        )
        projectTable.add(projectWithUser)

            .addOnSuccessListener { ref ->
                println("successfully added project: " + ref.id)
                onSuccess(ref.id)
            }
            .addOnFailureListener {
                println("failed to add project")
                onFailure(projectLayout)
            }
    }

    public override fun rmProject(id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        projectTable.document(id)
            .delete()
            .addOnSuccessListener {
                println("successfully removed project: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to remove project: $id")
                onFailure(id)
            }
    }

    public override fun updProject(projectLayout: ProjectLayout, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        projectTable.document(projectLayout.id)
            .set(projectLayout)
            .addOnSuccessListener {
                println("successfully updated project: " + projectLayout.id)
                onSuccess(projectLayout.id)
            }
            .addOnFailureListener {
                println("failed to update project: " + projectLayout.id)
                onFailure(projectLayout.id)
            }
    }

    public override fun updProject(id: String, projectLayout: ProjectLayout, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        projectTable.document(id)
            .set(projectLayout)
            .addOnSuccessListener {
                println("successfully updated project: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to update project: $id")
                onFailure(id)
            }
    }

    public override suspend fun getProject(id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) : ProjectLayout? {
        val snap = projectTable.document(id)
            .get()
            .addOnSuccessListener {
                println("successfully retrieved project: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to retrieve project: $id")
                onFailure(id)
            }
            .await()

        return snap.toObject(ProjectLayout::class.java)
    }

    public override fun getAllProjects() : Flow<List<ProjectLayout>> {
        return projects
    }

    public override fun getProjectsFromUser(userID: String?) : Flow<List<ProjectLayout>> {
        return if (userID != null) {
            projects.map { projects ->
                projects.filter { it.users.contains(userID) }
            }
        } else {
            flowOf(emptyList())
        }
    }

    public override suspend fun addIssue(projID: String, issueLayout: IssueLayout, onSuccess: (String) -> Unit, onFailure: (IssueLayout) -> Unit) {
        val batch = Firebase.firestore.batch()
        val projREF = projectTable.document(projID)
        val issueREF = issueTable.document()

        batch.set(issueREF, issueLayout)

        batch.update(projREF, "issues", FieldValue.arrayUnion(issueREF.id))

        batch.commit()
            .addOnSuccessListener {
                println("successfully added issue: " + issueREF.id)
                onSuccess(issueREF.id)
            }
            .addOnFailureListener {
                println("failed to add issue")
                onFailure(issueLayout)
            }
    }

    public override suspend fun addIssue(projID: String, viewID: String, issueLayout: IssueLayout, onSuccess: (String) -> Unit, onFailure: (IssueLayout) -> Unit) {
        val projREF = projectTable.document(projID)
        val viewREF = viewTable.document(viewID)
        val issueREF = issueTable.document()

        Firebase.firestore.runTransaction { transaction ->
            val projSnap = transaction.get(projREF)
            val views = projSnap.get("views") as? List<String> ?: emptyList()

            if (!views.contains(viewID)) {
                throw FirebaseFirestoreException("project does not include the view", FirebaseFirestoreException.Code.ABORTED)
            }

            transaction.set(issueREF, issueLayout)

            transaction.update(projREF, "issues", FieldValue.arrayUnion(issueREF.id))
            transaction.update(viewREF, "issues", FieldValue.arrayUnion(issueREF.id))
        }
            .addOnSuccessListener {
                println("successfully added issue: " + issueREF.id)
                onSuccess(issueREF.id)
            }
            .addOnFailureListener {
                println("failed to add issue")
                onFailure(issueLayout)
            }
    }

    public override suspend fun rmIssue(projID: String, id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val projREF = projectTable.document(projID)
        val issueREF = issueTable.document(id)

        Firebase.firestore.runTransaction { transaction ->
            val projSnap = transaction.get(projREF)
            val views = projSnap.get("views") as? List<String> ?: emptyList()

            transaction.delete(issueREF)

            transaction.update(projREF, "issues", FieldValue.arrayRemove(id))

            for (view in views) {
                val viewREF = viewTable.document(view)
                transaction.update(viewREF, "issues", FieldValue.arrayRemove(id))
            }

        }
            .addOnSuccessListener {
                println("successfully removed issue: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to remove issue: $id")
                onFailure(id)
            }
    }

    public override suspend fun addIssueToView(viewID: String, id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val batch = Firebase.firestore.batch()

        viewTable.document(viewID)
            .update("issues", FieldValue.arrayUnion(id))
            .addOnSuccessListener {
                println("successfully added issue to view: $viewID")
                onSuccess(viewID)
            }
            .addOnFailureListener {
                println("failed to add issue to view: $viewID")
                onFailure(viewID)
            }
    }

    public override suspend fun rmIssueFromView(viewID: String, id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val batch = Firebase.firestore.batch()

        viewTable.document(viewID)
            .update("issues", FieldValue.arrayRemove(id))
            .addOnSuccessListener {
                println("successfully removed issue from view: $viewID")
                onSuccess(viewID)
            }
            .addOnFailureListener {
                println("failed to remove issue from view: $viewID")
                onFailure(viewID)
            }
    }

    public override fun updIssue(issueLayout: IssueLayout, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        issueTable.document(issueLayout.id)
            .set(issueLayout)
            .addOnSuccessListener {
                println("successfully updated issue: " + issueLayout.id)
                onSuccess(issueLayout.id)
            }
            .addOnFailureListener {
                println("failed to update issue: " + issueLayout.id)
                onFailure(issueLayout.id)
            }
    }

    public override fun updIssue(id: String, issueLayout: IssueLayout, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        issueTable.document(id)
            .set(issueLayout)
            .addOnSuccessListener {
                println("successfully updated issue: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to update issue: $id")
                onFailure(id)
            }
    }

    public override suspend fun getIssue(id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) : IssueLayout? {
        val snap = issueTable.document(id)
            .get()
            .addOnSuccessListener {
                println("successfully retrieved issue: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to retrieve issue: $id")
                onFailure(id)
            }
            .await()

        return snap.toObject(IssueLayout::class.java)
    }

    public override fun getAllIssues() : Flow<List<IssueLayout>> {
        return issues
    }

    public override fun getIssuesFromView(viewID: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) : Flow<List<IssueLayout>> {
        return viewTable.document(viewID)
            .snapshots()
            .map { snap ->
                snap.toObject(ViewLayout::class.java)?.issues ?: emptyList()
            }
            .distinctUntilChanged()
            .flatMapLatest { issues ->
                if (issues.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val docFlows = issues.map { issue ->
                    issueTable.document(issue)
                        .snapshots()
                        .map { snap ->
                            snap.toObject(IssueLayout::class.java)
                        }
                }

                combine (docFlows) { docs ->
                    val map = docs.filterNotNull().associateBy { it.id }
                    issues.mapNotNull { map[it] }
                }
            }
            .onCompletion {
                println("successfully retrieved issues from view: $viewID")
                onSuccess(viewID)
            }
            .catch {
                println("failed to retrieve issues from view: $viewID")
                onFailure(viewID)
            }
    }

    public override fun getIssuesFromProject(projID: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) : Flow<List<IssueLayout>> {
        return projectTable.document(projID)
            .snapshots()
            .map { snap ->
                snap.toObject(ProjectLayout::class.java)?.issues ?: emptyList()
            }
            .distinctUntilChanged()
            .flatMapLatest { issues ->
                if (issues.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val docFlows = issues.map { issue ->
                    issueTable.document(issue)
                        .snapshots()
                        .map { snap ->
                            snap.toObject(IssueLayout::class.java)
                        }
                }

                combine (docFlows) { docs ->
                    val map = docs.filterNotNull().associateBy { it.id }
                    issues.mapNotNull { map[it] }
                }
            }
            .onCompletion {
                println("successfully retrieved issues from project: $projID")
                onSuccess(projID)
            }
            .catch {
                println("failed to retrieve issues from project: $projID")
                onFailure(projID)
            }
    }

    public override fun getIssuesFromAssignment(userID: String?) : Flow<List<IssueLayout>> {
        return issues.map { issues ->
            if (userID != null) {
                issues.filter { it.users.contains(userID) }
            }
            else {
                emptyList()
            }
        }
    }

    public override fun getIssuesFromAssignment(userID: String?, projID: String) : Flow<List<IssueLayout>> {
        return getIssuesFromProject(projID).map { issues ->
            if (userID != null) {
                issues.filter { it.users.contains(userID) }
            }
            else {
                emptyList()
            }
        }
    }

    public override fun getIssuesFromCreator(userID: String?) : Flow<List<IssueLayout>> {
        return issues.map { issues ->
            if (userID != null) {
                issues.filter { it.creator == userID }
            }
            else {
                emptyList()
            }
        }
    }

    public override fun getIssuesFromCreator(userID: String?, projID: String) : Flow<List<IssueLayout>> {
        return getIssuesFromProject(projID).map { issues ->
            if (userID != null) {
                issues.filter { it.creator == userID }
            }
            else {
                emptyList()
            }
        }
    }

    public override fun getIssuesFromUser(userID: String?) : Flow<List<IssueLayout>> {
        return issues.map { issues ->
            if (userID != null) {
                issues.filter { it.users.contains(userID) || it.creator == userID }
            }
            else {
                emptyList()
            }
        }
    }

    public override fun getIssuesFromUser(userID: String?, projID: String) : Flow<List<IssueLayout>> {
        return getIssuesFromProject(projID).map { issues ->
            if (userID != null) {
                issues.filter { it.users.contains(userID) || it.creator == userID }
            }
            else {
                emptyList()
            }
        }
    }

    public override suspend fun addView(userID: String?, viewLayout: ViewLayout, onSuccess: (String) -> Unit, onFailure: (ViewLayout) -> Unit) {
        if (userID == null) {
            println("failed to remove view, invalid user id: $userID")
            onFailure(viewLayout)

            return
        }

        val batch = Firebase.firestore.batch()
        val userREF = userTable.document(userID)
        val viewREF = viewTable.document()

        batch.set(viewREF, viewLayout)

        batch.update(userREF, "views", FieldValue.arrayUnion(viewREF.id))

        batch.commit()
            .addOnSuccessListener {
                println("successfully added view: " + viewREF.id)
                onSuccess(viewREF.id)
            }
            .addOnFailureListener {
                println("failed to add view")
                onFailure(viewLayout)
            }
    }

    public override suspend fun rmView(userID: String?, id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (userID == null) {
            println("failed to remove view, invalid user id: $userID")
            onFailure(id)

            return
        }

        val batch = Firebase.firestore.batch()
        val userREF = userTable.document(userID)
        val viewREF = viewTable.document(id)

        batch.delete(viewREF)

        batch.update(userREF, "views", FieldValue.arrayRemove(id))

        batch.commit()
            .addOnSuccessListener {
                println("successfully removed view: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to remove view: $id")
                onFailure(id)
            }
    }

    public override fun updView(viewLayout: ViewLayout, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        viewTable.document(viewLayout.id)
            .set(viewLayout)
            .addOnSuccessListener {
                println("successfully updated view: " + viewLayout.id)
                onSuccess(viewLayout.id)
            }
            .addOnFailureListener {
                println("failed to update view: " + viewLayout.id)
                onFailure(viewLayout.id)
            }
    }

    public override fun updView(id: String, viewLayout: ViewLayout, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        viewTable.document(id)
            .set(viewLayout)
            .addOnSuccessListener {
                println("successfully updated view: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to update view: $id")
                onFailure(id)
            }
    }

    public override suspend fun getViews(userID: String?, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) : List<ViewLayout> {
        if (userID.isNullOrBlank()) return emptyList()
        val userSnap = FirebaseProvider.firestore
            .collection("users")
            .document(userID)
            .get().await()
        val viewIds = userSnap.get("views") as? List<String> ?: emptyList()

        return coroutineScope {
            viewIds.map { id ->
                viewTable.document(id).get().await().toObject(ViewLayout::class.java)
            }
                .filterNotNull()
        }
    }

    public override fun getAllViews() : Flow<List<ViewLayout>> {
        return views
    }

    // firestore collection references //
    private lateinit var projectTable: CollectionReference
    private lateinit var issueTable: CollectionReference
    private lateinit var viewTable: CollectionReference
    private lateinit var userTable: CollectionReference
}