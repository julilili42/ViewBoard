package com.example.viewboard.backend.storageServer.impl

import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.LabelLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.storageServer.abstraction.StorageServerAPI
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object FirebaseAPI : StorageServerAPI() {
    public fun init() {
        val db = Firebase.firestore

        m_projectTable = db.collection("Projects")
        m_projects = m_projectTable.snapshots().map { it.toObjects<ProjectLayout>() }

        m_labelTable = db.collection("Labels")
        m_labels = m_labelTable.snapshots().map { it.toObjects<LabelLayout>() }

        m_issueTable = db.collection("Issues")
        m_issues = m_issueTable.snapshots().map { it.toObjects<IssueLayout>() }
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

    public override fun getProjects() : Flow<List<ProjectLayout>> {
        return m_projects
    }

    public override fun addLabel(labelLayout: LabelLayout) {
        m_labelTable.add(labelLayout)
            .addOnSuccessListener { ref ->
                println("success adding label: " + ref.id)
            }
            .addOnFailureListener {
                println("Failure adding label: ")
            }
    }

    public override fun rmLabel(labelLayout: LabelLayout) {
        // TODO: print warning or inform the user, if ref counter > 0

        m_labelTable.document(labelLayout.id).delete()
    }

    public override fun rmLabel(id: String) {
        // TODO: print warning or inform the user, if ref counter > 0

        m_labelTable.document(id).delete()
    }

    public override fun updLabel(labelLayout: LabelLayout) {
        m_labelTable.document(labelLayout.id).set(labelLayout)
    }

    public override fun updLabel(id: String, labelLayout: LabelLayout) {
        m_labelTable.document(id).set(labelLayout)
    }

    public override fun getLabels() : Flow<List<LabelLayout>> {
        return m_labels
    }

    public override fun addIssue(issueLayout: IssueLayout) {
        m_issueTable.add(issueLayout)
            .addOnSuccessListener { ref ->
                println("success adding issue: " + ref.id)
            }
            .addOnFailureListener {
                println("Failure adding issue: ")
            }
    }

    public override fun rmIssue(issueLayout: IssueLayout) {
        m_issueTable.document(issueLayout.id).delete()
    }

    public override fun rmIssue(id: String) {
        m_issueTable.document(id).delete()
    }

    public override fun updIssue(issueLayout: IssueLayout) {
        m_issueTable.document(issueLayout.id).set(issueLayout)
    }

    public override fun updIssue(id: String, issueLayout: IssueLayout) {
        m_issueTable.document(id).set(issueLayout)
    }

    public override fun getIssues() : Flow<List<IssueLayout>> {
        return m_issues
    }

    private lateinit var m_projectTable: CollectionReference
    private lateinit var m_labelTable: CollectionReference
    private lateinit var m_issueTable: CollectionReference
}