package com.example.viewboard.backend.storageServer.impl

import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.LabelLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.backend.dataLayout.UserHelper
import com.example.viewboard.backend.storageServer.abstraction.StorageServerAPI
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.snapshots
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

        m_projectTable = db.collection("Projects")
        m_projects = m_projectTable.snapshots().map { it.toObjects<ProjectLayout>() }

        m_labelTable = db.collection("Labels")
        m_labels = m_labelTable.snapshots().map { it.toObjects<LabelLayout>() }

        m_issueTable = db.collection("Issues")
        m_issues = m_issueTable.snapshots().map { it.toObjects<IssueLayout>() }

        m_viewTable = db.collection("Views")
        m_views = m_viewTable.snapshots().map { it.toObjects<ViewLayout>() }
    }

    public override fun addProject(projectLayout: ProjectLayout, onSuccess: (String) -> Unit, onFailure: (ProjectLayout) -> Unit) {
        val uid = UserHelper.getUid() ?: return

        val projectWithUser = projectLayout.copy(
            creator = uid,
            users = arrayListOf(uid)
        )
        m_projectTable.add(projectWithUser)

            .addOnSuccessListener { ref ->
                println("successfully added project: " + ref.id)
                onSuccess(ref.id)
            }
            .addOnFailureListener {
                println("failed to add project")
                onFailure(projectLayout)
            }
    }

    fun getMyProjects(): Flow<List<ProjectLayout>> {
        val uid = UserHelper.getUid()
        return if (uid != null) {
            m_projects.map { projects ->
                projects.filter { it.users.contains(uid) }
            }
        } else {
            flowOf(emptyList())
        }
    }

    public override fun rmProject(id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        m_projectTable.document(id)
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
        m_projectTable.document(projectLayout.id)
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
        m_projectTable.document(id)
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
        val snap = m_projectTable.document(id)
            .get()
            .addOnSuccessListener {
                println("successfully retrieved project: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to retrieved project: $id")
                onFailure(id)
            }
            .await()

        return snap.toObject(ProjectLayout::class.java)
    }

    public override fun getProjects() : Flow<List<ProjectLayout>> {
        return m_projects
    }

    public override suspend fun addLabel(projID: String, labelLayout: LabelLayout, onSuccess: (String) -> Unit, onFailure: (LabelLayout) -> Unit) {
        val batch = Firebase.firestore.batch()
        val projREF = m_projectTable.document(projID)
        val labelREF = m_labelTable.document()

        batch.set(labelREF, labelLayout)

        batch.update(projREF, "labels", FieldValue.arrayUnion(labelREF.id))

        batch.commit()
            .addOnSuccessListener {
                println("successfully added label: " + labelREF.id)
                onSuccess(labelREF.id)
            }
            .addOnFailureListener {
                println("failed to add label")
                onFailure(labelLayout)
            }
    }

    public override suspend fun rmLabel(projID: String, id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        // TODO: print warning or inform the user, if ref counter > 0

        val projREF = m_projectTable.document(projID)
        val labelREF = m_labelTable.document(id)

        Firebase.firestore.runTransaction { transaction ->
            val projSnap = transaction.get(projREF)
            val issues = projSnap.get("issues") as? List<String> ?: emptyList()

            transaction.delete(labelREF)

            transaction.update(projREF, "labels", FieldValue.arrayRemove(id))

            for (issue in issues) {
                val issueREF = m_issueTable.document(issue)
                transaction.update(issueREF, "labels", FieldValue.arrayRemove(id))
            }

        }
            .addOnSuccessListener {
                println("successfully removed label: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to remove label: $id")
                onFailure(id)
            }
    }

    public override suspend fun addLabelToIssue(issueID: String, id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val batch = Firebase.firestore.batch()

        m_issueTable.document(issueID)
            .update("labels", FieldValue.arrayUnion(id))
            .addOnSuccessListener {
                println("successfully added label to issue: $issueID")
                onSuccess(issueID)
            }
            .addOnFailureListener {
                println("failed to add label to issue: $issueID")
                onFailure(issueID)
            }
    }

    public override suspend fun rmLabelFromIssue(issueID: String, id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val batch = Firebase.firestore.batch()

        m_issueTable.document(issueID)
            .update("labels", FieldValue.arrayRemove(id))
            .addOnSuccessListener {
                println("successfully removed label from issue: $issueID")
                onSuccess(issueID)
            }
            .addOnFailureListener {
                println("failed to remove label from issue: $issueID")
                onFailure(issueID)
            }
    }

    public override fun updLabel(labelLayout: LabelLayout, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        m_labelTable.document(labelLayout.id)
            .set(labelLayout)
            .addOnSuccessListener {
                println("successfully updated label: " + labelLayout.id)
                onSuccess(labelLayout.id)
            }
            .addOnFailureListener {
                println("failed to update label: " + labelLayout.id)
                onFailure(labelLayout.id)
            }
    }

    public override fun updLabel(id: String, labelLayout: LabelLayout, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        m_labelTable.document(id)
            .set(labelLayout)
            .addOnSuccessListener {
                println("successfully updated label: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to update label: $id")
                onFailure(id)
            }
    }

    public override suspend fun getLabel(id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) : LabelLayout? {
        val snap = m_labelTable.document(id)
            .get()
            .addOnSuccessListener {
                println("successfully retrieved label: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to retrieved label: $id")
                onFailure(id)
            }
            .await()

        return snap.toObject(LabelLayout::class.java)
    }

    public override fun getLabels() : Flow<List<LabelLayout>> {
        return m_labels
    }

    public override fun getLabels(projID: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) : Flow<List<LabelLayout>> {
        return m_projectTable.document(projID)
            .snapshots()
            .map { snap ->
                snap.toObject(ProjectLayout::class.java)?.labels ?: emptyList()
            }
            .distinctUntilChanged()
            .flatMapLatest { labels ->
                if (labels.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val docFlows = labels.map { label ->
                    m_labelTable.document(label)
                        .snapshots()
                        .map { snap ->
                            snap.toObject(LabelLayout::class.java)
                        }
                }

                combine (docFlows) { docs ->
                    val map = docs.filterNotNull().associateBy { it.id }
                    labels.mapNotNull { map[it] }
                }
            }
            .onCompletion {
                println("successfully retrieved issues from project: $projID")
                onSuccess(projID)
            }
            .catch {
                println("failed to retrieved issues from project: $projID")
                onFailure(projID)
            }
    }

    public override suspend fun addIssue(projID: String, issueLayout: IssueLayout, onSuccess: (String) -> Unit, onFailure: (IssueLayout) -> Unit) {
        val batch = Firebase.firestore.batch()
        val projREF = m_projectTable.document(projID)
        val issueREF = m_issueTable.document()

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
        val projREF = m_projectTable.document(projID)
        val viewREF = m_viewTable.document(viewID)
        val issueREF = m_issueTable.document()

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
        val projREF = m_projectTable.document(projID)
        val issueREF = m_issueTable.document(id)

        Firebase.firestore.runTransaction { transaction ->
            val projSnap = transaction.get(projREF)
            val views = projSnap.get("views") as? List<String> ?: emptyList()

            transaction.delete(issueREF)

            transaction.update(projREF, "issues", FieldValue.arrayRemove(id))

            for (view in views) {
                val viewREF = m_viewTable.document(view)
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

        m_viewTable.document(viewID)
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

        m_viewTable.document(viewID)
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
        m_issueTable.document(issueLayout.id)
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
        m_issueTable.document(id)
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
        val snap = m_issueTable.document(id)
            .get()
            .addOnSuccessListener {
                println("successfully retrieved issue: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to retrieved issue: $id")
                onFailure(id)
            }
            .await()

        return snap.toObject(IssueLayout::class.java)
    }

    fun getMyIssues(projID: String): Flow<List<IssueLayout>> {
        val uid = UserHelper.getUid()
        return getIssues(projID).map { issues ->
            if (uid != null) {
                issues.filter { it.assignments.contains(uid) || it.creator == uid }
            } else {
                emptyList()
            }
        }
    }


    public override fun getIssues() : Flow<List<IssueLayout>> {
        return m_issues
    }

    public override fun getIssuesFromView(viewID: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) : Flow<List<IssueLayout>> {
        return m_viewTable.document(viewID)
            .snapshots()
            .map { snap ->
                snap.toObject(ViewLayout::class.java)?.issues ?: emptyList()
            }
            .distinctUntilChanged()
            .flatMapLatest { issues ->
                if (issues.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val docFlows = issues.map { issue ->
                    m_issueTable.document(issue)
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
                println("failed to retrieved issues from view: $viewID")
                onFailure(viewID)
            }
    }

    public override fun getIssues(projID: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) : Flow<List<IssueLayout>> {
        return m_projectTable.document(projID)
            .snapshots()
            .map { snap ->
                snap.toObject(ProjectLayout::class.java)?.issues ?: emptyList()
            }
            .distinctUntilChanged()
            .flatMapLatest { issues ->
                if (issues.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val docFlows = issues.map { issue ->
                    m_issueTable.document(issue)
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
                println("failed to retrieved issues from project: $projID")
                onFailure(projID)
            }
    }

    public override suspend fun addView(projID: String, viewLayout: ViewLayout, onSuccess: (String) -> Unit, onFailure: (ViewLayout) -> Unit) {
        val batch = Firebase.firestore.batch()
        val projREF = m_projectTable.document(projID)
        val viewREF = m_viewTable.document()

        batch.set(viewREF, viewLayout)

        batch.update(projREF, "views", FieldValue.arrayUnion(viewREF.id))

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

    public override suspend fun rmView(projID: String, id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val batch = Firebase.firestore.batch()
        val projREF = m_projectTable.document(projID)
        val viewREF = m_viewTable.document(id)

        batch.delete(viewREF)

        batch.update(projREF, "views", FieldValue.arrayRemove(id))

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
        m_viewTable.document(viewLayout.id)
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
        m_viewTable.document(id)
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

    public override suspend fun getView(id: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) : ViewLayout? {
        val snap = m_viewTable.document(id)
            .get()
            .addOnSuccessListener {
                println("successfully retrieved view: $id")
                onSuccess(id)
            }
            .addOnFailureListener {
                println("failed to retrieved view: $id")
                onFailure(id)
            }
            .await()

        return snap.toObject(ViewLayout::class.java)
    }

    public override fun getViews() : Flow<List<ViewLayout>> {
        return m_views
    }

    public override fun getViews(projID: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) : Flow<List<ViewLayout>> {
        return m_projectTable.document(projID)
            .snapshots()
            .map { snap ->
                snap.toObject(ProjectLayout::class.java)?.views ?: emptyList()
            }
            .distinctUntilChanged()
            .flatMapLatest { views ->
                if (views.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val docFlows = views.map { view ->
                    m_viewTable.document(view)
                        .snapshots()
                        .map { snap ->
                            snap.toObject(ViewLayout::class.java)
                        }
                }

                combine (docFlows) { docs ->
                    val map = docs.filterNotNull().associateBy { it.id }
                    views.mapNotNull { map[it] }
                }
            }
            .onCompletion {
                println("successfully retrieved views from project: $projID")
                onSuccess(projID)
            }
            .catch {
                println("failed to retrieved views from project: $projID")
                onFailure(projID)
            }
    }

    private lateinit var m_projectTable: CollectionReference
    private lateinit var m_labelTable: CollectionReference
    private lateinit var m_issueTable: CollectionReference
    private lateinit var m_viewTable: CollectionReference
}