package com.example.viewboard.backend.storageServer.impl

import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.LabelLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.backend.storageServer.abstraction.StorageServerAPI
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

object FirebaseAPI : StorageServerAPI() {
    public fun init() {
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

    public override fun addProject(projectLayout: ProjectLayout) {
        m_projectTable.add(projectLayout)
            .addOnSuccessListener { ref ->
                println("success adding project: " + ref.id)
            }
            .addOnFailureListener {
                println("Failure adding project: ")
            }
    }

    public override fun rmProject(projectLayout: ProjectLayout) {
        m_projectTable.document(projectLayout.id).delete()
    }

    public override fun rmProject(id: String) {
        m_projectTable.document(id).delete()
    }

    public override fun updProject(projectLayout: ProjectLayout) {
        m_projectTable.document(projectLayout.id).set(projectLayout)
    }

    public override fun updProject(id: String, projectLayout: ProjectLayout) {
        m_projectTable.document(id).set(projectLayout)
    }

    public override suspend fun getProject(id: String) : ProjectLayout? {
        val snap = m_projectTable.document(id).get().await()
        return snap.toObject(ProjectLayout::class.java)
    }

    public override fun getProjects() : Flow<List<ProjectLayout>> {
        return m_projects
    }

    public override suspend fun addLabel(projID: String, labelLayout: LabelLayout) {
        val proj: ProjectLayout? = getProject(projID)

        m_labelTable.add(labelLayout)
            .addOnSuccessListener { ref ->
                proj!!.labels.add(ref.id)

                updProject(proj!!)

                println("success adding label: " + ref.id)
            }
            .addOnFailureListener {
                println("Failure adding label: ")
            }
    }

    public override suspend fun rmLabel(projID: String, labelLayout: LabelLayout) {
        // TODO: print warning or inform the user, if ref counter > 0

        val proj: ProjectLayout? = getProject(projID)

        proj!!.labels.remove(labelLayout.id)

        updProject(proj!!)

        m_labelTable.document(labelLayout.id).delete()
    }

    public override suspend fun rmLabel(projID: String, id: String) {
        // TODO: print warning or inform the user, if ref counter > 0

        val proj: ProjectLayout? = getProject(projID)

        proj!!.labels.remove(id)

        updProject(proj!!)

        m_labelTable.document(id).delete()
    }

    public override fun updLabel(labelLayout: LabelLayout) {
        m_labelTable.document(labelLayout.id).set(labelLayout)
    }

    public override fun updLabel(id: String, labelLayout: LabelLayout) {
        m_labelTable.document(id).set(labelLayout)
    }

    public override suspend fun getLabel(id: String) : LabelLayout? {
        val snap = m_labelTable.document(id).get().await()
        return snap.toObject(LabelLayout::class.java)
    }

    public override fun getLabels() : Flow<List<LabelLayout>> {
        return m_labels
    }

    public override fun getLabels(projID: String) : Flow<List<LabelLayout>> {
        return m_projectTable.document(projID).snapshots()
            .map { snap ->
                snap.toObject(ProjectLayout::class.java)?.labels ?: emptyList()
            }
            .distinctUntilChanged()
            .flatMapLatest { labels ->
                if (labels.isEmpty()) return@flatMapLatest flowOf(emptyList())
                val docFlows = labels.map { label ->
                    m_labelTable.document(label).snapshots()
                        .map { snap ->
                            snap.toObject(LabelLayout::class.java)
                        }
                }

                combine (docFlows) { docs ->
                    val map = docs.filterNotNull().associateBy { it.id }
                    labels.mapNotNull { map[it] }
                }
            }
    }

    public override suspend fun addIssue(projID: String, issueLayout: IssueLayout) {
        val proj: ProjectLayout? = getProject(projID)

        m_issueTable.add(issueLayout)
            .addOnSuccessListener { ref ->
                proj!!.issues.add(ref.id)

                updProject(proj!!)

                println("success adding issue: " + ref.id)
            }
            .addOnFailureListener {
                println("Failure adding issue: ")
            }
    }

    public override suspend fun rmIssue(projID: String, issueLayout: IssueLayout) {
        val proj: ProjectLayout? = getProject(projID)

        proj!!.issues.remove(issueLayout.id)

        updProject(proj!!)

        m_issueTable.document(issueLayout.id).delete()
    }

    public override suspend fun rmIssue(projID: String, id: String) {
        val proj: ProjectLayout? = getProject(projID)

        proj!!.issues.remove(id)

        updProject(proj!!)

        m_issueTable.document(id).delete()
    }

    public override fun updIssue(issueLayout: IssueLayout) {
        m_issueTable.document(issueLayout.id).set(issueLayout)
    }

    public override fun updIssue(id: String, issueLayout: IssueLayout) {
        m_issueTable.document(id).set(issueLayout)
    }

    public override suspend fun getIssue(id: String) : IssueLayout? {
        val snap = m_issueTable.document(id).get().await()
        return snap.toObject(IssueLayout::class.java)
    }

    public override fun getIssues() : Flow<List<IssueLayout>> {
        return m_issues
    }

    public override fun getIssues(projID: String) : Flow<List<IssueLayout>> {
        return m_projectTable.document(projID).snapshots()
            .map { snap ->
                snap.toObject(ProjectLayout::class.java)?.issues ?: emptyList()
            }
            .distinctUntilChanged()
            .flatMapLatest { issues ->
                if (issues.isEmpty()) return@flatMapLatest flowOf(emptyList())
                val docFlows = issues.map { issue ->
                    m_issueTable.document(issue).snapshots()
                        .map { snap ->
                            snap.toObject(IssueLayout::class.java)
                        }
                }

                combine (docFlows) { docs ->
                    val map = docs.filterNotNull().associateBy { it.id }
                    issues.mapNotNull { map[it] }
                }
            }
    }

    public override suspend fun addView(projID: String, viewLayout: ViewLayout) {
        val proj: ProjectLayout? = getProject(projID)

        m_viewTable.add(viewLayout)
            .addOnSuccessListener { ref ->
                proj!!.views.add(ref.id)

                updProject(proj!!)

                println("success adding view: " + ref.id)
            }
            .addOnFailureListener {
                println("Failure adding view: ")
            }
    }

    public override suspend fun rmView(projID: String, viewLayout: ViewLayout) {
        val proj: ProjectLayout? = getProject(projID)

        proj!!.views.remove(viewLayout.id)

        updProject(proj!!)

        m_viewTable.document(viewLayout.id).delete()
    }

    public override suspend fun rmView(projID: String, id: String) {
        val proj: ProjectLayout? = getProject(projID)

        proj!!.views.remove(id)

        updProject(proj!!)

        m_viewTable.document(id).delete()
    }

    public override fun updView(viewLayout: ViewLayout) {
        m_viewTable.document(viewLayout.id).set(viewLayout)
    }

    public override fun updView(id: String, viewLayout: ViewLayout) {
        m_viewTable.document(id).set(viewLayout)
    }

    public override suspend fun getView(id: String) : ViewLayout? {
        val snap = m_viewTable.document(id).get().await()
        return snap.toObject(ViewLayout::class.java)
    }

    public override fun getViews() : Flow<List<ViewLayout>> {
        return m_views
    }

    public override fun getViews(projID: String) : Flow<List<ViewLayout>> {
        return m_projectTable.document(projID).snapshots()
            .map { snap ->
                snap.toObject(ProjectLayout::class.java)?.views ?: emptyList()
            }
            .distinctUntilChanged()
            .flatMapLatest { views ->
                if (views.isEmpty()) return@flatMapLatest flowOf(emptyList())
                val docFlows = views.map { view ->
                    m_viewTable.document(view).snapshots()
                        .map { snap ->
                            snap.toObject(ViewLayout::class.java)
                        }
                }

                combine (docFlows) { docs ->
                    val map = docs.filterNotNull().associateBy { it.id }
                    views.mapNotNull { map[it] }
                }
            }
    }

    private lateinit var m_projectTable: CollectionReference
    private lateinit var m_labelTable: CollectionReference
    private lateinit var m_issueTable: CollectionReference
    private lateinit var m_viewTable: CollectionReference
}